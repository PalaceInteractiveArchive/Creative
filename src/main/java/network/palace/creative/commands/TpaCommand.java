package network.palace.creative.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
import network.palace.creative.utils.TpaUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * Created by Marc on 2/6/15
 */
@CommandMeta(description = "Send a teleport request", rank = Rank.SETTLER)
public class TpaCommand extends CoreCommand {

    public TpaCommand() {
        super("tpa");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "/tpa [Player]");
            return;
        }
        CPlayer tp = Core.getPlayerManager().getPlayer(Bukkit.getPlayer(args[0]));
        if (tp == null) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }
        if (player.getRank().getRankId() < Rank.TRAINEE.getRankId()) {
            if (tp.getRank().getRankId() >= Rank.TRAINEE.getRankId() ||
                    Creative.getInstance().getIgnoreUtil().isIgnored(tp.getUniqueId(), player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You can't send this player a TPA Request!");
                return;
            }
        }
        TpaUtil.addTeleport(player, tp);
    }
}