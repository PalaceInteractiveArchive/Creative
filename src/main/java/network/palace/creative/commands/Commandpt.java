package network.palace.creative.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
import network.palace.creative.handlers.CreativeInventoryType;

/**
 * Created by Marc on 9/6/15
 */
@CommandMeta(description = "Open Particle Menu")
@CommandPermission(rank = Rank.SETTLER)
public class Commandpt extends CoreCommand {

    public Commandpt() {
        super("pt");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Creative.menuUtil.openMenu(player.getBukkitPlayer(), CreativeInventoryType.PARTICLE);
    }
}
