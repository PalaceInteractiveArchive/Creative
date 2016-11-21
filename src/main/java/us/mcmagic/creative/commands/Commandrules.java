package us.mcmagic.creative.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.chat.formattedmessage.FormattedMessage;

/**
 * Created by Marc on 12/27/15
 */
public class Commandrules implements CommandExecutor {
    private FormattedMessage msg = new FormattedMessage("\nClick to read the Creative Rules on our Website!\n")
            .color(ChatColor.YELLOW).style(ChatColor.BOLD).link("https://mcmagic.us/creative/rules/");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        msg.send((Player) sender);
        return true;
    }
}