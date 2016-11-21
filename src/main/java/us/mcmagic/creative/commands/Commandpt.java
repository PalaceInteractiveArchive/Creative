package us.mcmagic.creative.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.creative.Creative;
import us.mcmagic.creative.handlers.CreativeInventoryType;

/**
 * Created by Marc on 9/6/15
 */
public class Commandpt implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this!");
        }
        Player player = (Player) sender;
        Creative.menuUtil.openMenu(player, CreativeInventoryType.PARTICLE);
        return true;
    }
}