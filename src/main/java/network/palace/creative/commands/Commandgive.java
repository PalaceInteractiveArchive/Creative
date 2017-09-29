package network.palace.creative.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandMeta(description = "Give yourself an item", aliases = {"item", "i"})
@CommandPermission(rank = Rank.MOD)
public class Commandgive extends CoreCommand {

    public Commandgive() {
        super("give");
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED
                    + "Only players can use this command!");
            return;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            if (!isInt(args[0])) {
                try {
                    if (args[0].contains(":")) {
                        String[] list = args[0].split(":");
                        int id;
                        byte data;
                        if (list != null) {
                            id = Integer.parseInt(list[0]);
                            data = Byte.parseByte(list[1]);
                        } else {
                            id = Integer.parseInt(args[0]);
                            data = (byte) 0;
                        }
                        ItemStack item = new ItemStack(id, 64, data);
                        player.getInventory().addItem(item);
                        player.sendMessage(ChatColor.GRAY
                                + "Giving 64 of "
                                + item.getType().toString().toLowerCase()
                                .replaceAll("_", " "));
                        return;
                    }
                    Material mat = Material.getMaterial(args[0].toUpperCase());
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
                    player.sendMessage(ChatColor.RED + "/give" + " [Numeric ID] [Username] [Amount]");
                    return;
                }
                return;
            } else {
                String[] list;
                if (args[0].contains(":")) {
                    list = args[0].split(":");
                } else {
                    list = null;
                }
                try {
                    int id;
                    byte data;
                    if (list != null) {
                        id = Integer.parseInt(list[0]);
                        data = Byte.parseByte(list[1]);
                    } else {
                        id = Integer.parseInt(args[0]);
                        data = (byte) 0;
                    }
                    ItemStack item = new ItemStack(id, 64, data);
                    player.getInventory().addItem(item);
                    player.sendMessage(ChatColor.GRAY
                            + "Giving 64 of "
                            + item.getType().toString().toLowerCase()
                            .replaceAll("_", " "));
                } catch (Exception e) {
                    player.sendMessage(ChatColor.RED + "There was an error, sorry!");
                }
                return;
            }
        }
        if (args.length == 2) {
            Player tp = Bukkit.getPlayer(args[1]);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            if (!isInt(args[0])) {
                try {
                    Material mat = Material.getMaterial(args[0].toUpperCase());
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
                    player.sendMessage(ChatColor.RED + "/give"
                            + " [Numeric ID] [Username] [Amount]");
                    return;
                }
            } else {
                String[] list;
                if (args[0].contains(":")) {
                    list = args[0].split(":");
                } else {
                    list = null;
                }
                try {
                    int id;
                    byte data;
                    if (list != null) {
                        id = Integer.parseInt(list[0]);
                        data = Byte.parseByte(list[1]);
                    } else {
                        id = Integer.parseInt(args[0]);
                        data = (byte) 0;
                    }
                    ItemStack item = new ItemStack(id, 64, data);
                    tp.getInventory().addItem(item);
                    player.sendMessage(ChatColor.GRAY
                            + "Giving 64 of "
                            + item.getType().toString().toLowerCase()
                            .replaceAll("_", " ") + " to "
                            + tp.getName());
                } catch (Exception e) {
                    player.sendMessage(ChatColor.RED
                            + "There was an error, sorry!");
                }
            }
            return;
        }
        if (args.length == 3) {
            Player tp = Bukkit.getPlayer(args[1]);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            if (!isInt(args[2])) {
                player.sendMessage(ChatColor.RED + "/give"
                        + " [Numeric ID] [Username] [Amount]");
                return;
            }
            int amount = Integer.parseInt(args[2]);
            if (!isInt(args[0])) {
                try {
                    Material mat = Material.getMaterial(args[0].toUpperCase());
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
                    player.sendMessage(ChatColor.RED + "/give"
                            + " [Numeric ID] [Username] [Amount]");
                    return;
                }
            } else {
                String[] list;
                if (args[0].contains(":")) {
                    list = args[0].split(":");
                } else {
                    list = null;
                }
                try {
                    int id;
                    byte data;
                    if (list != null) {
                        id = Integer.parseInt(list[0]);
                        data = Byte.parseByte(list[1]);
                    } else {
                        id = Integer.parseInt(args[0]);
                        data = (byte) 0;
                    }
                    ItemStack item = new ItemStack(id, amount, data);
                    tp.getInventory().addItem(item);
                    player.sendMessage(ChatColor.GRAY
                            + "Giving "
                            + amount
                            + " of "
                            + item.getType().toString().toLowerCase()
                            .replaceAll("_", " ") + " to "
                            + tp.getName());
                } catch (Exception e) {
                    player.sendMessage(ChatColor.RED
                            + "There was an error, sorry!");
                }
            }
            return;
        }
        player.sendMessage(ChatColor.RED + "/give"
                + " [Numeric ID] [Username] [Amount]");
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