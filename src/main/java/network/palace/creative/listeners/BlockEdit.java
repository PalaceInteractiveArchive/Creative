package network.palace.creative.listeners;

import java.util.ArrayList;
import java.util.List;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by Marc on 2/8/15
 */
public class BlockEdit implements Listener {

    private List<Material> blockBlackList = new ArrayList<>();
    private List<Material> itemBlackList = new ArrayList<>();

    public BlockEdit() {
        blockBlackList.add(Material.BARRIER);
        blockBlackList.add(Material.LAVA_BUCKET);
        blockBlackList.add(Material.LAVA);
        blockBlackList.add(Material.BEACON);
        blockBlackList.add(Material.BEDROCK);
        blockBlackList.add(Material.COMMAND_BLOCK);
        blockBlackList.add(Material.CHAIN_COMMAND_BLOCK);
        blockBlackList.add(Material.DRAGON_EGG);
        blockBlackList.add(Material.FIRE);
        blockBlackList.add(Material.END_PORTAL);
        blockBlackList.add(Material.CHORUS_FLOWER);
        blockBlackList.add(Material.CHORUS_PLANT);
        blockBlackList.add(Material.END_CRYSTAL);
        blockBlackList.add(Material.INFESTED_CHISELED_STONE_BRICKS);
        blockBlackList.add(Material.INFESTED_COBBLESTONE);
        blockBlackList.add(Material.INFESTED_CRACKED_STONE_BRICKS);
        blockBlackList.add(Material.INFESTED_MOSSY_STONE_BRICKS);
        blockBlackList.add(Material.INFESTED_STONE);
        blockBlackList.add(Material.INFESTED_STONE_BRICKS);
        blockBlackList.add(Material.REPEATING_COMMAND_BLOCK);
        itemBlackList.add(Material.BAT_SPAWN_EGG);
        itemBlackList.add(Material.BLAZE_SPAWN_EGG);
        itemBlackList.add(Material.CAVE_SPIDER_SPAWN_EGG);
        itemBlackList.add(Material.CHICKEN_SPAWN_EGG);
        itemBlackList.add(Material.COD_SPAWN_EGG);
        itemBlackList.add(Material.COW_SPAWN_EGG);
        itemBlackList.add(Material.CREEPER_SPAWN_EGG);
        itemBlackList.add(Material.DOLPHIN_SPAWN_EGG);
        itemBlackList.add(Material.DONKEY_SPAWN_EGG);
        itemBlackList.add(Material.DROWNED_SPAWN_EGG);
        itemBlackList.add(Material.ELDER_GUARDIAN_SPAWN_EGG);
        itemBlackList.add(Material.ENDERMAN_SPAWN_EGG);
        itemBlackList.add(Material.ENDERMITE_SPAWN_EGG);
        itemBlackList.add(Material.EVOKER_SPAWN_EGG);
        itemBlackList.add(Material.GHAST_SPAWN_EGG);
        itemBlackList.add(Material.GUARDIAN_SPAWN_EGG);
        itemBlackList.add(Material.HORSE_SPAWN_EGG);
        itemBlackList.add(Material.HUSK_SPAWN_EGG);
        itemBlackList.add(Material.LLAMA_SPAWN_EGG);
        itemBlackList.add(Material.MAGMA_CUBE_SPAWN_EGG);
        itemBlackList.add(Material.MOOSHROOM_SPAWN_EGG);
        itemBlackList.add(Material.MULE_SPAWN_EGG);
        itemBlackList.add(Material.OCELOT_SPAWN_EGG);
        itemBlackList.add(Material.PARROT_SPAWN_EGG);
        itemBlackList.add(Material.PHANTOM_SPAWN_EGG);
        itemBlackList.add(Material.PIG_SPAWN_EGG);
        itemBlackList.add(Material.POLAR_BEAR_SPAWN_EGG);
        itemBlackList.add(Material.PUFFERFISH_SPAWN_EGG);
        itemBlackList.add(Material.RABBIT_SPAWN_EGG);
        itemBlackList.add(Material.SALMON_SPAWN_EGG);
        itemBlackList.add(Material.SHEEP_SPAWN_EGG);
        itemBlackList.add(Material.SHULKER_SPAWN_EGG);
        itemBlackList.add(Material.SILVERFISH_SPAWN_EGG);
        itemBlackList.add(Material.SKELETON_HORSE_SPAWN_EGG);
        itemBlackList.add(Material.SKELETON_SPAWN_EGG);
        itemBlackList.add(Material.SLIME_SPAWN_EGG);
        itemBlackList.add(Material.SPIDER_SPAWN_EGG);
        itemBlackList.add(Material.SQUID_SPAWN_EGG);
        itemBlackList.add(Material.STRAY_SPAWN_EGG);
        itemBlackList.add(Material.TROPICAL_FISH_SPAWN_EGG);
        itemBlackList.add(Material.TURTLE_SPAWN_EGG);
        itemBlackList.add(Material.VEX_SPAWN_EGG);
        itemBlackList.add(Material.VILLAGER_SPAWN_EGG);
        itemBlackList.add(Material.VINDICATOR_SPAWN_EGG);
        itemBlackList.add(Material.WITCH_SPAWN_EGG);
        itemBlackList.add(Material.WITHER_SKELETON_SPAWN_EGG);
        itemBlackList.add(Material.WOLF_SPAWN_EGG);
        itemBlackList.add(Material.ZOMBIE_HORSE_SPAWN_EGG);
        itemBlackList.add(Material.ZOMBIE_PIGMAN_SPAWN_EGG);
        itemBlackList.add(Material.ZOMBIE_SPAWN_EGG);
        itemBlackList.add(Material.ZOMBIE_VILLAGER_SPAWN_EGG);
        itemBlackList.add(Material.SNOWBALL);
        itemBlackList.add(Material.ENDER_PEARL);
        itemBlackList.add(Material.TNT_MINECART);
        itemBlackList.add(Material.ENDER_EYE);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (player == null) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot place blocks right now! (Error Code 107)");
            return;
        }
        if (player.getRank().getRankId() < Rank.MOD.getRankId()) {
            Block block = event.getBlock();
            if (blockBlackList.contains(block.getType()) || block.getType().name().toLowerCase().contains("lava")) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You are not permitted to place " + ChatColor.GREEN + block.getType().toString() + "!");
            }
        }
        else {
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        if (event.getEntity().getType().equals(EntityType.MINECART_TNT)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (player == null) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot place blocks right now! (Error Code 107)");
            return;
        }
        if (player.getRank().getRankId() < Rank.MOD.getRankId()) {
            if (event.getBlock().getType().equals(Material.BEDROCK)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot place or break Bedrock!");
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (player == null) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot place blocks right now! (Error Code 107)");
            return;
        }
        if (player.getRank().getRankId() < Rank.MOD.getRankId()) {
            if (itemBlackList.contains(player.getBukkitPlayer().getInventory().getItemInMainHand().getType())) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You are not permitted to interact with " + ChatColor.GREEN + player.getBukkitPlayer().getInventory().getItemInMainHand().getType().toString() + "!");
            }
        }
    }
}
