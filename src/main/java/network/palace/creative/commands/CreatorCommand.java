package network.palace.creative.commands;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotArea;
import com.intellectualcrafters.plot.object.PlotId;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.plotsquared.bukkit.util.BukkitUtil;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.message.FormattedMessage;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.player.RankTag;
import network.palace.creative.Creative;
import network.palace.creative.handlers.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marc on 1/21/16
 */
@CommandMeta(description = "Creator Project", rank = Rank.SETTLER)
public class CreatorCommand extends CoreCommand {
    private static FormattedMessage msg = new FormattedMessage("Learn how to join The Creator Project: ")
            .color(ChatColor.YELLOW).style(ChatColor.BOLD).then("https://palnet.us/creator").color(ChatColor.AQUA)
            .link("https://palnet.us/creator").tooltip(ChatColor.GREEN + "Click to visit https://palnet.us/creator");
    private static PlotAPI api = new PlotAPI();

    public CreatorCommand() {
        super("creator");
    }

    @Override
    protected void handleCommand(CPlayer p, String[] args) throws CommandException {
        Player player = p.getBukkitPlayer();
        PlayerData data = Creative.getInstance().getPlayerData(player.getUniqueId());
        if (!p.hasTag(RankTag.CREATOR) && p.getRank().getRankId() < Rank.COORDINATOR.getRankId()) {
            msg.send(player);
            return;
        }

        if (args.length >= 1) {
            switch (args[0].toLowerCase()) {
                case "plot": {
                    List<Plot> plots = new ArrayList<>(api.getPlayerPlots(Bukkit.getWorld("creator"), player));
                    if (plots.isEmpty()) {
                        giveCreatorPlot(player);
                    } else {
                        Plot plot = plots.get(0);
                        Location loc = Creative.getInstance().getMenuUtil().getHome(plot);
                        player.teleport(loc);
                    }
                    return;
                }
                case "list": {
                    player.sendMessage(ChatColor.AQUA + "This command has changed! To list members of the creator project, run " + ChatColor.YELLOW + "/perm tag creator members");
                    return;
                }
                case "set": {
                    player.sendMessage(ChatColor.AQUA + "This command has changed! To add/remove members from the creator project, use " +
                            ChatColor.YELLOW + "/perm player [username] addtag creator" + ChatColor.AQUA + " and " + ChatColor.YELLOW + "/perm player [username] removetag creator");
                    return;
                }
            }
        }
        helpMenu(player, p.getRank());
    }

    public void giveCreatorPlot(final Player player) {
        player.closeInventory();
        final long time = System.currentTimeMillis();
        player.sendMessage(ChatColor.GREEN + "Finding you a plot right now...");
        PlotPlayer plr = BukkitUtil.getPlayer(player);
        final String worldname = "creator";
        PlotArea plotarea = PS.get().getPlotArea(worldname, worldname);
        plotarea.setMeta("lastPlot", new PlotId(0, 0));
        while (true) {
            PlotId start = getNextPlotId(getLastPlotId(plotarea), 1);
            PlotId end = new PlotId(start.x, start.y);
            plotarea.setMeta("lastPlot", start);
            if (plotarea.canClaim(plr, start, end)) {
                for (int i = start.x; i <= end.x; i++) {
                    for (int j = start.y; j <= end.y; j++) {
                        Plot plot = plotarea.getPlotAbs(new PlotId(i, j));
                        boolean teleport = i == end.x && j == end.y;
                        plot.claim(plr, teleport, null);
                    }
                }
                break;
            }
        }
        player.sendMessage(ChatColor.GREEN + "Here's your Plot! Get to it with /menu. " + ChatColor.DARK_AQUA +
                "(Took " + (System.currentTimeMillis() - time) + "ms)");
    }

    public static PlotId getNextPlotId(PlotId id, int step) {
        int absX = Math.abs(id.x);
        int absY = Math.abs(id.y);
        if (absX > absY) {
            if (id.x > 0) {
                return new PlotId(id.x, id.y + 1);
            } else {
                return new PlotId(id.x, id.y - 1);
            }
        } else if (absY > absX) {
            if (id.y > 0) {
                return new PlotId(id.x - 1, id.y);
            } else {
                return new PlotId(id.x + 1, id.y);
            }
        } else {
            if (id.x == id.y && id.x > 0) {
                return new PlotId(id.x, id.y + step);
            }
            if (id.x == absX) {
                return new PlotId(id.x, id.y + 1);
            }
            if (id.y == absY) {
                return new PlotId(id.x, id.y - 1);
            }
            return new PlotId(id.x + 1, id.y);
        }
    }

    public PlotId getLastPlotId(PlotArea area) {
        PlotId value = (PlotId) area.getMeta("lastPlot");
        if (value == null) {
            value = new PlotId(0, 0);
            area.setMeta("lastPlot", value);
            return value;
        }
        return value;
    }

    private void helpMenu(Player player, Rank rank) {
        player.sendMessage(ChatColor.GREEN + "The Creator Project Commands:");
        player.sendMessage(ChatColor.GREEN + "/creator plot " + ChatColor.AQUA + "- Bring you to your Creator Plot");
        if (rank.getRankId() >= Rank.COORDINATOR.getRankId()) {
            player.sendMessage(ChatColor.GREEN + "/creator set [Username] [true/false] " + ChatColor.AQUA +
                    "- Add or remove a Guest from The Creator Project");
            player.sendMessage(ChatColor.GREEN + "/creator list " + ChatColor.AQUA +
                    "- List the members of The Creator Project.");
        }
    }
}
