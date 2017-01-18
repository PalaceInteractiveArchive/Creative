package network.palace.creative.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Marc on 1/3/17.
 */
@CommandMeta(description = "Choose a Resource Pack")
public class CommandPack extends CoreCommand {

    public CommandPack() {
        super("pack");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        CPlayer cp = sender instanceof Player ? Core.getPlayerManager().getPlayer((Player) sender) : null;
        if (cp == null || (args.length > 0 && args[0].equalsIgnoreCase("reload") && cp.getRank().getRankId() >= Rank.WIZARD.getRankId())) {
            sender.sendMessage(ChatColor.BLUE + "Reloading Resource Packs...");
            Creative.getInstance().getResourceUtil().loadPacks();
            sender.sendMessage(ChatColor.BLUE + "Resource Packs Reloaded!");
        } else {
            Creative.getInstance().getResourceUtil().openMenu(cp);
        }
    }
}
