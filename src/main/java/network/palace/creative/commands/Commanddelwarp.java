package network.palace.creative.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
import org.bukkit.ChatColor;

/**
 * Created by Marc on 2/8/15
 */
@CommandMeta(description = "Delete a warp")
@CommandPermission(rank = Rank.KNIGHT)
public class Commanddelwarp extends CoreCommand {

    public Commanddelwarp() {
        super("delwarp");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "/delwarp [Warp]");
            return;
        }
        if (Creative.removeWarp(args[0])) {
            player.sendMessage(ChatColor.GRAY + "Warp " + args[0] + " removed!");
            return;
        }
        player.sendMessage(ChatColor.RED + "Warp not found!");
    }
}
