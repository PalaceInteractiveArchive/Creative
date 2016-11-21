package us.mcmagic.creative.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 4/13/15
 */
public class RedstoneListener implements Listener {
    private List<UUID> uuids = new ArrayList<>();

    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent event) {
        if (uuids.isEmpty()) {
            return;
        }
        for (UUID uuid : new ArrayList<>(this.uuids)) {
            try {
                Block b = event.getBlock();
                Location loc = b.getLocation();
                Bukkit.getPlayer(uuid).sendMessage("Redstone Event: Type=" + b.getType().name() + " Location=" +
                        loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ());
            } catch (NullPointerException e) {
                uuids.remove(uuid);
            }
        }
    }

    public void toggleForPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (uuids.contains(uuid)) {
            uuids.remove(uuid);
            player.sendMessage(ChatColor.RED + "Removed from Log Lag Mode!");
            return;
        }
        player.sendMessage(ChatColor.RED + "Added to Log Lag Mode!");
        uuids.add(uuid);
    }
}
