package us.mcmagic.creative.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.creative.Creative;
import us.mcmagic.creative.handlers.CreativeInventoryType;

/**
 * Created by Marc on 7/29/15
 */
public class Commandmenu implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        player.sendMessage(ChatColor.GREEN + "Opening Creative Menu...");
        Creative.menuUtil.openMenu(player, CreativeInventoryType.MAIN);
        return true;
    }
}