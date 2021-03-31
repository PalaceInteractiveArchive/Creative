package network.palace.creative.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.HeadUtil;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

@CommandMeta(description = "Give a player a head with their skin")
public class MyHeadCommand extends CoreCommand {

    public MyHeadCommand() {
        super("myhead");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        try {
            ItemStack head = HeadUtil.getPlayerHead(player.getTextureValue(), player.getRank().getTagColor() + player.getName() + "'s Head");
            if (head == null) throw new Exception();
            player.getInventory().addItem(head);
            player.sendMessage(ChatColor.GREEN + "You've been given a head that looks like you!");
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "There was an error getting your head, sorry!");
        }
    }
}
