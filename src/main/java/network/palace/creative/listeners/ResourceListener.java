package network.palace.creative.listeners;

import network.palace.core.Core;
import network.palace.core.events.CurrentPackReceivedEvent;
import network.palace.core.player.CPlayer;
import network.palace.creative.Creative;
import network.palace.creative.handlers.PlayerData;
import org.bukkit.Bukkit;
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
        PlayerData data = Creative.getInstance().getPlayerData(player.getUniqueId());
        String preferred = data.getResourcePack();
        if (!preferred.equals("NoPrefer")) {
            Runnable openPackSelector = () -> Creative.getInstance().getResourceUtil().openMenu(player);
            if (preferred.equals("none")) {
                //Choose a pack
                player.sendMessage(ChatColor.GREEN + "Please choose a Resource Pack option for Creative.");
                Bukkit.getScheduler().runTaskLater(Creative.getInstance(), openPackSelector, 20L);
            } else if (Core.getResourceManager().getPack(preferred) == null) {
                player.sendMessage(ChatColor.RED + "Your chosen Resource Pack is not available, choose a new one!");
                //Choose a pack
                Bukkit.getScheduler().runTaskLater(Creative.getInstance(), openPackSelector, 20L);
            } else if (preferred.equals("Blank") && !current.equals("none")) {
                //Send blank
                Core.getResourceManager().sendPack(player, "Blank");
            } else if (!current.equalsIgnoreCase(preferred)) {
                Core.getResourceManager().sendPack(player, preferred);
            }
        }
    }
}
