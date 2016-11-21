package us.mcmagic.creative.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.creative.Creative;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;

/**
 * Created by Marc on 4/13/15
 */
public class Commandloglag implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        Rank rank = MCMagicCore.getUser(player.getUniqueId()).getRank();
        if (rank.getRankId() != Rank.EMPEROR.getRankId()) {
            return true;
        }
        Creative.redstoneListener.toggleForPlayer(player);
        return true;
    }
}
