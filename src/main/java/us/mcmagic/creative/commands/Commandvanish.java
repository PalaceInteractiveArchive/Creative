package us.mcmagic.creative.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.creative.utils.VanishUtil;

/**
 * Created by Marc on 12/14/14
 */
public class Commandvanish implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            if (VanishUtil.isVanished(player)) {
                VanishUtil.removeFromVanish(player);
            } else {
                VanishUtil.addToVanish(player);
            }
            return true;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                player.sendMessage(VanishUtil.vanishedPlayers());
                return true;
            }
            if (args[0].equalsIgnoreCase("check")) {
                if (VanishUtil.isVanished(player)) {
                    player.sendMessage(ChatColor.DARK_AQUA + "You are vanished.");
                } else {
                    player.sendMessage(ChatColor.DARK_AQUA + "You are not vanished.");
                }
            }
        }
        return true;
    }
}