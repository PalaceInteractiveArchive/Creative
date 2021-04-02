package network.palace.creative.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Created by Marc on 2/6/15
 */
@CommandMeta(description = "Broadcast to the whole server", rank = Rank.CM)
public class BroadcastCommand extends CoreCommand {

    public BroadcastCommand() {
        super("bc");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "/bc" + " [Message]");
            return;
        }
        StringBuilder message = new StringBuilder();
        for (String s : args) {
            message.append(s).append(" ");
        }
        Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.AQUA + "Information" + ChatColor.WHITE + "] " +
                ChatColor.GREEN + ChatColor.translateAlternateColorCodes('&', message.toString()));
    }
}
