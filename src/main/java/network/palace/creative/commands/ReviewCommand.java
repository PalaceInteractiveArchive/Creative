package network.palace.creative.commands;

import com.github.intellectualsites.plotsquared.plot.object.PlotId;
import com.github.intellectualsites.plotsquared.plot.object.PlotPlayer;
import java.util.Optional;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
import network.palace.creative.plotreview.PlotReview;
import network.palace.creative.plotreview.PlotReviewData;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;

@CommandMeta(description = "Submit a plot for review", rank = Rank.SRMOD)
public class ReviewCommand extends CoreCommand {

    public ReviewCommand() {
        super("review");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        PlotReview plotReview = Creative.getInstance().getPlotReview();
        if (args.length == 0) {
            plotReview.openMenu(player, 1);
            return;
        }

        String[] msg = new String[args.length - 1];
        System.arraycopy(args, 1, msg, 0, args.length - 1);
        String message = StringUtils.join(msg, " ");
        if (ChatColor.stripColor(message).split(" ").length <= 8) {
            player.sendMessage(ChatColor.RED + "Reason to short, please be more specific.");
            return;
        }

        PlotId plot = PlotPlayer.wrap(player.getBukkitPlayer()).getCurrentPlot().getId();
        Optional<PlotReviewData> data = plotReview.getPlotReviewData(plot);
        if (!data.isPresent()) {
            player.sendMessage(ChatColor.RED + "This plot has not been submitted yet.");
            return;
        }

        if (args[0].toLowerCase().equals("accept")) {
            plotReview.accept(plot, message);
        }
        else if (args[0].toLowerCase().equals("deny")) {
            plotReview.deny(plot, message);
        }
    }
}
