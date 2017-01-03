package network.palace.creative.utils;

import network.palace.core.message.FormattedMessage;
import network.palace.core.player.CPlayer;
import network.palace.creative.Creative;
import network.palace.creative.handlers.PlayerData;
import network.palace.creative.handlers.RolePlay;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by Marc on 11/16/15
 */
public class RolePlayUtil {
    private HashMap<UUID, RolePlay> rolePlays = new HashMap<>();
    public HashMap<UUID, UUID> timerList = new HashMap<>();

    public RolePlay create(CPlayer player) {
        PlayerData data = Creative.getPlayerData(player.getUniqueId());
        List<UUID> members = new ArrayList<>();
        members.add(player.getUniqueId());
        RolePlay rp = new RolePlay(player.getUniqueId(), members, data.getRPLimit());
        rolePlays.put(rp.getUniqueId(), rp);
        return rp;
    }

    public void close(RolePlay rp) {
        rp.sendMessage(ChatColor.RED + "The Role Play has been closed!");
        rolePlays.remove(rp.getUniqueId());
    }

    public RolePlay getRolePlay(UUID uuid) {
        for (RolePlay rp : getRolePlays()) {
            if (rp.getUniqueId().equals(uuid)) {
                return rp;
            }
            if (rp.getMembers().contains(uuid)) {
                return rp;
            }
        }
        return null;
    }

    public List<RolePlay> getRolePlays() {
        return new ArrayList<>(rolePlays.values());
    }

    public void invitePlayer(final RolePlay rp, final Player tp, final CPlayer owner) {
        if (timerList.containsKey(tp.getUniqueId())) {
            owner.sendMessage(ChatColor.GREEN + "This player already has a Role Play request pending!");
            return;
        }
        if (rp.getMembers().contains(tp.getUniqueId())) {
            owner.sendMessage(ChatColor.RED + "This player is already in your Role Play!");
            return;
        }
        RolePlay rpp = getRolePlay(tp.getUniqueId());
        if (rpp != null) {
            if (rpp.getMembers().size() > 1 || hasTimer(rpp)) {
                owner.sendMessage(ChatColor.RED + "This player is already in a Role Play!");
                return;

            }
            rolePlays.remove(rpp.getUniqueId());
        }
        timerList.put(tp.getUniqueId(), rp.getUniqueId());
        FormattedMessage msg = new FormattedMessage(owner.getName()).color(ChatColor.YELLOW)
                .then(" has invited you to their Role Play! ").color(ChatColor.GREEN).then("Click here to join the Role Play.")
                .color(ChatColor.GOLD).tooltip(ChatColor.AQUA + "Click to join this Role Play!").command("/role accept")
                .then(" This invite will expire in 5 minutes.")
                .color(ChatColor.GREEN);
        msg.send(tp);
        rp.sendMessage(ChatColor.YELLOW + owner.getName() + " has asked " + tp.getName() +
                " to join their Role Play, they have 5 minutes to accept!");
        Bukkit.getScheduler().runTaskLater(Creative.getInstance(), () -> {
            if (timerList.containsKey(tp.getUniqueId()) && timerList.get(tp.getUniqueId()).equals(rp.getUniqueId())) {
                timerList.remove(tp.getUniqueId());
                tp.sendMessage(ChatColor.YELLOW + owner.getName() + "'s Role Play request has expired!");
                rp.sendMessage(ChatColor.YELLOW + owner.getName() + "'s request to " + tp.getName() + " has expired!");
            }
        }, 6000L);
    }

    private boolean hasTimer(RolePlay rp) {
        for (Map.Entry<UUID, UUID> entry : timerList.entrySet()) {
            if (entry.getValue().equals(rp.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    public void acceptRequest(CPlayer player) {
        if (!timerList.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You have no pending Role Play Requests!");
            return;
        }
        RolePlay rp = getRolePlay(timerList.remove(player.getUniqueId()));
        rp.addMember(player);
        rp.sendMessage(ChatColor.YELLOW + player.getName() + " has accepted the Role Play Request!");
    }

    public void denyRequest(CPlayer player) {
        if (!timerList.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You have no pending Role Play Requests!");
            return;
        }
        timerList.remove(player.getUniqueId());
        player.sendMessage(ChatColor.RED + "You have denied the Role Play Request!");
    }
}