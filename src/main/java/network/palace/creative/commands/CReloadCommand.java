package network.palace.creative.commands;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@CommandMeta(description = "Reload Creative configs.", usage = "/creload <banneditems | config | loops | plotwarps | warps>", rank = Rank.COORDINATOR)
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
            case "banneditems":
                try {
                    plugin.getItemExploitHandler().loadBlockedItems();
                    sender.sendMessage(ChatColor.GREEN + "Blocked items reloaded successfully.");
                }
                catch (IOException e) {
                    sender.sendMessage(ChatColor.RED + "An error has occurred while trying to read/load the blocked items.");
                }
                break;
            case "config":
                plugin.loadConfig();
                sender.sendMessage(ChatColor.GREEN + "Config reloaded successfully.");
                break;
            case "loops":
                plugin.getParkLoopUtil().reloadLoops();
                sender.sendMessage(ChatColor.GREEN + "Loops reloaded successfully.");
                break;
            case "plotwarps":
                plugin.getPlotWarpUtil().load();
                sender.sendMessage(ChatColor.GREEN + "Plot Warps reloaded successfully.");
                break;
            case "warps":
                plugin.loadWarps();
                sender.sendMessage(ChatColor.GREEN + "Warps reloaded successfully.");
                break;
            default:
                sender.sendMessage(ChatColor.RED + "/creload <banneditems | config | loops | warps>");
        }
    }

    @Override
    protected List<String> handleTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Stream.of("banneditems", "config", "loops", "plotwarps", "warps").filter(arg -> args.length != 0 && arg.startsWith(args[0])).collect(Collectors.toList());
    }
}
