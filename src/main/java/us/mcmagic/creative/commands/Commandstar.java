package us.mcmagic.creative.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import us.mcmagic.creative.listeners.PlayerJoinAndLeave;

/**
 * Created by Marc on 6/27/16
 */
public class Commandstar implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        PlayerInventory inv = player.getInventory();
        inv.remove(Material.NETHER_STAR);
        inv.setItem(8, PlayerJoinAndLeave.star);
        player.sendMessage(ChatColor.GREEN + "You have been given the Creative Menu again!");
        return true;
    }
}