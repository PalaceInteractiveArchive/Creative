package us.mcmagic.creative.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import us.mcmagic.creative.Creative;

/**
 * Created by Marc on 12/14/14
 */
public class Commandhead implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(ChatColor.BLUE + "Reloading Head Database...");
        Creative.headUtil.update();
        sender.sendMessage(ChatColor.BLUE + "Head Database reloaded!");
        return true;
    }
}
