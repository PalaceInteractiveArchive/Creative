package us.mcmagic.creative.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.creative.Creative;
import us.mcmagic.creative.handlers.CreativeInventoryType;
import us.mcmagic.creative.handlers.PlayerData;
import us.mcmagic.creative.handlers.RolePlay;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.player.PlayerUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 11/16/15
 */
public class Commandrole implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            help(player);
            return true;
        }
        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "create": {
                    if (Creative.rolePlayUtil.getRolePlay(player.getUniqueId()) != null) {
                        player.sendMessage(ChatColor.RED + "You're already in a Role Play!");
                        return true;
                    }
                    Creative.rolePlayUtil.create(player);
                    player.sendMessage(ChatColor.GREEN + "You created a " + ChatColor.YELLOW + "Role Play!");
                    return true;
                }
                case "list": {
                    RolePlay rp = Creative.rolePlayUtil.getRolePlay(player.getUniqueId());
                    if (rp == null) {
                        player.sendMessage(ChatColor.RED + "You're not in a Role Play!");
                        return true;
                    }
                    List<UUID> members = rp.getMembers();
                    List<String> names = new ArrayList<>();
                    for (UUID uuid : members) {
                        try {
                            names.add(Bukkit.getPlayer(uuid).getName());
                        } catch (Exception ignored) {
                        }
                    }
                    Collections.sort(names);
                    String msg = rp.getTag() + ChatColor.YELLOW + " Role Play Members: ";
                    for (int i = 0; i < names.size(); i++) {
                        msg += names.get(i);
                        if (i < (names.size() - 1)) {
                            msg += ", ";
                        }
                    }
                    player.sendMessage(msg);
                    return true;
                }
                case "shop": {
                    Creative.menuUtil.openMenu(player, CreativeInventoryType.CREATIVESHOP);
                    return true;
                }
                case "close": {
                    RolePlay rp = Creative.rolePlayUtil.getRolePlay(player.getUniqueId());
                    if (rp == null) {
                        player.sendMessage(ChatColor.RED + "You're not in a Role Play!");
                        return true;
                    }
                    if (!rp.getOwner().equals(player.getUniqueId())) {
                        player.sendMessage(ChatColor.RED + "Only the Role Play Owner can do this!");
                        return true;
                    }
                    Creative.rolePlayUtil.close(rp);
                    return true;
                }
                case "leave": {
                    RolePlay rp = Creative.rolePlayUtil.getRolePlay(player.getUniqueId());
                    if (rp == null) {
                        player.sendMessage(ChatColor.RED + "You're not in a Role Play!");
                        return true;
                    }
                    if (rp.getOwner().equals(player.getUniqueId())) {
                        player.sendMessage(ChatColor.RED +
                                "You're the Role Play Owner, you can't leave! Close the Role Play instead.");
                        return true;
                    }
                    rp.leave(player);
                    return true;
                }
                case "accept": {
                    Creative.rolePlayUtil.acceptRequest(player);
                    return true;
                }
                case "deny": {
                    Creative.rolePlayUtil.denyRequest(player);
                    return true;
                }
            }
            help(player);
            return true;
        }
        if (args.length == 2 && !args[0].equalsIgnoreCase("tag")) {
            switch (args[0].toLowerCase()) {
                case "invite": {
                    RolePlay rp = Creative.rolePlayUtil.getRolePlay(player.getUniqueId());
                    if (rp == null) {
                        player.sendMessage(ChatColor.RED + "You're not in a Role Play!");
                        return true;
                    }
                    if (!rp.getOwner().equals(player.getUniqueId())) {
                        player.sendMessage(ChatColor.RED + "You must be the Role Play Owner to change the tag!");
                        return true;
                    }
                    PlayerData data = Creative.getPlayerData(player.getUniqueId());
                    if (rp.getMembers().size() >= data.getRPLimit()) {
                        player.sendMessage(ChatColor.RED + "You reached your Role Play Limit of " + ChatColor.GREEN +
                                data.getRPLimit() + " Players! " + ChatColor.RED +
                                "Increase this limit in the Shop. /role shop");
                        return true;
                    }
                    Player tp = PlayerUtil.findPlayer(args[1]);
                    if (tp == null) {
                        player.sendMessage(ChatColor.RED + "Player not found!");
                        return true;
                    }
                    Creative.rolePlayUtil.invitePlayer(rp, tp, player);
                    return true;
                }
                case "remove": {
                    RolePlay rp = Creative.rolePlayUtil.getRolePlay(player.getUniqueId());
                    if (rp == null) {
                        player.sendMessage(ChatColor.RED + "You're not in a Role Play!");
                        return true;
                    }
                    if (!rp.getOwner().equals(player.getUniqueId())) {
                        player.sendMessage(ChatColor.RED + "You must be the Role Play Owner to change the tag!");
                        return true;
                    }
                    Player tp = PlayerUtil.findPlayer(args[1]);
                    if (tp == null) {
                        player.sendMessage(ChatColor.RED + "Player not found!");
                        return true;
                    }
                    if (tp.getUniqueId().equals(player.getUniqueId())) {
                        player.sendMessage(ChatColor.RED + "You can't remove yourself from the Role Play!");
                        return true;
                    }
                    rp.leave(tp);
                    return true;
                }
            }
            help(player);
            return true;
        }
        if (args[0].equalsIgnoreCase("tag")) {
            RolePlay rp = Creative.rolePlayUtil.getRolePlay(player.getUniqueId());
            if (rp == null) {
                player.sendMessage(ChatColor.RED + "You're not in a Role Play!");
                return true;
            }
            if (!rp.getOwner().equals(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You must be the Role Play Owner to change the tag!");
                return true;
            }
            PlayerData data = Creative.getPlayerData(player.getUniqueId());
            if (!data.hasRPTag()) {
                player.sendMessage(ChatColor.RED +
                        "You must purchase the ability to customize your Role Play Tag in the RP Shop! /role shop");
                return true;
            }
            if (rp.getLastTagSet() >= System.currentTimeMillis()) {
                player.sendMessage(ChatColor.RED + "You can only change your Role Play Tag every 10 seconds!");
                return true;
            }
            String tag = "";
            for (int i = 1; i < args.length; i++) {
                tag += args[i];
                if (i < (args.length - 1)) {
                    tag += " ";
                }
            }
            if (tag.length() > 16) {
                player.sendMessage(ChatColor.RED + "Role Play Tags cannot be longer than 16 characters!");
                return true;
            }
            tag = ChatColor.WHITE + "[" + ChatColor.BLUE + ChatColor.translateAlternateColorCodes('&', tag) +
                    ChatColor.WHITE + "]";
            rp.setTag(tag);
            rp.sendMessage("The RP Tag was changed to " + tag + ChatColor.YELLOW + " by " +
                    MCMagicCore.getUser(player.getUniqueId()).getRank().getTagColor() + player.getName());
            return true;
        }
        help(player);
        return true;
    }

    private void help(Player player) {
        player.sendMessage(ChatColor.GREEN + "Role Play Commands:");
        player.sendMessage(ChatColor.GREEN + "/role create " + ChatColor.AQUA + "- Create a new Role Play");
        player.sendMessage(ChatColor.GREEN + "/role list " + ChatColor.AQUA +
                "- List all members of your Role Play");
        player.sendMessage(ChatColor.GREEN + "/role invite [Username] " + ChatColor.AQUA +
                "- Invite someone to your Role Play");
        player.sendMessage(ChatColor.GREEN + "/role leave " + ChatColor.AQUA + "- Leave your current Role Play");
        player.sendMessage(ChatColor.GREEN + "/role remove [Username] " + ChatColor.AQUA +
                "- Remove someone from your Role Play");
        player.sendMessage(ChatColor.GREEN + "/role accept " + ChatColor.AQUA + "- Accept a Role Play invite");
        player.sendMessage(ChatColor.GREEN + "/role deny " + ChatColor.AQUA + "- Deny a Role Play invite");
        player.sendMessage(ChatColor.GREEN + "/role shop " + ChatColor.AQUA +
                "- Purchase upgrades for your Role Play");
        player.sendMessage(ChatColor.GREEN + "/role tag [Tag...] " + ChatColor.AQUA +
                "- Set the Tag for your Role Play");
        player.sendMessage(ChatColor.GREEN + "/role close " + ChatColor.AQUA + "- Close the Role Play");
    }
}