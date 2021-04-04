package network.palace.creative.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
import org.bukkit.ChatColor;

import java.io.IOException;

@CommandMeta(description = "Set the Creative MOTD", rank = Rank.CM)
public class SetMOTDCommand extends CoreCommand {

    public SetMOTDCommand() {
        super("setmotd");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/setmotd [MOTD]");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "The MOTD is Green by default, and supports color codes.");
            player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.ITALIC + "Run '/setmotd blank' to disable the MOTD.");
            return;
        }
        StringBuilder msg = new StringBuilder();
        for (String arg : args) {
            msg.append(arg).append(" ");
        }
        String motd;
        if (msg.toString().trim().equalsIgnoreCase("blank")) motd = "blank";
        else motd = ChatColor.AQUA + ChatColor.translateAlternateColorCodes('&', msg.toString().trim());
        try {
            Creative.getInstance().setMotd(motd);
            if (motd.equals("blank")) player.sendMessage(ChatColor.GREEN + "Disabled the Creative MOTD!");
            else player.sendMessage(ChatColor.GREEN + "Set the Creative MOTD to: " + motd);
        } catch (IOException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "Failed to set the Creative MOTD! Check console for errors.");
        }
    }
}
