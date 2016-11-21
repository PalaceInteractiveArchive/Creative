package us.mcmagic.creative.commands;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import net.minecraft.server.v1_8_R3.MojangsonParseException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.creative.Creative;

/**
 * Created by Marc on 8/7/15
 */
public class Commandmanage implements CommandExecutor {

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        Plot plot = new PlotAPI(Creative.getInstance()).getPlot(player.getLocation());
        if (plot == null) {
            player.sendMessage(ChatColor.RED + "You're not standing on a Plot!");
            return true;
        }
        try {
            Creative.menuUtil.openManagePlot(player, plot);
        } catch (MojangsonParseException e) {
            e.printStackTrace();
        }
        return true;
    }
}