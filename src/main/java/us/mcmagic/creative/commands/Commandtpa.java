package us.mcmagic.creative.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.creative.utils.TpaUtil;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.PlayerUtil;

/**
 * Created by Marc on 2/6/15
 */
public class Commandtpa implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "/tpa [Player]");
            return true;
        }
        Player tp = PlayerUtil.findPlayer(args[0]);
        if (tp == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }
        if (MCMagicCore.getUser(player.getUniqueId()).getRank().getRankId() < Rank.SQUIRE.getRankId()) {
            if (MCMagicCore.getUser(tp.getUniqueId()).getRank().getRankId() > Rank.SQUIRE.getRankId()) {
                player.sendMessage(ChatColor.RED + "You can't send this player a TPA Request!");
                return true;
            }
        }
        TpaUtil.addTeleport(player, tp);
        return true;
    }
}