package network.palace.creative.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.ItemUtil;
import network.palace.creative.Creative;
import network.palace.creative.handlers.BannerInventoryType;
import network.palace.creative.handlers.CreativeInventoryType;
import network.palace.creative.handlers.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Marc on 6/12/15
 */
public class InventoryClick implements Listener {
    private List<Material> clickBlacklist = Arrays.asList(Material.MOB_SPAWNER, Material.PORTAL, Material.ENDER_PORTAL,
            Material.DRAGON_EGG, Material.COMMAND, Material.COMMAND_CHAIN, Material.COMMAND_REPEATING,
            Material.COMMAND_MINECART, Material.BARRIER, Material.END_GATEWAY, Material.END_CRYSTAL,
            Material.STRUCTURE_BLOCK, Material.STRUCTURE_VOID);
    private ItemStack air = ItemUtil.create(Material.AIR);

    @EventHandler
    public void onInventoryCreative(InventoryCreativeEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer((Player) event.getWhoClicked());
        if (player == null)
            return;
        if (player.getRank().getRankId() >= Rank.TRAINEE.getRankId())
            return;
        ItemStack cursor = event.getCursor();
        if (cursor == null)
            return;
        Material type = cursor.getType();
        if (type != null && clickBlacklist.contains(type)) {
            event.setCancelled(true);
            event.setCursor(air);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        long t = System.currentTimeMillis();
        Inventory inv = event.getClickedInventory();

        if (inv == null || inv.getTitle() == null) {
            return;
        }
        if (event.getWhoClicked() != null) {
            if (event.getWhoClicked() instanceof Player) {
                PlayerData data = Creative.getInstance().getPlayerData(event.getWhoClicked().getUniqueId());
                if (data != null) {
                    data.resetAction();
                }
            }
        }

        String title = ChatColor.stripColor(inv.getTitle());
        if (title.startsWith("Manage Plot")) {
            Creative.getInstance().getMenuUtil().handleClick(event, CreativeInventoryType.MANAGE_PLOT);
        } else if (title.startsWith("Added Players")) {
            Creative.getInstance().getMenuUtil().handleClick(event, CreativeInventoryType.ADDED_PLAYERS);
        } else if (title.startsWith("Denied Players")) {
            Creative.getInstance().getMenuUtil().handleClick(event, CreativeInventoryType.DENIED_PLAYERS);
        } else if (title.startsWith("Add Player to Plot")) {
            Creative.getInstance().getMenuUtil().handleClick(event, CreativeInventoryType.ADD_PLAYER);
        } else if (title.startsWith("Heads - ")) {
            Creative.getInstance().getHeadUtil().handleClick(event);
        } else if (title.contains(" Action")) {
            try {
                Creative.getInstance().getShowManager().handle(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (title.startsWith("Edit Show File Page ")) {
            try {
                Creative.getInstance().getShowManager().handle(event);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        switch (title) {
            case "Resource Pack":
                Creative.getInstance().getResourceUtil().handle(event);
                break;
            case "Creative Menu":
                Creative.getInstance().getMenuUtil().handleClick(event, CreativeInventoryType.MAIN);
                break;
            case "Creative Shop":
                Creative.getInstance().getMenuUtil().handleClick(event, CreativeInventoryType.CREATIVESHOP);
                break;
            case "Building Plots":
                Creative.getInstance().getMenuUtil().handleClick(event, CreativeInventoryType.BUILDING_PLOTS);
                break;
            case "Heads":
                Creative.getInstance().getMenuUtil().handleClick(event, CreativeInventoryType.HEADSHOP);
                break;
            case "My Plots":
                Creative.getInstance().getMenuUtil().handleClick(event, CreativeInventoryType.MY_PLOTS);
                break;
            case "Plot Settings":
                Creative.getInstance().getMenuUtil().handleClick(event, CreativeInventoryType.PLOT_SETTINGS);
                break;
            case "Select Base Color":
                Creative.getInstance().getBannerUtil().handle(event, BannerInventoryType.SELECT_BASE);
                break;
            case "Add Layer":
                Creative.getInstance().getBannerUtil().handle(event, BannerInventoryType.ADD_LAYER);
                break;
            case "Particle Menu":
                Creative.getInstance().getMenuUtil().handleClick(event, CreativeInventoryType.PARTICLE);
                break;
            case "Choose Layer Color":
                Creative.getInstance().getBannerUtil().handle(event, BannerInventoryType.LAYER_COLOR);
                break;
            case "Change Biome":
                Creative.getInstance().getMenuUtil().handleClick(event, CreativeInventoryType.CHANGE_BIOME);
            case "Select Colors":
            case "Select Fade Colors":
            case "Set Power":
            case "Select Type":
            case "Select Track":
            case "Select Particle":
            case "Set Music":
                try {
                    Creative.getInstance().getShowManager().handle(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        ItemStack item = event.getCurrentItem();
        String name = "";
        if (item != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.getDisplayName() != null) {
                name = ChatColor.stripColor(meta.getDisplayName());
            }
        }
        long t2 = System.currentTimeMillis();
        long diff = t2 - t;
        if (diff >= 500) {
            for (CPlayer cp : Core.getPlayerManager().getOnlinePlayers()) {
                if (cp == null)
                    continue;
                if (cp.getRank().getRankId() >= Rank.DEVELOPER.getRankId()) {
                    cp.sendMessage(ChatColor.RED + "Click event took " + diff + "ms! " + ChatColor.GREEN +
                            event.getWhoClicked().getName() + " " + title + " ");
                }
            }
        }
    }
}
