package network.palace.creative.listeners;

import network.palace.creative.Creative;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Created by Marc on 1/4/17.
 */
public class PlayerMove implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getTo().getBlockY() <= 0) {
            player.teleport(Creative.getInstance().getSpawn());
            return;
        }
    }
}
