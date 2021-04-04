package network.palace.creative.commands;

import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.message.FormattedMessage;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
import network.palace.creative.handlers.PlayerData;
import network.palace.creative.show.Show;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by Marc on 12/11/15
 */
@CommandMeta(description = "Show Manager", rank = Rank.GUEST)
public class ShowCommand extends CoreCommand {
    private final FormattedMessage msg = new FormattedMessage("[Show] ").color(ChatColor.BLUE)
            .then("Purchase the Show Creator in the Creative Shop to use this! ").color(ChatColor.YELLOW)
            .then("Click here to open the Shop").color(ChatColor.AQUA).style(ChatColor.BOLD).tooltip(ChatColor.GREEN +
                    "Open the Creative Shop").command("/shop");

    public ShowCommand() {
        super("show");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Creative creative = Creative.getInstance();
        PlayerData data = creative.getPlayerData(player.getUniqueId());
        if (!data.hasShowCreator()) {
            msg.send(player);
            return;
        }
        if (args.length == 0) {
            helpMenu(player);
            return;
        }
        String action = args[0];
        switch (action.toLowerCase()) {
            case "start": {
                Bukkit.getScheduler().runTaskAsynchronously(creative, () -> {
                    if (args.length > 1 || creative.getShowManager().getMaxShowAmount(player) == 1 || creative.getShowManager().getTotalShows(player) == 1) {
                        if (args.length > 1) {
                            List<String> showName = new ArrayList<>(Arrays.asList(args));
                            showName.remove(0);
                            player.setMetadata("showname", new FixedMetadataValue(Creative.getInstance(), ChatColor.stripColor(String.join(" ", showName))));
                        } else {
                            File shows = new File("plugins/Creative/shows/" + player.getUniqueId().toString());
                            File[] files = shows.listFiles();
                            if (files == null) {
                                creative.getShowManager().messagePlayer(player, ChatColor.RED + "You have not created any shows yet.");
                                return;
                            }

                            player.setMetadata("showname", new FixedMetadataValue(Creative.getInstance(), files[0].getName().replace(".show", "")));
                        }

                        Show show = creative.getShowManager().startShow(player);
                        if (show != null && show.getNameColored() != null) {
                            creative.getShowManager().messagePlayer(player, "Your show " + ChatColor.AQUA + show.getNameColored() +
                                    ChatColor.GREEN + " has started!");
                        } else {
                            creative.getShowManager().messagePlayer(player, "Error starting your show! (Did you create one yet?)");
                        }

                        player.removeMetadata("showname", Creative.getInstance());
                    } else {
                        creative.getShowManager().messagePlayer(player, ChatColor.RED + "Please specify the name of your show.");
                    }
                });
                return;
            }
            case "stop": {
                if (creative.getShowManager().stopShow(player.getUniqueId())) {
                    creative.getShowManager().messagePlayer(player, "Your show has stopped!");
                } else {
                    creative.getShowManager().messagePlayer(player, ChatColor.RED +
                            "There was an error stopping your show! (Maybe it wasn't running?)");
                }
                return;
            }
            case "edit": {
                if (args.length > 1 || creative.getShowManager().getMaxShowAmount(player) == 1 || creative.getShowManager().getTotalShows(player) == 1) {
                    File shows = new File("plugins/Creative/shows/" + player.getUniqueId().toString());
                    File[] files = shows.listFiles();
                    if (files == null) {
                        creative.getShowManager().messagePlayer(player, ChatColor.RED + "You have not created any shows yet.");
                        return;
                    }

                    String name = files[0].getName().replace(".show", "");
                    if (args.length > 1) {
                        List<String> showName = new ArrayList<>(Arrays.asList(args));
                        showName.remove(0);
                        String temp = String.join(" ", showName).replace("\\W", " ");
                        Optional<File> file = Stream.of(files).filter(f -> f.getName().contains(temp)).findFirst();
                        if (file.isEmpty()) {
                            player.sendMessage(ChatColor.RED + "You do not have a show named: " + temp);
                            return;
                        }

                        name = ChatColor.stripColor(temp);
                    }

                    File file = new File(Creative.getInstance().getDataFolder(), "shows/" + player.getUniqueId().toString() + "/" + name + ".show");
                    if (!file.exists()) {
                        player.sendMessage(ChatColor.RED + "A show with that name does not exist.");
                        return;
                    }

                    Plot plot = PlotPlayer.wrap(player.getBukkitPlayer()).getCurrentPlot();
                    if (plot == null || !player.getUniqueId().equals(plot.getOwners().iterator().next())) {
                        player.sendMessage(ChatColor.RED + "You must be on your own plot to edit this show.");
                        return;
                    }

                    creative.getShowManager().editShow(player, 1, new Show(file, player, plot));
                } else {
                    creative.getShowManager().selectShow(player);
                }

                return;
            }
            case "reload": {
                if (player.getRank().getRankId() < Rank.CM.getRankId()) {
                    helpMenu(player);
                    return;
                }
                creative.getShowManager().loadTracks();
                player.sendMessage(ChatColor.GREEN + "Audio tracks reloaded!");
                return;
            }
            default:
                helpMenu(player);
        }
    }

    private void helpMenu(CPlayer player) {
        player.sendMessage(ChatColor.GREEN + "Show Commands:");
        player.sendMessage(ChatColor.GREEN + "/show start [name]" + ChatColor.AQUA + "- Start your coded Show");
        player.sendMessage(ChatColor.GREEN + "/show stop " + ChatColor.AQUA + "- Stop your coded Show");
        player.sendMessage(ChatColor.GREEN + "/show edit [name]" + ChatColor.AQUA + "- Edit your Show");
        if (player.getRank().getRankId() >= Rank.CM.getRankId()) {
            player.sendMessage(ChatColor.GREEN + "/show reload " + ChatColor.AQUA + "- Reload track list");
        }
    }
}
