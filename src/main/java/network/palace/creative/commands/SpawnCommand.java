package network.palace.creative.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Marc on 2/8/15
 */
@CommandMeta(description = "Return to Spawn", rank = Rank.GUEST)
public class SpawnCommand extends CoreCommand {

    public SpawnCommand() {
        super("spawn");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "/spawn [Player]");
                return;
            }
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            player.teleport(Creative.getInstance().getSpawn());
            return;
        }
        CPlayer player = Core.getPlayerManager().getPlayer((Player) sender);
        if (player == null) {
            return;
        }
        if (player.getRank().getRankId() < Rank.CM.getRankId()) {
            player.teleport(Creative.getInstance().getSpawn());
            return;
        }
        if (args.length != 1) {
            Creative.getInstance().getTeleportUtil().log(player.getBukkitPlayer(), player.getLocation());
            player.teleport(Creative.getInstance().getSpawn());
            return;
        }
        CPlayer tp = Core.getPlayerManager().getPlayer(Bukkit.getPlayer(args[0]));
        if (tp == null) {
            player.sendMessage(ChatColor.RED + "/spawn [Player]");
            return;
        }
        tp.teleport(Creative.getInstance().getSpawn());
        player.sendMessage(ChatColor.GRAY + tp.getName() + " has teleported to Spawn!");
    }
}
