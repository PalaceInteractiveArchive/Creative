package us.mcmagic.creative.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.creative.Creative;
import us.mcmagic.creative.handlers.CreativeInventoryType;

/**
 * Created by Marc on 12/27/15
 */
public class Commandshop implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Creative.menuUtil.openMenu((Player) sender, CreativeInventoryType.CREATIVESHOP);
        return true;
    }
}
