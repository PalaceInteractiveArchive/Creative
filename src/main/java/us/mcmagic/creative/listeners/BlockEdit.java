package us.mcmagic.creative.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;

import java.util.ArrayList;
import java.util.List;

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
        blockBlackList.add(Material.STATIONARY_LAVA);
        blockBlackList.add(Material.BEACON);
        blockBlackList.add(Material.BEDROCK);
        blockBlackList.add(Material.COMMAND);
        blockBlackList.add(Material.DRAGON_EGG);
        blockBlackList.add(Material.FIRE);
        blockBlackList.add(Material.ENDER_PORTAL);
        itemBlackList.add(Material.MONSTER_EGG);
        itemBlackList.add(Material.MONSTER_EGGS);
        itemBlackList.add(Material.SNOW_BALL);
        itemBlackList.add(Material.ENDER_PEARL);
        itemBlackList.add(Material.EXPLOSIVE_MINECART);
        itemBlackList.add(Material.EYE_OF_ENDER);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        User user = MCMagicCore.getUser(player.getUniqueId());
        if (user == null) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot place blocks right now! (Error Code 107)");
            return;
        }
        if (user.getRank().getRankId() < Rank.KNIGHT.getRankId()) {
            Block block = event.getBlock();
            if (blockBlackList.contains(block.getType()) || block.getType().name().toLowerCase().contains("lava")) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You are not permitted to place " + ChatColor.GREEN +
                        block.getType().toString() + "!");
            }
        } else {
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
        Entity entity = event.getEntity();
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        User user = MCMagicCore.getUser(player.getUniqueId());
        if (user == null) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot place blocks right now! (Error Code 107)");
            return;
        }
        if (user.getRank().getRankId() < Rank.KNIGHT.getRankId()) {
            if (event.getBlock().getType().equals(Material.BEDROCK)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot place or break Bedrock!");
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        User user = MCMagicCore.getUser(player.getUniqueId());
        if (user == null) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You cannot place blocks right now! (Error Code 107)");
            return;
        }
        if (user.getRank().getRankId() < Rank.KNIGHT.getRankId()) {
            if (itemBlackList.contains(player.getItemInHand().getType())) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You are not permitted to interact with " + ChatColor.GREEN +
                        player.getItemInHand().getType().toString() + "!");
            }
        }
    }
}
