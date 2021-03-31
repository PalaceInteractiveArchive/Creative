package network.palace.creative.commands;

import com.plotsquared.bukkit.player.BukkitPlayer;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.api.PlotAPI;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import com.plotsquared.core.plot.PlotId;
import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.message.FormattedMessage;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
import network.palace.creative.handlers.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 1/21/16
 */
@CommandMeta(description = "Creator Project", rank = Rank.SETTLER)
public class CreatorCommand extends CoreCommand {
    private static final FormattedMessage msg = new FormattedMessage("Learn how to join The Creator Project: ")
            .color(ChatColor.YELLOW).style(ChatColor.BOLD).then("https://palnet.us/creator").color(ChatColor.AQUA)
            .link("https://palnet.us/creator").tooltip(ChatColor.GREEN + "Click to visit https://palnet.us/creator");
    private static final PlotAPI api = new PlotAPI();

    public CreatorCommand() {
        super("creator");
    }

    @Override
    protected void handleCommand(CPlayer p, String[] args) throws CommandException {
        Player player = p.getBukkitPlayer();
        PlayerData data = Creative.getInstance().getPlayerData(player.getUniqueId());
        if (!data.isCreator() && p.getRank().getRankId() < Rank.COORDINATOR.getRankId()) {
            msg.send(player);
            return;
        }

        switch (args.length) {
            case 1: {
                switch (args[0].toLowerCase()) {
                    case "plot": {
                        List<Plot> plots = new ArrayList<>(PlotPlayer.wrap(player).getPlots("creator"));
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
                        if (p.getRank().getRankId() < Rank.COORDINATOR.getRankId()) {
                            helpMenu(player, p.getRank());
                            return;
                        }
                        List<String> names = Core.getMongoHandler().getCreatorMembers();
                        names.sort(String.CASE_INSENSITIVE_ORDER);
                        if (names.isEmpty()) {
                            player.sendMessage(ChatColor.RED + "There is no one in The Creator Project!");
                        } else {
                            StringBuilder s = new StringBuilder(ChatColor.GREEN + "The Creator Project Members:\n");
                            for (int i = 0; i < names.size(); i++) {
                                String name = names.get(i);
                                if (i == (names.size() - 1)) {
                                    s.append(name);
                                    continue;
                                }
                                s.append(name).append(", ");
                            }
                            player.sendMessage(s.toString());
                        }
                        return;
                    }
                }
                helpMenu(player, p.getRank());
                return;
            }
            case 3: {
                if (args[0].equalsIgnoreCase("set")) {
                    if (p.getRank().getRankId() < Rank.COORDINATOR.getRankId()) {
                        helpMenu(player, p.getRank());
                        return;
                    }
                    String username = args[1];
                    boolean value = Boolean.parseBoolean(args[2]);
                    UUID uuid;
                    Player tp = Bukkit.getPlayer(username);
                    if (tp == null) {
                        uuid = Core.getMongoHandler().usernameToUUID(username);
                        if (uuid == null) {
                            player.sendMessage(ChatColor.RED + "Player not found!");
                            return;
                        }
                    } else {
                        username = tp.getName();
                        uuid = tp.getUniqueId();
                        if (value) {
                            tp.sendMessage(ChatColor.GREEN + "You are now part of The Creator Project!");
                        } else {
                            tp.sendMessage(ChatColor.RED + "You are no longer a part of The Creator Project!");
                        }
                        Creative.getInstance().getPlayerData(uuid).setCreator(value);
                    }
                    Core.getMongoHandler().setCreativeValue(uuid, "creator", value);
                    if (value) {
                        player.sendMessage(ChatColor.GREEN + username + " is now part of The Creator Project!");
                    } else {
                        player.sendMessage(ChatColor.RED + username + " is no longer a part of The Creator Project!");
                    }
                    return;
                }
                helpMenu(player, p.getRank());
                return;
            }
        }
        helpMenu(player, p.getRank());
    }

    public void giveCreatorPlot(final Player player) {
        player.closeInventory();
        final long time = System.currentTimeMillis();
        player.sendMessage(ChatColor.GREEN + "Finding you a plot right now...");
        BukkitPlayer plr = (BukkitPlayer) PlotPlayer.wrap(player);
        final String worldname = "creator";
        PlotArea plotarea = PlotSquared.get().getPlotArea(worldname, worldname);
        plotarea.setMeta("lastPlot", new PlotId(0, 0));
        while (true) {
            PlotId start = getNextPlotId(getLastPlotId(plotarea), 1);
            PlotId end = new PlotId(start.getX(), start.getY());
            plotarea.setMeta("lastPlot", start);
            if (plotarea.canClaim(plr, start, end)) {
                for (int i = start.getX(); i <= end.getX(); i++) {
                    for (int j = start.getY(); j <= end.getY(); j++) {
                        Plot plot = plotarea.getPlotAbs(new PlotId(i, j));
                        boolean teleport = i == end.getX() && j == end.getY();
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
        int absX = Math.abs(id.getX());
        int absY = Math.abs(id.getY());
        if (absX > absY) {
            if (id.getX() > 0) {
                return new PlotId(id.getX(), id.getY() + 1);
            } else {
                return new PlotId(id.getX(), id.getY() - 1);
            }
        } else if (absY > absX) {
            if (id.getY() > 0) {
                return new PlotId(id.getX() - 1, id.getY());
            } else {
                return new PlotId(id.getX() + 1, id.getY());
            }
        } else {
            if (id.getX() == id.getY() && id.getX() > 0) {
                return new PlotId(id.getX(), id.getY() + step);
            }
            if (id.getX() == absX) {
                return new PlotId(id.getX(), id.getY() + 1);
            }
            if (id.getY() == absY) {
                return new PlotId(id.getX(), id.getY() - 1);
            }
            return new PlotId(id.getX() + 1, id.getY());
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
