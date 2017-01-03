package network.palace.creative.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;

/**
 * Created by Marc on 4/13/15
 */
@CommandMeta(description = "View all redstone activity (spams your chat!)")
@CommandPermission(rank = Rank.PALADIN)
public class Commandloglag extends CoreCommand {

    public Commandloglag() {
        super("loglag");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Creative.redstoneListener.toggleForPlayer(player);
    }
}
