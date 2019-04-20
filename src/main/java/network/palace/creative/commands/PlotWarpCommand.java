package network.palace.creative.commands;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
import network.palace.creative.handlers.Warp;
import network.palace.creative.utils.PlotWarpUtil;
import org.bukkit.ChatColor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

@CommandMeta(description = "Player submitted warps to plots.", rank = Rank.SETTLER)
public class PlotWarpCommand extends CoreCommand {

    public PlotWarpCommand() {
        super("plotwarp");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Creative plugin = Creative.getInstance();
        PlotWarpUtil plotWarpUtil = plugin.getPlotWarpUtil();
        if (args.length > 0) {
            String name = args[0];
            if (args.length > 1) {
                if (!args[1].equalsIgnoreCase("register")) {
                    player.sendMessage(ChatColor.RED + "/plotwarp [warp] register");
                    return;
                }

                if (player.getRank().getRankId() < Rank.HONORABLE.getRankId()) {
                    player.sendMessage(ChatColor.RED + "You must be Honorable+ to use this command.");
                    return;
                }

                PlotAPI plotAPI = new PlotAPI();
                Plot plot = plotAPI.getPlot(player.getBukkitPlayer());
                if (plot == null || !new ArrayList<>(plot.getOwners()).contains(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "You must be in a plot you own or co-own.");
                    return;
                }

                if (plotWarpUtil.getWarp(name).isPresent() || plotWarpUtil.getPendingWarp(name).isPresent()) {
                    player.sendMessage(ChatColor.RED + "A warp with that name has already been submitted.");
                    return;
                }

                try {
                    plotWarpUtil.submitWarp(args[0], player.getBukkitPlayer());
                    player.sendMessage(ChatColor.GREEN + "Your warp has been submitted. We will review it soon.");
                } catch (IOException e) {
                    player.sendMessage(ChatColor.RED + "An error has occurred. Please alert a dev!");
                    e.printStackTrace();
                }

                return;
            }

            Optional<Warp> warp = plotWarpUtil.getWarp(name);
            if (warp.isPresent()) {
                Warp w = warp.get();
                player.teleport(w.getLocation());
                player.sendMessage(ChatColor.GREEN + "You have been warped to " + ChatColor.GOLD + w.getName());
                return;
            }

            player.sendMessage(ChatColor.RED + "A warp with that name does not exist.");
            return;
        }

        plotWarpUtil.openWarpsMenu(player.getBukkitPlayer(), 1);
    }
}
