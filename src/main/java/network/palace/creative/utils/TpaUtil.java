package network.palace.creative.utils;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.creative.Creative;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Marc on 2/6/15
 */
public class TpaUtil {
    private static HashMap<UUID, UUID> map = new HashMap<>();
    private static HashMap<UUID, Integer> map2 = new HashMap<>();
    private static HashMap<UUID, Integer> map3 = new HashMap<>();

    public static void logout(Player player) {
        if (map.containsKey(player.getUniqueId())) {
            UUID tuuid = map.remove(player.getUniqueId());
            Bukkit.getPlayer(tuuid).sendMessage(ChatColor.RED + player.getName() +
                    " has logged out, TPA cancelled!");
            cancelTimer(player.getUniqueId());
            cancelTimer(tuuid);
            map.remove(player.getUniqueId());
            return;
        }
        if (map.containsValue(player.getUniqueId())) {
            for (Map.Entry<UUID, UUID> entry : map.entrySet()) {
                if (entry.getValue().equals(player.getUniqueId())) {
                    Bukkit.getPlayer(entry.getKey()).sendMessage(ChatColor.RED + player.getName() +
                            " has logged out, TPA cancelled!");
                    cancelTimer(player.getUniqueId());
                    cancelTimer(entry.getKey());
                    map.remove(entry.getKey());
                    return;
                }
            }
        }
    }

    public static void addTeleport(final CPlayer sender, final CPlayer target) {
        if (map.containsValue(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "That player already has a pending teleport request!");
            return;
        }
        map.put(sender.getUniqueId(), target.getUniqueId());
        final String name = sender.getRank().getTagColor() + sender.getName();
        final String name2 = target.getRank().getTagColor() + target.getName();
        sender.sendMessage(ChatColor.GREEN + "Teleport Request sent to " + name2);
        target.sendMessage(name + ChatColor.GREEN + " has sent you a Teleport Request. Type /tpaccept to accept, and /tpdeny to deny.");
        map2.put(sender.getUniqueId(), Bukkit.getScheduler().runTaskLater(Creative.getInstance(), () -> {
            if (!map.containsKey(sender.getUniqueId())) {
                return;
            }
            map.remove(sender.getUniqueId());
            sender.sendMessage(ChatColor.RED + "Your Teleport Request to " + name2 + ChatColor.RED +
                    " has timed out!");
            target.sendMessage(name + "'s " + ChatColor.RED + "Teleport Request sent to you has timed out!");
        }, 400L).getTaskId());
        map3.put(sender.getUniqueId(), Bukkit.getScheduler().runTaskTimer(Creative.getInstance(), new Runnable() {
            int i = 20;

            @Override
            public void run() {
                if (i <= 0) {
                    target.getActionBar().show(ChatColor.RED + sender.getName() + "'s TPA Expired!");
                    sender.getActionBar().show(ChatColor.RED + "Your TPA to " + target.getName() + " Expired!");
                    cancelTimer(sender.getUniqueId());
                    return;
                }
                target.getActionBar().show(ChatColor.AQUA + sender.getName() + "'s TPA: " + getTimerMessage(i)
                        + " " + ChatColor.AQUA + i + "s");
                sender.getActionBar().show(ChatColor.GREEN + "Your TPA to " + ChatColor.AQUA + target.getName()
                        + ": " + getTimerMessage(i) + " " + ChatColor.AQUA + i + "s");
                i--;
            }
        }, 0, 20L).getTaskId());
    }

    public static void acceptTeleport(CPlayer player) {
        if (!map.containsValue(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You don't have any pending Teleport Requests!");
            return;
        }
        CPlayer tp = null;
        for (Map.Entry<UUID, UUID> entry : map.entrySet()) {
            if (entry.getValue().equals(player.getUniqueId())) {
                tp = Core.getPlayerManager().getPlayer(Bukkit.getPlayer(entry.getKey()));
                break;
            }
        }
        if (tp == null) {
            player.sendMessage(ChatColor.RED + "You don't have any pending Teleport Requests!");
            return;
        }
        map.remove(tp.getUniqueId());
        cancelTimer(tp.getUniqueId());
        Creative.getInstance().getTeleportUtil().log(tp.getBukkitPlayer(), tp.getLocation());
        tp.teleport(player.getLocation());
        final String name = tp.getRank().getTagColor() + tp.getName();
        final String name2 = player.getRank().getTagColor() + player.getName();
        player.getActionBar().show(ChatColor.GREEN + "You accepted " + name + "'s " + ChatColor.GREEN +
                "Teleport Request!");
        tp.getActionBar().show(name2 + ChatColor.GREEN + " accepted your Teleport Request!");
        player.sendMessage(ChatColor.GREEN + "You accepted " + name + "'s " + ChatColor.GREEN + "Teleport Request!");
        tp.sendMessage(name2 + ChatColor.GREEN + " accepted your Teleport Request!");
    }

    public static void denyTeleport(CPlayer player) {
        if (!map.containsValue(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You don't have any pending Teleport Requests!");
            return;
        }
        CPlayer tp = null;
        for (Map.Entry<UUID, UUID> entry : map.entrySet()) {
            if (entry.getValue().equals(player.getUniqueId())) {
                tp = Core.getPlayerManager().getPlayer(Bukkit.getPlayer(entry.getKey()));
                break;
            }
        }
        if (tp == null) {
            player.sendMessage(ChatColor.RED + "You don't have any pending Teleport Requests!");
            return;
        }
        map.remove(tp.getUniqueId());
        cancelTimer(tp.getUniqueId());
        final String name = tp.getRank().getTagColor() + tp.getName();
        final String name2 = player.getRank().getTagColor() + player.getName();
        player.getActionBar().show(ChatColor.RED + "You denied " + name + "'s " + ChatColor.RED +
                "Teleport Request!");
        tp.getActionBar().show(name2 + ChatColor.RED + " denied your Teleport Request!");
        player.sendMessage(ChatColor.RED + "You denied " + name + "'s " + ChatColor.RED + "Teleport Request!");
        tp.sendMessage(name2 + ChatColor.RED + " denied your Teleport Request!");
    }

    private static void cancelTimer(UUID uuid) {
        Integer taskID1 = map2.remove(uuid);
        Integer taskID2 = map3.remove(uuid);
        if (taskID1 != null) {
            Bukkit.getScheduler().cancelTask(taskID1);
        }
        if (taskID2 != null) {
            Bukkit.getScheduler().cancelTask(taskID2);
        }
    }

    private static String getTimerMessage(int i) {
        switch (i) {
            case 20:
                return ChatColor.DARK_GREEN + "▉▉▉▉▉▉▉▉▉▉";
            case 19:
                return ChatColor.DARK_GREEN + "▉▉▉▉▉▉▉▉▉" + ChatColor.GREEN + "▉";
            case 18:
                return ChatColor.DARK_GREEN + "▉▉▉▉▉▉▉▉▉" + ChatColor.RED + "▉";
            case 17:
                return ChatColor.DARK_GREEN + "▉▉▉▉▉▉▉▉" + ChatColor.GREEN + "▉" + ChatColor.RED + "▉";
            case 16:
                return ChatColor.DARK_GREEN + "▉▉▉▉▉▉▉▉" + ChatColor.RED + "▉▉";
            case 15:
                return ChatColor.DARK_GREEN + "▉▉▉▉▉▉▉" + ChatColor.GREEN + "▉" + ChatColor.RED + "▉▉";
            case 14:
                return ChatColor.DARK_GREEN + "▉▉▉▉▉▉▉" + ChatColor.RED + "▉▉▉";
            case 13:
                return ChatColor.DARK_GREEN + "▉▉▉▉▉▉" + ChatColor.GREEN + "▉" + ChatColor.RED + "▉▉▉";
            case 12:
                return ChatColor.DARK_GREEN + "▉▉▉▉▉▉" + ChatColor.RED + "▉▉▉▉";
            case 11:
                return ChatColor.DARK_GREEN + "▉▉▉▉▉" + ChatColor.GREEN + "▉" + ChatColor.RED + "▉▉▉▉";
            case 10:
                return ChatColor.DARK_GREEN + "▉▉▉▉▉" + ChatColor.RED + "▉▉▉▉▉";
            case 9:
                return ChatColor.DARK_GREEN + "▉▉▉▉" + ChatColor.GREEN + "▉" + ChatColor.RED + "▉▉▉▉▉";
            case 8:
                return ChatColor.DARK_GREEN + "▉▉▉▉" + ChatColor.RED + "▉▉▉▉▉▉";
            case 7:
                return ChatColor.DARK_GREEN + "▉▉▉" + ChatColor.GREEN + "▉" + ChatColor.RED + "▉▉▉▉▉▉";
            case 6:
                return ChatColor.DARK_GREEN + "▉▉▉" + ChatColor.RED + "▉▉▉▉▉▉▉";
            case 5:
                return ChatColor.DARK_GREEN + "▉▉" + ChatColor.GREEN + "▉" + ChatColor.RED + "▉▉▉▉▉▉▉";
            case 4:
                return ChatColor.DARK_GREEN + "▉▉" + ChatColor.RED + "▉▉▉▉▉▉▉▉";
            case 3:
                return ChatColor.DARK_GREEN + "▉" + ChatColor.GREEN + "▉" + ChatColor.RED + "▉▉▉▉▉▉▉▉";
            case 2:
                return ChatColor.DARK_GREEN + "▉" + ChatColor.RED + "▉▉▉▉▉▉▉▉▉";
            case 1:
                return ChatColor.GREEN + "▉" + ChatColor.RED + "▉▉▉▉▉▉▉▉▉";
            case 0:
                return ChatColor.RED + "▉▉▉▉▉▉▉▉▉▉";
            default:
                return "";
        }
    }
}