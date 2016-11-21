package us.mcmagic.creative.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.creative.Creative;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.PlayerUtil;

/**
 * Created by Marc on 2/8/15
 */
public class Commandspawn implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "/spawn [Player]");
                return true;
            }
            Player player = PlayerUtil.findPlayer(args[0]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }
            player.teleport(Creative.getSpawn());
            return true;
        }
        Player player = (Player) sender;
        if (MCMagicCore.getUser(player.getUniqueId()).getRank().getRankId() < Rank.KNIGHT.getRankId()) {
            player.teleport(Creative.getSpawn());
            return true;
        }
        if (args.length != 1) {
            Creative.teleportUtil.log(player, player.getLocation());
            player.teleport(Creative.getSpawn());
            return true;
        }
        Player tp = PlayerUtil.findPlayer(args[0]);
        if (tp == null) {
            player.sendMessage(ChatColor.RED + "/spawn [Player]");
            return true;
        }
        tp.teleport(Creative.getSpawn());
        player.sendMessage(ChatColor.GRAY + tp.getName() + " has teleported to Spawn!");
        return true;
    }
}
