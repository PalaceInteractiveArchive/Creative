package us.mcmagic.creative.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.player.PlayerUtil;

/**
 * Created by Marc on 2/6/15
 */
public class Commandmsg implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "/" + label + " [Player] [Message]");
                return true;
            }
            String msg = "";
            for (int i = 1; i < args.length; i++) {
                msg += args[i] + " ";
            }
            Player player = PlayerUtil.findPlayer(args[0]);
            if (player == null) {
                return true;
            }
            player.sendMessage(ChatColor.AQUA + "" + ChatColor.translateAlternateColorCodes('&', msg));
            return true;
        }
        sender.sendMessage(ChatColor.RED + "Whoa, what'd you do? Stahp it!");
        return true;
    }
}
