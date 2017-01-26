package network.palace.creative.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Marc on 2/6/15
 */
@CommandMeta(description = "Send a message", aliases = {"tell", "whisper"})
@CommandPermission(rank = Rank.KNIGHT)
public class Commandmsg extends CoreCommand {

    public Commandmsg() {
        super("msg");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "/msg [Player] [Message]");
                return;
            }
            String msg = "";
            for (int i = 1; i < args.length; i++) {
                msg += args[i] + " ";
            }
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                return;
            }
            player.sendMessage(ChatColor.AQUA + "" + ChatColor.translateAlternateColorCodes('&', msg));
            return;
        }
        sender.sendMessage(ChatColor.RED + "Whoa, what'd you do? Stahp it!");
    }
}
