package us.mcmagic.creative.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.creative.Creative;
import us.mcmagic.creative.handlers.BannerInventoryType;

/**
 * Created by Marc on 6/12/15
 */
public class Commandbanner implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        Creative.bannerUtil.openMenu(player, BannerInventoryType.SELECT_BASE);
        return true;
    }
}
