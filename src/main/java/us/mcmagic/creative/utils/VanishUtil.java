package us.mcmagic.creative.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 12/14/14
 */
public class VanishUtil {
    private static List<UUID> vanished = new ArrayList<>();

    public static void addToVanish(Player player) {
        player.sendMessage(ChatColor.DARK_AQUA + "You have vanished. Poof.");
        vanished.add(player.getUniqueId());
        for (Player tp : Bukkit.getOnlinePlayers()) {
            User user = MCMagicCore.getUser(tp.getUniqueId());
            if (tp.getUniqueId().equals(player.getUniqueId()))
                continue;
            if (!canVanish(tp)) {
                tp.hidePlayer(player);
            } else {
                tp.sendMessage(ChatColor.YELLOW + player.getName() + " has vanished. Poof.");
            }
        }
    }

    public static void setVanished(Player player) {
        vanished.add(player.getUniqueId());
        for (Player tp : Bukkit.getOnlinePlayers()) {
            User user = MCMagicCore.getUser(tp.getUniqueId());
            if (tp.getUniqueId().equals(player.getUniqueId()))
                continue;
            if (!canVanish(tp)) {
                tp.hidePlayer(player);
            }
        }
    }

    public static void removeFromVanish(Player player) {
        player.sendMessage(ChatColor.DARK_AQUA + "You have become visible.");
        try {
            vanished.remove(player.getUniqueId());
        } catch (Exception ignored) {
        }
        for (Player tp : Bukkit.getOnlinePlayers()) {
            User user = MCMagicCore.getUser(tp.getUniqueId());
            if (tp.getUniqueId().equals(player.getUniqueId()))
                continue;
            tp.showPlayer(player);
            if (canVanish(tp)) {
                tp.sendMessage(ChatColor.YELLOW + player.getName() + " has become visible.");
            }
        }
    }

    public static void logout(Player player) {
        if (vanished.contains(player.getUniqueId())) {
            vanished.remove(player.getUniqueId());
        }
    }

    public static void hideVanishedPlayers(Player player) {
        for (Player tp : Bukkit.getOnlinePlayers()) {
            User user = MCMagicCore.getUser(tp.getUniqueId());
            if (tp == null) {
                continue;
            }
            if (tp.getUniqueId().equals(player.getUniqueId()))
                continue;
            if (vanished.contains(tp.getUniqueId())) {
                player.hidePlayer(tp);
            }
        }
    }

    public static boolean isVanished(Player player) {
        return vanished.contains(player.getUniqueId());
    }

    public static String vanishedPlayers() {
        StringBuilder list = new StringBuilder();
        for (Player tp : Bukkit.getOnlinePlayers()) {
            User user = MCMagicCore.getUser(tp.getUniqueId());
            if (vanished.contains(tp.getUniqueId())) {
                if (list.length() > 0) {
                    list.append(ChatColor.DARK_AQUA);
                    list.append(", ");
                }
                list.append(ChatColor.AQUA);
                list.append(tp.getName());
            }
        }
        list.insert(0, "Vanished: ");
        list.insert(0, ChatColor.DARK_AQUA);
        return list.toString();
    }

    public static boolean canVanish(Player player) {
        return MCMagicCore.getUser(player.getUniqueId()).getRank().getRankId() >= Rank.SQUIRE.getRankId();
    }
}