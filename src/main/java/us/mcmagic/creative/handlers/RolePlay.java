package us.mcmagic.creative.handlers;

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
 * Created by Marc on 11/16/15
 */
public class RolePlay {
    private UUID uniqueRPId = UUID.randomUUID();
    private List<UUID> members;
    private UUID owner;
    private int limit;
    private String tag;
    private long lastTagSet = 0;

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
        Rank rank = MCMagicCore.getUser(player.getUniqueId()).getRank();
        String msg = tag + " " + rank.getNameWithBrackets() + ChatColor.GRAY + " " + player.getName() + ": " +
                rank.getChatColor() + m;
        String staffmsg = "[RP] " + player.getName() + ": " + m;
        List<UUID> members = getMembers();
        for (UUID uuid : members) {
            Player tp = Bukkit.getPlayer(uuid);
            if (tp != null) {
                tp.sendMessage(msg);
            }
        }
        for (Player tp : Bukkit.getOnlinePlayers()) {
            if (members.contains(tp.getUniqueId())) {
                continue;
            }
            User user = MCMagicCore.getUser(tp.getUniqueId());
            if (user.getRank().getRankId() < Rank.SQUIRE.getRankId()) {
                continue;
            }
            tp.sendMessage(staffmsg);
        }
        return msg;
    }

    public void join(Player player) {
        members.remove(player.getUniqueId());
        members.add(player.getUniqueId());
        User user = MCMagicCore.getUser(player.getUniqueId());
        sendMessage(user.getRank().getTagColor() + player.getName() + ChatColor.YELLOW +
                " has accepted the Role Play Request!");
    }

    public void leave(Player player) {
        members.remove(player.getUniqueId());
        User user = MCMagicCore.getUser(player.getUniqueId());
        sendMessage(user.getRank().getTagColor() + player.getName() + ChatColor.RED +
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

    public UUID getOwner() {
        return owner;
    }

    public List<UUID> getMembers() {
        return new ArrayList<>(members);
    }

    public int getLimit() {
        return limit;
    }

    public String getTag() {
        return tag;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setTag(String tag) {
        this.tag = tag;
        this.lastTagSet = System.currentTimeMillis() + (10000);
    }

    public long getLastTagSet() {
        return lastTagSet;
    }

    public void addMember(Player player) {
        members.add(player.getUniqueId());
    }
}