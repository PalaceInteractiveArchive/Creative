package network.palace.creative.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
import org.bukkit.ChatColor;

/**
 * Created by Marc on 7/29/15
 */
@CommandMeta(description = "Open Creative Menu", rank = Rank.GUEST)
public class MenuCommand extends CoreCommand {

    public MenuCommand() {
        super("menu");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        player.sendMessage(ChatColor.GREEN + "Opening Creative Menu...");
        Creative.getInstance().getMenuUtil().openMenu(player);
    }
}
