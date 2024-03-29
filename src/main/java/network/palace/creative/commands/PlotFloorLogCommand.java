package network.palace.creative.commands;

import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
import network.palace.creative.utils.PlotFloorUtil.LogSection;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

@CommandMeta(aliases = {"pfl"}, description = "Check the last time a person set the floor of their plot.", rank = Rank.CM)
public class PlotFloorLogCommand extends CoreCommand {

    public PlotFloorLogCommand() {
        super("plotfloorlog");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Player only.");
            return;
        }

        Player player = (Player) sender;
        Plot plot = PlotPlayer.wrap(player).getCurrentPlot();
        if (plot == null) {
            player.sendMessage(ChatColor.RED + "You must be on a plot to do that.");
            return;
        }

        LogSection log = Creative.getInstance().getPlotFloorUtil().getLog(plot.getOwners().iterator().next());
        if (log == null) {
            player.sendMessage(ChatColor.GREEN + "No log found for this plot.");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "Plot floor last changed to " + log.getBlock().toString() + " at " + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date(log.getTimeStamp())) + " EST.");
    }
}
