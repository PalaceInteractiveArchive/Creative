package network.palace.creative.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Created by Marc on 12/14/14
 */
@CommandMeta(description = "Reload head database")
@CommandPermission(rank = Rank.KNIGHT)
public class CommandHead extends CoreCommand {

    public CommandHead() {
        super("head");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(ChatColor.BLUE + "Reloading Head Database...");
        Creative.getInstance().getHeadUtil().update();
        sender.sendMessage(ChatColor.BLUE + "Head Database reloaded!");
    }
}
