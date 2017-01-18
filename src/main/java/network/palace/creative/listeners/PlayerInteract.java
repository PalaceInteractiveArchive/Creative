package network.palace.creative.listeners;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import network.palace.creative.Creative;
import network.palace.creative.handlers.CreativeInventoryType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 8/7/15
 */
public class PlayerInteract implements Listener {

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Creative.getInstance().getPlayerData(player.getUniqueId()).resetAction();
        if (event.getAction().equals(Action.PHYSICAL)) {
            return;
        }
        if (player.getItemInHand().getType().equals(Material.NETHER_STAR) && player.getItemInHand().getItemMeta()
                != null && player.getItemInHand().getItemMeta().getDisplayName() != null &&
                player.getItemInHand().getItemMeta().getDisplayName().startsWith(ChatColor.AQUA + "Creative")) {
            Creative.getInstance().getMenuUtil().openMenu(player, CreativeInventoryType.MAIN);
            event.setCancelled(true);
            return;
        }
        if (!event.getAction().name().toLowerCase().contains("block")) {
            return;
        }
        if (!event.getClickedBlock().getType().name().toLowerCase().contains("sign")) {
            return;
        }
        Sign s = (Sign) event.getClickedBlock().getState();
        if (s.getLine(0).equals(ChatColor.BLUE + "[Show]")) {
            PlotAPI api = new PlotAPI(Creative.getInstance());
            Plot plot = api.getPlot(s.getLocation());
            if (plot != null) {
                List<UUID> owners = new ArrayList<>(plot.getOwners());
                Player owner = Bukkit.getPlayer(owners.get(0));
                if (owner != null) {
                    Creative.getInstance().getShowManager().syncMusic(player, plot, owner);
                }
            }
            return;
        }
        if (!s.getLine(0).equals(ChatColor.BLUE + "[Plot]")) {
            return;
        }
        event.setCancelled(true);
        if (!player.getWorld().getName().equals("spawn")) {
            return;
        }
        List<Plot> plots = new ArrayList<>(new PlotAPI(Creative.getInstance()).getPlayerPlots(player));
        for (Plot p : plots) {
            if (p.getArea().worldname.equalsIgnoreCase("plotworld")) {
                player.sendMessage(ChatColor.RED +
                        "You already claimed your Free Plot! To get a second, you must purchase it in /menu.");
                return;
            }
        }
        Creative.getInstance().getMenuUtil().givePlot(player, true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getHeldItemSlot() == 8) {
            event.setCancelled(true);
        }
        Creative.getInstance().getPlayerData(player.getUniqueId()).resetAction();
    }

    @EventHandler
    public void onPlayerBucketFill(PlayerBucketEmptyEvent event) {
        if (!event.getBucket().name().toLowerCase().contains("water")) {
            event.setCancelled(true);
        }
    }
}