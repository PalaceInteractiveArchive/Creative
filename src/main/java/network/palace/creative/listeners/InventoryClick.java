package network.palace.creative.listeners;

import network.palace.creative.Creative;
import network.palace.creative.handlers.BannerInventoryType;
import network.palace.creative.handlers.CreativeInventoryType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.io.IOException;

/**
 * Created by Marc on 6/12/15
 */
public class InventoryClick implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getClickedInventory();
        if (inv == null || inv.getTitle() == null) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        String title = ChatColor.stripColor(inv.getTitle());
        if (title.startsWith("Manage Plot")) {
            Creative.menuUtil.handleClick(event, CreativeInventoryType.MANAGE_PLOT);
        } else if (title.startsWith("Added Players")) {
            Creative.menuUtil.handleClick(event, CreativeInventoryType.ADDED_PLAYERS);
        } else if (title.startsWith("Denied Players")) {
            Creative.menuUtil.handleClick(event, CreativeInventoryType.DENIED_PLAYERS);
        } else if (title.startsWith("Add Player to Plot")) {
            Creative.menuUtil.handleClick(event, CreativeInventoryType.ADD_PLAYER);
        } else if (title.startsWith("Heads - ")) {
            Creative.headUtil.handleClick(event);
        } else if (title.contains(" Action")) {
            try {
                Creative.showManager.handle(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        if (title.startsWith("Edit Show File Page ")) {
            try {
                Creative.showManager.handle(event);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        switch (title) {
            case "Resource Pack":
                Creative.resourceUtil.handle(event);
                break;
            case "Creative Menu":
                Creative.menuUtil.handleClick(event, CreativeInventoryType.MAIN);
                break;
            case "Creative Shop":
                Creative.menuUtil.handleClick(event, CreativeInventoryType.CREATIVESHOP);
                break;
            case "Building Plots":
                Creative.menuUtil.handleClick(event, CreativeInventoryType.BUILDING_PLOTS);
                break;
            case "Heads":
                Creative.menuUtil.handleClick(event, CreativeInventoryType.HEADSHOP);
                break;
            case "My Plots":
                Creative.menuUtil.handleClick(event, CreativeInventoryType.MY_PLOTS);
                break;
            case "Plot Settings":
                Creative.menuUtil.handleClick(event, CreativeInventoryType.PLOT_SETTINGS);
                break;
            case "Select Base Color":
                Creative.bannerUtil.handle(event, BannerInventoryType.SELECT_BASE);
                break;
            case "Add Layer":
                Creative.bannerUtil.handle(event, BannerInventoryType.ADD_LAYER);
                break;
            case "Particle Menu":
                Creative.menuUtil.handleClick(event, CreativeInventoryType.PARTICLE);
                break;
            case "Choose Layer Color":
                Creative.bannerUtil.handle(event, BannerInventoryType.LAYER_COLOR);
                break;
            case "Change Biome":
                Creative.menuUtil.handleClick(event, CreativeInventoryType.CHANGE_BIOME);
            case "Select Color":
            case "Select Fade":
            case "Set Power":
            case "Select Type":
            case "Select Track":
            case "Select Particle":
            case "Set Music":
                try {
                    Creative.showManager.handle(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}