package network.palace.creative.listeners;

import org.bukkit.event.Listener;

public class PlayerPlotListener implements Listener {

    //    @EventHandler
//    public void enter(PlayerEnterPlotEvent event) {
//        Bukkit.getScheduler().scheduleSyncDelayedTask(Creative.getInstance(), () -> {
//            Player player = event.getPlayer();
//            Plot plot = event.getPlot();
//            if (plot.getOwners().contains(player.getUniqueId()) || MenuUtil.isStaff(player)) {
//                return;
//            }
//
//            boolean flight = plot.getFlag((BooleanFlag) FlagManager.getFlag("flight"), true);
//            player.setAllowFlight(flight);
//        });
//    }
//
//    @EventHandler
//    public void leave(PlayerLeavePlotEvent event) {
//        event.getPlayer().setAllowFlight(true);
//    }
}
