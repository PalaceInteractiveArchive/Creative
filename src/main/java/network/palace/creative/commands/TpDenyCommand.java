package network.palace.creative.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.creative.utils.TpaUtil;

/**
 * Created by Marc on 2/8/15
 */
@CommandMeta(description = "Deny a teleport request")
@CommandPermission(rank = Rank.SETTLER)
public class TpDenyCommand extends CoreCommand {

    public TpDenyCommand() {
        super("tpdeny");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        TpaUtil.denyTeleport(player);
    }
}
