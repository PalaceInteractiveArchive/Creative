package network.palace.creative.listeners;

import network.palace.creative.Creative;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Created by Marc on 1/4/17.
 */
public class PlayerMove implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo().getBlockY() <= 0) {
            event.getPlayer().teleport(Creative.getInstance().getSpawn());
        }
    }
}
