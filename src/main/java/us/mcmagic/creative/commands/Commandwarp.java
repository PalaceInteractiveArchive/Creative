package us.mcmagic.creative.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.creative.Creative;
import us.mcmagic.creative.handlers.Warp;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.chat.formattedmessage.FormattedMessage;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.PlayerUtil;
import us.mcmagic.mcmagiccore.player.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Marc on 2/8/15
 */
public class Commandwarp implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length != 2) {
                sender.sendMessage(ChatColor.RED + "/warp [Warp] [Player]");
                return true;
            }
            Warp warp = Creative.getWarp(args[0]);
            if (warp == null) {
                sender.sendMessage(ChatColor.RED + "Warp not found!");
                return true;
            }
            Player tp = PlayerUtil.findPlayer(args[1]);
            if (tp == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return true;
            }
            Creative.teleportUtil.log(tp, tp.getLocation());
            tp.teleport(warp.getLocation());
            tp.sendMessage(ChatColor.BLUE + "You have arrived at " + ChatColor.WHITE + "[" + ChatColor.GREEN +
                    warp.getName() + ChatColor.WHITE + "]");
            return true;
        }
        Player player = (Player) sender;
        User user = MCMagicCore.getUser(player.getUniqueId());
        if (user.getRank().getRankId() < Rank.KNIGHT.getRankId()) {
            if (args.length == 0) {
                listWarps(player, 1);
                return true;
            }
            if (isInt(args[0])) {
                listWarps(player, Integer.parseInt(args[0]));
                return true;
            }
            Warp warp = Creative.getWarp(args[0]);
            if (warp == null) {
                player.sendMessage(ChatColor.RED + "Warp not found!");
                return true;
            }
            if (warp.getName().toLowerCase().startsWith("dvc")) {
                if (user.getRank().getRankId() < Rank.DVCMEMBER.getRankId()) {
                    player.sendMessage(ChatColor.RED + "You must be a " + Rank.DVCMEMBER.getNameWithBrackets() +
                            ChatColor.RED + " or higher to go here!");
                    return true;
                }
            }
            if (warp.getName().toLowerCase().startsWith("staff")) {
                if (user.getRank().getRankId() < Rank.SQUIRE.getRankId()) {
                    player.sendMessage(ChatColor.RED + "You must be an " + Rank.SQUIRE.getNameWithBrackets() +
                            ChatColor.RED + " or higher to go here!");
                    return true;
                }
            }
            Creative.teleportUtil.log(player, player.getLocation());
            player.teleport(warp.getLocation());
            player.sendMessage(ChatColor.BLUE + "You have arrived at " + ChatColor.WHITE + "[" + ChatColor.GREEN +
                    warp.getName() + ChatColor.WHITE + "]");
            return true;
        }
        if (args.length == 0) {
            listWarps(player, 1);
            return true;
        }
        if (args.length == 1) {
            if (isInt(args[0])) {
                listWarps(player, Integer.parseInt(args[0]));
                return true;
            }
            Warp warp = Creative.getWarp(args[0]);
            if (warp == null) {
                player.sendMessage(ChatColor.RED + "Warp not found!");
                return true;
            }
            Creative.teleportUtil.log(player, player.getLocation());
            player.teleport(warp.getLocation());
            player.sendMessage(ChatColor.BLUE + "You have arrived at " + ChatColor.WHITE + "[" + ChatColor.GREEN +
                    warp.getName() + ChatColor.WHITE + "]");
            return true;
        }
        if (isInt(args[0])) {
            listWarps(player, Integer.parseInt(args[0]));
            return true;
        }
        Warp warp = Creative.getWarp(args[0]);
        Player tp = PlayerUtil.findPlayer(args[1]);
        if (warp == null) {
            player.sendMessage(ChatColor.RED + "Warp not found!");
            return true;
        }
        if (tp == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }
        Creative.teleportUtil.log(tp, tp.getLocation());
        tp.teleport(warp.getLocation());
        tp.sendMessage(ChatColor.BLUE + "You have arrived at " + ChatColor.WHITE + "[" + ChatColor.GREEN +
                warp.getName() + ChatColor.WHITE + "]");
        player.sendMessage(ChatColor.BLUE + tp.getName() + " has arrived at " + ChatColor.WHITE + "[" + ChatColor.GREEN
                + warp.getName() + ChatColor.WHITE + "]");
        return true;
    }

    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    public static void listWarps(Player player, int page) {
        List<Warp> warps = Creative.getWarps();
        List<String> nlist = warps.stream().map(Warp::getName).collect(Collectors.toList());
        Collections.sort(nlist);
        if (nlist.size() < (page - 1) * 20 && page != 1) {
            listWarps(player, 1);
            return;
        }
        int max = page * 20;
        List<String> names = nlist.subList(20 * (page - 1), nlist.size() < max ? nlist.size() : max);
        FormattedMessage msg = new FormattedMessage("Warps (Page " + page + "):\n").color(ChatColor.GRAY);
        for (int i = 0; i < names.size(); i++) {
            String warp = names.get(i);
            if (i == (names.size() - 1)) {
                msg.then(warp).color(ChatColor.GRAY).command("/warp " + warp).tooltip(ChatColor.GREEN +
                        "Click to warp to " + ChatColor.BLUE + warp + ChatColor.GREEN + "!");
                continue;
            }
            msg.then(warp + ", ").color(ChatColor.GRAY).command("/warp " + warp).tooltip(ChatColor.GREEN +
                    "Click to warp to " + ChatColor.BLUE + warp + ChatColor.GREEN + "!");
        }
        msg.send(player);
    }
}