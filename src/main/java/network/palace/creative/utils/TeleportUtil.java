package network.palace.creative.utils;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Marc on 3/27/15
 */
public class TeleportUtil {
    private HashMap<UUID, Location> locations = new HashMap<>();

    public void log(Player player, Location location) {
        CPlayer cPlayer = Core.getPlayerManager().getPlayer(player);
        if (cPlayer == null) {
            player.sendMessage(ChatColor.RED + "An error has occurred. Please try again later.");
            return;
        }

        if (cPlayer.getRank().getRankId() < Rank.MOD.getRankId()) {
            return;
        }
        if (locations.containsKey(player.getUniqueId())) {
            locations.remove(player.getUniqueId());
        }
        locations.put(player.getUniqueId(), location);
    }

    public void logout(UUID uuid) {
        locations.remove(uuid);
    }

    public boolean back(CPlayer player) {
        return back(player.getBukkitPlayer());
    }

    public boolean back(Player player) {
        if (!locations.containsKey(player.getUniqueId())) {
            return false;
        }
        final Location loc = player.getLocation();
        player.teleport(locations.get(player.getUniqueId()));
        log(player, loc);
        return true;
    }
}
