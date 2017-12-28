package network.palace.creative.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.creative.listeners.PlayerJoinAndLeave;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.PlayerInventory;

/**
 * Created by Marc on 6/27/16
 */
@CommandMeta(description = "Replace the Creative Star")
@CommandPermission(rank = Rank.SETTLER)
public class StarCommand extends CoreCommand {

    public StarCommand() {
        super("star");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        PlayerInventory inv = player.getInventory();
        inv.remove(Material.NETHER_STAR);
        inv.setItem(8, PlayerJoinAndLeave.star);
        player.sendMessage(ChatColor.GREEN + "You have been given the Creative Menu again!");
    }
}