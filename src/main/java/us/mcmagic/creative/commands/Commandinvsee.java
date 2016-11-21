package us.mcmagic.creative.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.player.PlayerUtil;

/**
 * Created by Marc on 3/27/15
 */
public class Commandinvsee implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED
                    + "Only players can use this command!");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            Player tp = PlayerUtil.findPlayer(args[0]);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }
            player.sendMessage(ChatColor.GREEN + "Now looking in "
                    + tp.getName() + "'s Inventory!");
            player.openInventory(tp.getInventory());
            return true;
        }
        player.sendMessage(ChatColor.RED + "/invsee [Username]");
        return true;
    }
}