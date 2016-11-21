package us.mcmagic.creative.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.mcmagiccore.player.PlayerUtil;

public class Commandgive implements CommandExecutor {

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED
                    + "Only players can use this command!");
            return true;
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
                        return true;
                    }
                    Material mat = Material.getMaterial(args[0].toUpperCase());
                    if (mat != null) {
                        ItemStack item = new ItemStack(mat, 64);
                        player.getInventory().addItem(item);
                        player.sendMessage(ChatColor.GRAY
                                + "Giving 64 of "
                                + item.getType().toString().toLowerCase()
                                .replaceAll("_", " "));
                        return true;
                    }
                } catch (Exception e) {
                    player.sendMessage(ChatColor.RED + "/" + label
                            + " [Numeric ID] [Username] [Amount]");
                    return true;
                }
                return true;
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
                    player.sendMessage(ChatColor.RED
                            + "There was an error, sorry!");
                }
                return true;
            }
        }
        if (args.length == 2) {
            Player tp = PlayerUtil.findPlayer(args[1]);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return true;
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
                    player.sendMessage(ChatColor.RED + "/" + label
                            + " [Numeric ID] [Username] [Amount]");
                    return true;
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
            return true;
        }
        if (args.length == 3) {
            Player tp = PlayerUtil.findPlayer(args[1]);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }
            if (!isInt(args[2])) {
                player.sendMessage(ChatColor.RED + "/" + label
                        + " [Numeric ID] [Username] [Amount]");
                return true;
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
                    player.sendMessage(ChatColor.RED + "/" + label
                            + " [Numeric ID] [Username] [Amount]");
                    return true;
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
            return true;
        }
        player.sendMessage(ChatColor.RED + "/" + label
                + " [Numeric ID] [Username] [Amount]");
        return true;
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