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
 * Created by Marc on 12/27/15
 */
@CommandMeta(description = "Open Creative Shop")
@CommandPermission(rank = Rank.SETTLER)
public class Commandshop extends CoreCommand {

    public Commandshop() {
        super("shop");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Creative.getInstance().getMenuUtil().openMenu(player.getBukkitPlayer(), CreativeInventoryType.CREATIVESHOP);
    }
}
