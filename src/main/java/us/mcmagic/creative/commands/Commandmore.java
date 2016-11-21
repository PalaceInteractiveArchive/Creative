package us.mcmagic.creative.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Commandmore implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only Players can use this command!");
            return true;
        }
        Player player = (Player) sender;
        PlayerInventory pi = player.getInventory();
        if (pi.getItemInHand() == null || pi.getItemInHand().getType().equals(Material.AIR)) {
            player.sendMessage(ChatColor.RED + "There is nothing in your hand!");
            return true;
        }
        ItemStack stack = pi.getItemInHand();
        stack.setAmount(64);
        return true;
    }
}