package network.palace.creative.listeners;

import network.palace.core.Core;
import network.palace.core.events.CurrentPackReceivedEvent;
import network.palace.core.player.CPlayer;
import network.palace.creative.Creative;
import network.palace.creative.handlers.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by Marc on 6/6/15
 */
public class ResourceListener implements Listener {

    @EventHandler
    public void onCurrentPackReceive(CurrentPackReceivedEvent event) {
        CPlayer player = event.getPlayer();
        String current = event.getPack();
        PlayerData data = Creative.getPlayerData(player.getUniqueId());
        String preferred = data.getResourcePack();
        if (Core.getResourceManager().getPack(preferred) == null) {
            player.sendMessage(ChatColor.RED + "Your chosen Resource Pack is not available, choose a new one!");
            //Choose a pack
            return;
        }
        if (preferred.equals("none")) {
            //Choose a pack
            return;
        }
        if (preferred.equals("Blank") && !current.equals("none")) {
            //Send blank
            return;
        }
        Core.getResourceManager().sendPack(player, preferred);
        player.sendMessage(ChatColor.GREEN + "Attempting to send you the " + ChatColor.YELLOW + preferred +
                ChatColor.GREEN + " Resource Pack...");
    }
}
