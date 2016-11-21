package us.mcmagic.creative.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by Marc on 2/6/15
 */
public class Commandbc implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "/" + label + " [Message]");
            return true;
        }
        String message = "";
        for (String s : args) {
            message += s + " ";
        }
        Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.AQUA + "Information" + ChatColor.WHITE + "] " +
                ChatColor.GREEN + ChatColor.translateAlternateColorCodes('&', message));
        return true;
    }
}
