package network.palace.creative.listeners;

import com.github.intellectualsites.plotsquared.bukkit.events.PlayerEnterPlotEvent;
import com.github.intellectualsites.plotsquared.bukkit.events.PlayerLeavePlotEvent;
import com.github.intellectualsites.plotsquared.plot.flag.BooleanFlag;
import com.github.intellectualsites.plotsquared.plot.flag.FlagManager;
import com.github.intellectualsites.plotsquared.plot.object.Plot;
import network.palace.creative.Creative;
import network.palace.creative.utils.MenuUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerPlotListener implements Listener {

    @EventHandler
    public void enter(PlayerEnterPlotEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Creative.getInstance(), () -> {
            Player player = event.getPlayer();
            Plot plot = event.getPlot();
            if (plot.getOwners().contains(player.getUniqueId()) || MenuUtil.isStaff(player)) {
                return;
            }

            boolean flight = plot.getFlag((BooleanFlag) FlagManager.getFlag("flight"), true);
            player.setAllowFlight(flight);
        });
    }

    @EventHandler
    public void leave(PlayerLeavePlotEvent event) {
        event.getPlayer().setAllowFlight(true);
    }
}
