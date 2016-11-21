package us.mcmagic.creative.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

/**
 * Created by Marc on 2/8/15
 */
public class Commandhelpop implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            String message = "";
            for (String arg : args) {
                message += arg + " ";
            }
            if (sender instanceof Player) {
                Player player = (Player) sender;
                for (User user : MCMagicCore.getUsers()) {
                    if (user.getRank().getRankId() >= Rank.KNIGHT.getRankId()) {
                        Bukkit.getPlayer(user.getUniqueId()).sendMessage(ChatColor.DARK_RED + "[CM CHAT] " +
                                ChatColor.GRAY + player.getName() + ": " + ChatColor.WHITE +
                                ChatColor.translateAlternateColorCodes('&', message));
                    }
                }
                return true;
            }
            for (User user : MCMagicCore.getUsers()) {
                if (user.getRank().getRankId() >= Rank.KNIGHT.getRankId()) {
                    Bukkit.getPlayer(user.getUniqueId()).sendMessage(ChatColor.DARK_RED + "[CM CHAT] " + ChatColor.GRAY
                            + "Console" + ": " + ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', message));
                }
            }
            return true;
        }
        sender.sendMessage(ChatColor.RED + "/" + label + " [message]");
        return true;
    }
}
