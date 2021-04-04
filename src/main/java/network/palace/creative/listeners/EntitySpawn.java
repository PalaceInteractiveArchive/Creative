package network.palace.creative.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Marc on 1/5/17.
 */
public class EntitySpawn implements Listener {
    public static List<EntityType> allowed = Arrays.asList(EntityType.PLAYER, EntityType.ARMOR_STAND, EntityType.ITEM_FRAME,
            EntityType.ARROW, EntityType.BOAT, EntityType.DROPPED_ITEM, EntityType.EXPERIENCE_ORB, EntityType.FALLING_BLOCK,
            EntityType.FIREBALL, EntityType.FIREWORK, EntityType.FISHING_HOOK, EntityType.LIGHTNING, EntityType.PAINTING,
            EntityType.SPECTRAL_ARROW, EntityType.MINECART, EntityType.MINECART_CHEST, EntityType.MINECART_FURNACE, EntityType.MINECART_HOPPER);

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (!allowed.contains(event.getEntityType())) {
            event.setCancelled(true);
        }
    }
}
