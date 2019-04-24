package network.palace.creative.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;

/**
 * Created by Marc on 4/13/15
 */
@CommandMeta(description = "View all redstone activity (spams your chat!)", rank = Rank.SRMOD)
public class LogLagCommand extends CoreCommand {

    public LogLagCommand() {
        super("loglag");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Creative.getInstance().getRedstoneListener().toggleForPlayer(player);
    }
}
