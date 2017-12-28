package network.palace.creative.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
import org.bukkit.ChatColor;

/**
 * Created by Marc on 2/8/15
 */
@CommandMeta(description = "Set the spawn location")
@CommandPermission(rank = Rank.SRMOD)
public class SetSpawnCommand extends CoreCommand {

    public SetSpawnCommand() {
        super("setspawn");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        Creative.getInstance().setSpawn(player.getLocation());
        player.sendMessage(ChatColor.GRAY + "Spawn set!");
    }
}
