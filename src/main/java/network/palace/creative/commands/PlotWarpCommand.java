package network.palace.creative.commands;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
import network.palace.creative.handlers.Warp;
import network.palace.creative.utils.MenuUtil;
import network.palace.creative.utils.PlotWarpUtil;
import org.bukkit.ChatColor;

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
                if (player.getRank() != Rank.HONORABLE && !MenuUtil.isStaff(player.getBukkitPlayer())) {
                    player.sendMessage(ChatColor.RED + "You must be Honorable+ to use this command.");
                    return;
                }

                switch (args[1].toLowerCase()) {
                    case "register":
                        PlotAPI plotAPI = new PlotAPI();
                        Plot plot = plotAPI.getPlot(player.getBukkitPlayer());
                        if (plot == null || !new ArrayList<>(plot.getOwners()).contains(player.getUniqueId())) {
                            player.sendMessage(ChatColor.RED + "You must be in a plot you own or co-own.");
                            return;
                        }

                        Pattern pattern = Pattern.compile("[^a-zA-Z0-9_]");
                        Matcher matcher = pattern.matcher(name);
                        if (matcher.find()) {
                            player.sendMessage(ChatColor.RED + "Warp names can only contain letters, numbers and underscores (_).");
                            return;
                        }

                        if (plotWarpUtil.getWarp(name).isPresent() || plotWarpUtil.getPendingWarp(name).isPresent()) {
                            player.sendMessage(ChatColor.RED + "A warp with that name has already been submitted.");
                            return;
                        }

                        try {
                            plotWarpUtil.submitWarp(args[0], player.getBukkitPlayer());
                            player.sendMessage(ChatColor.GREEN + "Your warp has been submitted. We will review it soon.");
                        }
                        catch (IOException e) {
                            player.sendMessage(ChatColor.RED + "An error has occurred. Please alert a dev!");
                            e.printStackTrace();
                        }

                        return;
                    case "delete":
                        Optional<Warp> warp = plotWarpUtil.getWarp(args[0]).filter(w -> plotWarpUtil.getWarpOwner(w).equals(player.getUniqueId()) || MenuUtil.isStaff(player.getBukkitPlayer()));
                        if (warp.isPresent()) {
                            plotWarpUtil.removeWarp(warp.get());
                            player.sendMessage(ChatColor.GREEN + "Warp deleted.");
                            return;
                        }

                        player.sendMessage(ChatColor.RED + "That warp either does not exist or it is not your warp to delete.");
                        return;
                    default:
                        player.sendMessage(ChatColor.RED + "/plotwarp [warp] <register | delete>");
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
