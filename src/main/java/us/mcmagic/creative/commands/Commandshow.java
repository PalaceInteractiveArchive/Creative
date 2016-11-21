package us.mcmagic.creative.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.creative.Creative;
import us.mcmagic.creative.handlers.PlayerData;
import us.mcmagic.creative.show.Show;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.chat.formattedmessage.FormattedMessage;
import us.mcmagic.mcmagiccore.permissions.Rank;

import java.io.IOException;

/**
 * Created by Marc on 12/11/15
 */
public class Commandshow implements CommandExecutor {
    private FormattedMessage msg = new FormattedMessage("[Show] ").color(ChatColor.BLUE)
            .then("Purchase the Show Creator in the Creative Shop to use this! ").color(ChatColor.YELLOW)
            .then("Click here to open the Shop").color(ChatColor.AQUA).style(ChatColor.BOLD).tooltip(ChatColor.GREEN +
                    "Open the Creative Shop").command("/shop");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        PlayerData data = Creative.getPlayerData(player.getUniqueId());
        if (!data.hasShowCreator()) {
            msg.send(player);
            return true;
        }
        if (args.length == 0) {
            helpMenu(player);
            return true;
        }
        String action = args[0];
        switch (action.toLowerCase()) {
            case "start": {
                Show show = Creative.showManager.startShow(player);
                if (show != null && show.getNameColored() != null) {
                    Creative.showManager.messagePlayer(player, "Your show " + ChatColor.AQUA + show.getNameColored() +
                            ChatColor.GREEN + " has started!");
                } else {
                    Creative.showManager.messagePlayer(player, "Error starting your show! (Did you create one yet?)");
                }
                return true;
            }
            case "stop": {
                if (Creative.showManager.stopShow(player)) {
                    Creative.showManager.messagePlayer(player, "Your show has stopped!");
                } else {
                    Creative.showManager.messagePlayer(player, ChatColor.RED +
                            "There was an error stopping your show! (Maybe it wasn't running?)");
                }
                return true;
            }
            case "name": {
                if (args.length == 1) {
                    helpMenu(player);
                    return true;
                }
                String name = "";
                for (int i = 1; i < args.length; i++) {
                    name += args[i];
                    if (i < (args.length - 1)) {
                        name += " ";
                    }
                }
                Creative.showManager.setShowName(player, name);
                Creative.showManager.messagePlayer(player, "Your Show's name has been set to " +
                        ChatColor.translateAlternateColorCodes('&', name));
                return true;
            }
            case "edit": {
                try {
                    Creative.showManager.editShow(player);
                } catch (IOException e) {
                    e.printStackTrace();
                    player.sendMessage(ChatColor.RED + "There was an error editing your current Show! Please contact a Cast Member. (Error Code 111)");
                }
                return true;
            }
            case "reload": {
                if (MCMagicCore.getUser(player.getUniqueId()).getRank().getRankId() < Rank.KNIGHT.getRankId()) {
                    helpMenu(player);
                    return true;
                }
                Creative.showManager.loadTracks();
                player.sendMessage(ChatColor.GREEN + "Audio tracks reloaded!");
                return true;
            }
            default:
                helpMenu(player);
                return true;
        }
    }

    private void helpMenu(Player player) {
        player.sendMessage(ChatColor.GREEN + "Show Commands:");
        player.sendMessage(ChatColor.GREEN + "/show start " + ChatColor.AQUA + "- Start your coded Show");
        player.sendMessage(ChatColor.GREEN + "/show stop " + ChatColor.AQUA + "- Stop your coded Show");
        player.sendMessage(ChatColor.GREEN + "/show name [Name] " + ChatColor.AQUA + "- Name your coded Show");
        player.sendMessage(ChatColor.GREEN + "/show edit " + ChatColor.AQUA + "- Edit your Show");
        if (MCMagicCore.getUser(player.getUniqueId()).getRank().getRankId() >= Rank.KNIGHT.getRankId()) {
            player.sendMessage(ChatColor.GREEN + "/show reload " + ChatColor.AQUA + "- Reload track list");
        }
    }
}