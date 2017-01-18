package network.palace.creative.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

@CommandMeta(description = "Get a full stack of the item in your hand")
@CommandPermission(rank = Rank.SQUIRE)
public class CommandMore extends CoreCommand {

    public CommandMore() {
        super("more");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        PlayerInventory pi = player.getInventory();
        if (pi.getItemInMainHand() == null || pi.getItemInMainHand().getType().equals(Material.AIR)) {
            player.sendMessage(ChatColor.RED + "There is nothing in your hand!");
            return;
        }
        ItemStack stack = pi.getItemInMainHand();
        stack.setAmount(64);
    }
}
