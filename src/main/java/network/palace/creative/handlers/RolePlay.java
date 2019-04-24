package network.palace.creative.handlers;

import lombok.Getter;
import lombok.Setter;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 11/16/15
 */
public class RolePlay {
    private UUID uniqueRPId = UUID.randomUUID();
    private List<UUID> members;
    @Getter private UUID owner;
    @Getter @Setter private int limit;
    @Getter private String tag;
    @Getter private long lastTagSet = 0;

    public RolePlay(UUID owner, List<UUID> members) {
        this(owner, members, 5);
    }

    public RolePlay(UUID owner, List<UUID> members, int limit) {
        this(owner, members, limit, ChatColor.WHITE + "[" + ChatColor.BLUE + "RP" + ChatColor.WHITE + "]");
    }

    public RolePlay(UUID owner, List<UUID> members, int limit, String tag) {
        this.owner = owner;
        this.members = members;
        this.limit = limit;
        this.tag = tag;
    }

    public String chat(Player player, String m) {
        CPlayer cPlayer = Core.getPlayerManager().getPlayer(player);
        if (cPlayer == null) {
            player.sendMessage(ChatColor.RED + "An error has occurred. Please try again later.");
            return "";
        }

        return chat(cPlayer, m);
    }

    public String chat(CPlayer player, String m) {
        Rank rank = player.getRank();
        String msg = tag + " " + rank.getFormattedName() + ChatColor.GRAY + " " + player.getName() + ": " +
                rank.getChatColor() + m;
        String staffmsg = ChatColor.AQUA + "[RP] " + player.getName() + ": " + m;
        List<UUID> members = getMembers();
        for (UUID uuid : members) {
            Player tp = Bukkit.getPlayer(uuid);
            if (tp != null) {
                tp.sendMessage(msg);
            }
        }
        for (CPlayer tp : Core.getPlayerManager().getOnlinePlayers()) {
            if (tp == null)
                continue;
            if (members.contains(tp.getUniqueId())) {
                continue;
            }
            if (tp.getRank().getRankId() < Rank.TRAINEE.getRankId()) {
                continue;
            }
            tp.sendMessage(staffmsg);
        }
        return msg;
    }

    public void join(CPlayer player) {
        if (player == null)
            return;
        members.remove(player.getUniqueId());
        members.add(player.getUniqueId());
        sendMessage(player.getRank().getTagColor() + player.getName() + ChatColor.YELLOW +
                " has accepted the Role Play Request!");
    }

    public void leave(CPlayer player) {
        if (player == null)
            return;
        members.remove(player.getUniqueId());
        sendMessage(player.getRank().getTagColor() + player.getName() + ChatColor.RED +
                " has left the Role Play!");
    }

    public void sendMessage(String msg) {
        for (UUID uuid : getMembers()) {
            Player tp = Bukkit.getPlayer(uuid);
            if (tp != null) {
                tp.sendMessage(tag + " " + ChatColor.YELLOW + msg);
            }
        }
    }

    public UUID getUniqueId() {
        return uniqueRPId;
    }

    public List<UUID> getMembers() {
        return new ArrayList<>(members);
    }

    public void setTag(String tag) {
        this.tag = tag;
        this.lastTagSet = System.currentTimeMillis() + (10000);
    }

    public void addMember(CPlayer player) {
        members.add(player.getUniqueId());
    }
}
