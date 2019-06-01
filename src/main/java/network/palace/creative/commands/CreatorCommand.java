package network.palace.creative.commands;

import com.github.intellectualsites.plotsquared.plot.PlotSquared;
import com.github.intellectualsites.plotsquared.plot.object.Plot;
import com.github.intellectualsites.plotsquared.plot.object.PlotArea;
import com.github.intellectualsites.plotsquared.plot.object.PlotId;
import com.github.intellectualsites.plotsquared.plot.object.PlotPlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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

/**
 * Created by Marc on 1/21/16
 */
@CommandMeta(description = "Creator Project", rank = Rank.SETTLER)
public class CreatorCommand extends CoreCommand {
    private static FormattedMessage msg = new FormattedMessage("Learn how to join The Creator Project: ")
            .color(ChatColor.YELLOW).style(ChatColor.BOLD).then("https://palace.network/cc").color(ChatColor.AQUA)
            .link("https://palace.network/cc").tooltip(ChatColor.GREEN + "Click to visit https://palace.network/cc");

    public CreatorCommand() {
        super("creator");
    }

    @Override
    protected void handleCommand(CPlayer p, String[] args) throws CommandException {
        Player player = p.getBukkitPlayer();
        PlayerData data = Creative.getInstance().getPlayerData(player.getUniqueId());
        if (!data.isCreator() && p.getRank().getRankId() < Rank.SRMOD.getRankId()) {
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
                        if (p.getRank().getRankId() < Rank.SRMOD.getRankId()) {
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
                switch (args[0].toLowerCase()) {
                    case "set": {
                        if (p.getRank().getRankId() < Rank.SRMOD.getRankId()) {
                            helpMenu(player, p.getRank());
                            return;
                        }
                        String username = args[1];
                        Boolean value = Boolean.valueOf(args[2]);
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
                    case "settag": {
                        if (p.getRank().getRankId() < Rank.SRMOD.getRankId()) {
                            helpMenu(player, p.getRank());
                            return;
                        }
                        String username = args[1];
                        Boolean value = Boolean.valueOf(args[2]);
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
                                tp.sendMessage(ChatColor.GREEN + "You now have The Creator Tag!");
                            } else {
                                tp.sendMessage(ChatColor.RED + "You no longer have The Creator Tag!");
                            }
                            Creative.getInstance().getPlayerData(uuid).setCreatorTag(value);
                        }
                        Core.getMongoHandler().setCreativeValue(uuid, "creatortag", value);
                        if (value) {
                            player.sendMessage(ChatColor.GREEN + username + " now has The Creator Tag!");
                        } else {
                            player.sendMessage(ChatColor.RED + username + " no longer has The Creator Tag!");
                        }
                        return;
                    }
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
        PlotPlayer plr = PlotPlayer.wrap(player);
        final String worldname = "creator";
        PlotArea plotarea = PlotSquared.get().getPlotArea(worldname, worldname);
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
        if (rank.getRankId() >= Rank.SRMOD.getRankId()) {
            player.sendMessage(ChatColor.GREEN + "/creator settag [Username] [true/false] " + ChatColor.AQUA +
                    "- Add or remove The Creator Tag from a Guest");
            player.sendMessage(ChatColor.GREEN + "/creator set [Username] [true/false] " + ChatColor.AQUA +
                    "- Add or remove a Guest from The Creator Project");
            player.sendMessage(ChatColor.GREEN + "/creator list " + ChatColor.AQUA +
                    "- List the members of The Creator Project.");
        }
    }
}
