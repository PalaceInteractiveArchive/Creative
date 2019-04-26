package network.palace.creative.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

//TODO Wait for Lego to complete new Give command for Parks
@CommandMeta(description = "Give yourself an item", aliases = {"item", "i"}, rank = Rank.MOD)
public class GiveCommand extends CoreCommand {

    public GiveCommand() {
        super("give");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(ChatColor.RED + "Yell at Lego to complete give command for the parks.");
    }
}
