package network.palace.creative.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.message.FormattedMessage;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;

/**
 * Created by Marc on 12/27/15
 */
@CommandMeta(description = "View Creative Rules", rank = Rank.GUEST)
public class RulesCommand extends CoreCommand {
    private FormattedMessage msg = new FormattedMessage("\nClick to read the Creative Rules on our Website!\n")
            .color(ChatColor.YELLOW).style(ChatColor.BOLD).link("https://palace.network/creative/rules/");

    public RulesCommand() {
        super("rules");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        msg.send(player);
    }
}
