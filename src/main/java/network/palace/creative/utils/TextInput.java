package network.palace.creative.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import network.palace.creative.Creative;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TextInput implements Listener {

    private static final List<UUID> SESSIONS = new ArrayList<>();
    private final Player player;
    private final BiConsumer<Player, String> action;

    public TextInput(Player player, BiConsumer<Player, String> action) {
        this.player = player;
        this.action = action;
        SESSIONS.add(player.getUniqueId());
        Bukkit.getPluginManager().registerEvents(this, Creative.getInstance());
    }

    public static boolean hasSession(Player player) {
        return SESSIONS.contains(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player.getUniqueId().equals(this.player.getUniqueId())) {
            event.setCancelled(true);
            action.accept(player, event.getMessage());
            HandlerList.unregisterAll(this);
            SESSIONS.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        HandlerList.unregisterAll(this);
        SESSIONS.remove(event.getPlayer().getUniqueId());
    }
}
