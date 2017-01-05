package network.palace.creative.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Created by Marc on 1/3/17.
 */
public class WorldListener implements Listener {
    private static boolean plotsLoaded = false;
    private static boolean creatorLoaded = false;

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        World world = event.getWorld();
        world.setTime(2000);
        String name = world.getName();
        switch (name.toLowerCase()) {
            case "plotworld":
                plotsLoaded = true;
                break;
            case "creator":
                creatorLoaded = true;
                break;
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getTo().getWorld().getUID().equals(event.getFrom().getWorld().getUID())) {
            return;
        }
        String current = event.getFrom().getWorld().getName();
        String target = event.getTo().getWorld().getName();
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (current.equalsIgnoreCase("spawn")) {
            player.setGamemode(GameMode.CREATIVE);
        } else if (target.equalsIgnoreCase("spawn") && player.getRank().getRankId() < Rank.SQUIRE.getRankId()) {
            player.setGamemode(GameMode.ADVENTURE);
            PlayerInventory inv = player.getInventory();
            inv.remove(Material.ELYTRA);
            if (inv.getChestplate().getType().equals(Material.ELYTRA)) {
                inv.setChestplate(new ItemStack(Material.AIR));
            }
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        World world = event.getWorld();
        for (Entity e : event.getChunk().getEntities()) {
            if (!EntitySpawn.allowed.contains(e.getType())) {
                e.remove();
            }
        }
    }

    public static boolean isAllLoaded() {
        return plotsLoaded && creatorLoaded;
    }
}
