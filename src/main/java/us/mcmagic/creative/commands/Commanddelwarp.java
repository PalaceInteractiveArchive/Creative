package us.mcmagic.creative.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.creative.Creative;

/**
 * Created by Marc on 2/8/15
 */
public class Commanddelwarp implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "/delwarp [Warp]");
            return true;
        }
        if (Creative.removeWarp(args[0])) {
            player.sendMessage(ChatColor.GRAY + "Warp " + args[0] + " removed!");
            return true;
        }
        player.sendMessage(ChatColor.RED + "Warp not found!");
        return true;
    }
}
