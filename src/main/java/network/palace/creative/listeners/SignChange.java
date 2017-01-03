package network.palace.creative.listeners;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

/**
 * Created by Marc on 2/8/15
 */
public class SignChange implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (event.getLine(0).equalsIgnoreCase("[show]")) {
            event.setLine(0, ChatColor.BLUE + "[Show]");
            event.setLine(1, "Click to sync");
            event.setLine(2, "your music!");
            event.setLine(3, "");
            return;
        }
        if (event.getLine(0).equals("&9[Show]")) {
            return;
        }
        for (int i = 0; i < 4; i++) {
            event.setLine(i, ChatColor.translateAlternateColorCodes('&', event.getLine(i)));
        }
    }
}
