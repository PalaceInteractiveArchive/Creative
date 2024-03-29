package network.palace.creative.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;

/**
 * Created by Marc on 9/6/15
 */
@CommandMeta(description = "Open Particle Menu", rank = Rank.GUEST)
public class PTCommand extends CoreCommand {

    public PTCommand() {
        super("pt");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Creative.getInstance().getMenuUtil().openParticle(player);
    }
}
