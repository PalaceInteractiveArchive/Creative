package network.palace.creative.commands;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
import org.bukkit.ChatColor;

/**
 * Created by Marc on 8/7/15
 */
@CommandMeta(description = "Manage another player's Plot", rank = Rank.DEVELOPER)
public class ManageCommand extends CoreCommand {

    public ManageCommand() {
        super("manage");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Plot plot = new PlotAPI().getPlot(player.getLocation());
        if (plot == null) {
            player.sendMessage(ChatColor.RED + "You're not standing on a Plot!");
            return;
        }
        Creative.getInstance().getMenuUtil().openManagePlot(player.getBukkitPlayer(), plot);
    }
}
