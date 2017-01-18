package network.palace.creative.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
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
@CommandMeta(description = "Set a warp location")
@CommandPermission(rank = Rank.KNIGHT)
public class CommandSetWarp extends CoreCommand {

    public CommandSetWarp() {
        super("setwarp");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        Player player = (Player) sender;
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "/setwarp [Warp]");
            return;
        }
        Location loc = player.getLocation();
        Warp warp = new Warp(args[0], loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(),
                loc.getWorld().getName());
        Creative.getInstance().createWarp(warp);
        player.sendMessage(ChatColor.GRAY + "Warp " + args[0] + " set!");
    }
}
