package network.palace.creative.commands;

import java.util.Optional;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
import network.palace.creative.itemexploit.CaughtUserData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

@CommandMeta(description = "Check if a user has been caught with banned/hacked items.", aliases = "bic", usage = "/bic [player]", rank = Rank.SRMOD)
public class BannedItemCheckCommand extends CoreCommand {

    public BannedItemCheckCommand() {
        super("banneditemcheck");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "/bic [player]");
            return;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
        Optional<CaughtUserData> data = Creative.getInstance().getItemExploitHandler().getData(offlinePlayer.getUniqueId());
        if (data.isPresent()) {
            Creative.getInstance().getItemExploitHandler().viewPlayer(player, data.get(), offlinePlayer, 1);
            return;
        }

        player.sendMessage(ChatColor.GREEN + "That player does not have any banned/hacked items under review.");
    }
}
