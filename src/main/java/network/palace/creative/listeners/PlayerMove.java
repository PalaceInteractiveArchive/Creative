package network.palace.creative.listeners;

import com.intellectualcrafters.plot.flag.BooleanFlag;
import com.intellectualcrafters.plot.flag.FlagManager;
import com.intellectualcrafters.plot.object.Plot;
import network.palace.creative.Creative;
import network.palace.creative.utils.MenuUtil;
import org.bukkit.Location;
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
        Location location = event.getTo();
        Player player = event.getPlayer();
        if (event.getTo().getBlockY() <= 0) {
            player.teleport(Creative.getInstance().getSpawn());
            return;
        }

        if (!player.isFlying()) {
            return;
        }

        boolean flight;
        Plot plot = Plot.getPlot(new com.intellectualcrafters.plot.object.Location(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        if (plot == null) {
            flight = true;
        }
        else {
            if (plot.getOwners().contains(player.getUniqueId()) || MenuUtil.isStaff(player)) {
                flight = true;
            }
            else {
                BooleanFlag flag = (BooleanFlag) FlagManager.getFlag("flight");
                flight = plot.getFlag(flag, true);
            }
        }

        player.setAllowFlight(flight);
    }
}
