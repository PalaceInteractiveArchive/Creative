package network.palace.creative.commands;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandMeta(description = "Reload Creative configs.")
@CommandPermission(rank = Rank.SRMOD)
public class CReloadCommand extends CoreCommand {

    public CReloadCommand() {
        super("creload");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        Creative plugin = Creative.getInstance();
        if (args.length == 0) {
            plugin.loadConfig();
            plugin.loadWarps();
            plugin.getParkLoopUtil().reloadLoops();
            sender.sendMessage(ChatColor.GREEN + "Configs reloaded successfully.");
            return;
        }

        String part = args[0].toLowerCase();
        switch (part) {
            case "config":
                plugin.loadConfig();
                sender.sendMessage(ChatColor.GREEN + "Config reloaded successfully.");
                break;
            case "loops":
                plugin.getParkLoopUtil().reloadLoops();
                sender.sendMessage(ChatColor.GREEN + "Loops reloaded successfully.");
                break;
            case "warps":
                plugin.loadWarps();
                sender.sendMessage(ChatColor.GREEN + "Warps reloaded successfully.");
                break;
            default:
                sender.sendMessage(ChatColor.RED + "/creload <config | loops | warps>");
        }
    }

    @Override
    protected List<String> handleTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Stream.of("config", "loops", "warps").filter(arg -> args.length != 0 && arg.startsWith(args[0])).collect(Collectors.toList());
    }
}
