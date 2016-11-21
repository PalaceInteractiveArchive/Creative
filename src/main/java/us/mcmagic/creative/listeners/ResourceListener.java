package us.mcmagic.creative.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.player.User;
import us.mcmagic.mcmagiccore.resource.CurrentPackReceivedEvent;

/**
 * Created by Marc on 6/6/15
 */
public class ResourceListener implements Listener {

    @EventHandler
    public void onCurrentPackReceive(CurrentPackReceivedEvent event) {
        User user = event.getUser();
        Player player = Bukkit.getPlayer(user.getUniqueId());
        String current = event.getPacks();
        String preferred = user.getPreferredPack();
        if (preferred.equals("none") || preferred.equals("NoPrefer")) {
            if (!current.equals("none")) {
                MCMagicCore.resourceManager.sendPack(player, "Blank");
            }
            user.setPreferredPack("none");
            MCMagicCore.resourceManager.setCurrentPack(user, "none");
            return;
        }
        if (!current.equals(preferred)) {
            MCMagicCore.resourceManager.sendPack(player, preferred);
        }
    }
}
