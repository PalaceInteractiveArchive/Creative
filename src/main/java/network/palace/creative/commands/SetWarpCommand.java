package network.palace.creative.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
import network.palace.creative.handlers.Warp;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Marc on 2/8/15
 */
@CommandMeta(description = "Set a warp location", rank = Rank.CM)
public class SetWarpCommand extends CoreCommand {

    public SetWarpCommand() {
        super("setwarp");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        Player player = (Player) sender;
        Rank rank = Rank.GUEST;
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "/setwarp [Warp] <rank>");
            return;
        }

        if (args.length > 1) {
            try {
                rank = Rank.fromString(args[1]);
            } catch (IllegalArgumentException e) {
                sender.sendMessage(args[1] + " is not a valid rank.");
                return;
            }
        }
        Location loc = player.getLocation();
        Warp warp = new Warp(args[0], loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(),
                loc.getWorld().getName(), rank);
        Creative.getInstance().createWarp(warp);
        player.sendMessage(ChatColor.GRAY + "Warp " + args[0] + " set!");
        if (!rank.equals(Rank.GUEST)) {
            player.sendMessage(ChatColor.RED + "This warp is restricted to " + rank.getFormattedName() + ChatColor.RED + " and up!");
        }
    }
}
