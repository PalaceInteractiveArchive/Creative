package network.palace.creative.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Marc on 2/6/15
 */
@CommandMeta(description = "Teleport command")
@CommandPermission(rank = Rank.SQUIRE)
public class Commandtp extends CoreCommand {

    public Commandtp() {
        super("tp");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            if (args.length == 2) {
                Player tp1 = Bukkit.getPlayer(args[0]);
                Player tp2 = Bukkit.getPlayer(args[1]);
                if (tp1 == null || tp2 == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    return;
                }
                Creative.getInstance().getTeleportUtil().log(tp1, tp1.getLocation());
                tp1.teleport(tp2);
                sender.sendMessage(ChatColor.GRAY + tp1.getName() + " has been teleported to " + tp2.getName());
                return;
            }
            if (args.length == 4) {
                try {
                    Player tp = Bukkit.getPlayer(args[0]);
                    double x = args[1].startsWith("~") ? tp.getLocation().getX()
                            + num(args[1].substring(1)) :
                            num(args[1]);
                    double y = args[2].startsWith("~") ? tp.getLocation().getY()
                            + num(args[2].substring(1)) :
                            num(args[2]);
                    double z = args[3].startsWith("~") ? tp.getLocation().getZ()
                            + num(args[3].substring(1)) :
                            num(args[3]);
                    Location loc = new Location(tp.getWorld(), x, y, z, tp
                            .getLocation().getYaw(), tp.getLocation().getPitch());
                    Creative.getInstance().getTeleportUtil().log(tp, tp.getLocation());
                    tp.teleport(loc);
                    sender.sendMessage(ChatColor.GRAY + tp.getName() + " has been teleported to " + x + ", " + y + ", "
                            + z);
                    return;
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Error with numbers!");
                    return;
                }
            }
            sender.sendMessage(ChatColor.RED + "/tp [Player] <Target> or <x> <y> <z>");
            return;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            Player tp = Bukkit.getPlayer(args[0]);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            Creative.getInstance().getTeleportUtil().log(player, player.getLocation());
            player.teleport(tp);
            player.sendMessage(ChatColor.GRAY + "You teleported to " + tp.getName());
            return;
        }
        if (args.length == 2) {
            Player tp1 = Bukkit.getPlayer(args[0]);
            Player tp2 = Bukkit.getPlayer(args[1]);
            if (tp1 == null || tp2 == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            Creative.getInstance().getTeleportUtil().log(tp1, tp1.getLocation());
            tp1.teleport(tp2);
            player.sendMessage(ChatColor.GRAY + tp1.getName()
                    + " has been teleported to " + tp2.getName());
            return;
        }
        if (args.length == 3) {
            try {
                double x = args[0].startsWith("~") ? player.getLocation().getX()
                        + num(args[0].substring(1)) : num(args[0]);
                double y = args[1].startsWith("~") ? player.getLocation().getY()
                        + num(args[1].substring(1)) : num(args[1]);
                double z = args[2].startsWith("~") ? player.getLocation().getZ()
                        + num(args[2].substring(1)) : num(args[2]);
                Location loc = new Location(player.getWorld(), x, y, z, player
                        .getLocation().getYaw(), player.getLocation().getPitch());
                Creative.getInstance().getTeleportUtil().log(player, player.getLocation());
                player.teleport(loc);
                player.sendMessage(ChatColor.GRAY + "You teleported to " + x + ", " + y + ", " + z);
                return;
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Error with numbers!");
                return;
            }
        }
        if (args.length == 4) {
            try {
                Player tp = Bukkit.getPlayer(args[0]);
                double x = args[0].startsWith("~") ? player.getLocation().getX()
                        + num(args[0].substring(1)) : num(args[1]);
                double y = args[1].startsWith("~") ? player.getLocation().getY()
                        + num(args[1].substring(1)) : num(args[2]);
                double z = args[2].startsWith("~") ? player.getLocation().getZ()
                        + num(args[2].substring(1)) : num(args[3]);
                Location loc = new Location(tp.getWorld(), x, y, z, tp
                        .getLocation().getYaw(), tp.getLocation().getPitch());
                Creative.getInstance().getTeleportUtil().log(tp, tp.getLocation());
                tp.teleport(loc);
                player.sendMessage(ChatColor.GRAY + tp.getName() + " has been teleported to " + x + ", " + y + ", " + z);
                return;
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Error with numbers!");
                return;
            }
        }
        player.sendMessage(ChatColor.RED + "/tp [Player] <Target> or /tp <x> <y> <z> or /tp [Player] <x> <y> <z>");
    }

    private double num(String s) {
        if (s == null) {
            return 0;
        }
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }
}