package us.mcmagic.creative.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.creative.Creative;
import us.mcmagic.creative.handlers.Warp;

/**
 * Created by Marc on 2/8/15
 */
public class Commandsetwarp implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "");
            return true;
        }
        Player player = (Player) sender;
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "/setwarp [Warp]");
            return true;
        }
        Location loc = player.getLocation();
        Warp warp = new Warp(args[0], loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(),
                loc.getWorld().getName());
        Creative.createWarp(warp);
        player.sendMessage(ChatColor.GRAY + "Warp " + args[0] + " set!");
        return true;
    }
}
