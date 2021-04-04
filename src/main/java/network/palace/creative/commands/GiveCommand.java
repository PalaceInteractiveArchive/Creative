package network.palace.creative.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandMeta(description = "Give yourself an item", aliases = {"item", "i"}, rank = Rank.CM)
public class GiveCommand extends CoreCommand {

    public GiveCommand() {
        super("give");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            try {
                Material mat = Material.matchMaterial(args[1].toUpperCase());
                if (mat != null) {
                    ItemStack item = new ItemStack(mat, 64);
                    player.getInventory().addItem(item);
                    player.sendMessage(ChatColor.GRAY
                            + "Giving 64 of "
                            + item.getType().toString().toLowerCase()
                            .replaceAll("_", " "));
                    return;
                }
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "/give [Username] [Item] [Amount]");
                return;
            }
            return;
        }
        if (args.length == 2) {
            Player tp = Bukkit.getPlayer(args[0]);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            try {
                Material mat = Material.getMaterial(args[1].toUpperCase());
                if (mat != null) {
                    ItemStack item = new ItemStack(mat, 64);
                    tp.getInventory().addItem(item);
                    player.sendMessage(ChatColor.GRAY
                            + "Giving 64 of "
                            + item.getType().toString().toLowerCase()
                            .replaceAll("_", " ") + " to "
                            + tp.getName());
                }
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "/give [Username] [Item] [Amount]");
                return;
            }
        }
        if (args.length == 3) {
            Player tp = Bukkit.getPlayer(args[0]);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            if (!isInt(args[2])) {
                player.sendMessage(ChatColor.RED + "/give [Username] [Item] [Amount]");
                return;
            }
            int amount = Integer.parseInt(args[2]);
            try {
                Material mat = Material.getMaterial(args[1].toUpperCase());
                if (mat != null) {
                    ItemStack item = new ItemStack(mat, amount);
                    tp.getInventory().addItem(item);
                    player.sendMessage(ChatColor.GRAY
                            + "Giving "
                            + amount
                            + " of "
                            + item.getType().toString().toLowerCase()
                            .replaceAll("_", " ") + " to "
                            + tp.getName());
                }
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "/give [Username] [Item] [Amount]");
            }
            return;
        }
        player.sendMessage(ChatColor.RED + "/give [Username] [Item] [Amount]");
    }

    private static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }
}