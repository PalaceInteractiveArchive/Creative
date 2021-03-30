package network.palace.creative.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
import network.palace.creative.handlers.BannerInventoryType;

/**
 * Created by Marc on 6/12/15
 */
@CommandMeta(description = "Open the Banner Creator", rank = Rank.Guest)
public class BannerCommand extends CoreCommand {

    public BannerCommand() {
        super("banner");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Creative.getInstance().getBannerUtil().openMenu(player.getBukkitPlayer(), BannerInventoryType.SELECT_BASE);
    }
}
