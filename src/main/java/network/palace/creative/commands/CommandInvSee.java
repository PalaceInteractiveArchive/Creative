package network.palace.creative.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Marc on 3/27/15
 */
@CommandMeta(description = "View another player's inventory")
@CommandPermission(rank = Rank.SQUIRE)
public class CommandInvSee extends CoreCommand {

    public CommandInvSee() {
        super("invsee");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED
                    + "Only players can use this command!");
            return;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            Player tp = Bukkit.getPlayer(args[0]);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            player.sendMessage(ChatColor.GREEN + "Now looking in "
                    + tp.getName() + "'s Inventory!");
            player.openInventory(tp.getInventory());
            return;
        }
        player.sendMessage(ChatColor.RED + "/invsee [Username]");
    }
}