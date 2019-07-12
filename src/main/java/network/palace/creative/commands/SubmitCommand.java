package network.palace.creative.commands;

import com.github.intellectualsites.plotsquared.plot.object.Plot;
import com.github.intellectualsites.plotsquared.plot.object.PlotPlayer;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
import network.palace.creative.utils.CreativeRank;
import org.bukkit.ChatColor;

@CommandMeta(description = "Submit a plot for review", rank = Rank.SETTLER)
public class SubmitCommand extends CoreCommand {

    public SubmitCommand() {
        super("submit");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (CreativeRank.isValidWorld(player)) {
            player.sendMessage(ChatColor.RED + "You are not in a valid world to run this command.");
            return;
        }

        Plot plot = PlotPlayer.wrap(player.getBukkitPlayer()).getCurrentPlot();
        if (!plot.getOwners().contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You must be on your own plot.");
            return;
        }

        Creative.getInstance().getPlotReview().addPending(player.getUniqueId(), plot.getId(), player.getWorld());
        player.sendMessage(ChatColor.GREEN + "Plot submitted. You will be notified when it has been processed.");
    }
}
