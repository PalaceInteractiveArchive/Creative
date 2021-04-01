package network.palace.creative.listeners;

import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import com.comphenix.protocol.wrappers.nbt.NbtFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marc on 2/8/15
 */
public class BlockEdit implements Listener {
    private final List<Material> blockBlackList = new ArrayList<>();
    private final List<Material> itemBlackList = new ArrayList<>();

    public BlockEdit() {
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
        itemBlackList.add(Material.ZOMBIFIED_PIGLIN_SPAWN_EGG);
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
        if (player.getRank().getRankId() < Rank.TRAINEE.getRankId()) {
            Block block = event.getBlock();
            if (blockBlackList.contains(block.getType()) || block.getType().name().toLowerCase().contains("lava")) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You are not permitted to place " + ChatColor.GREEN + block.getType().toString() + "!");
            }
        } else {
            if (!event.isCancelled() && event.getBlock().getType().equals(Material.PLAYER_HEAD)) {
                ItemStack item = player.getItemInMainHand();
                if (!item.getType().equals(Material.PLAYER_HEAD)) {
                    event.setCancelled(true);
                    player.getInventory().setItem(player.getHeldItemSlot(), new ItemStack(Material.AIR));
                } else {
                    boolean valid = false;
                    try {
                        NbtCompound compound = (NbtCompound) NbtFactory.fromItemTag(item);
                        JsonReader reader = new JsonReader(new StringReader(String.valueOf(compound.getValue("SkullOwner").getValue())));
                        reader.setLenient(true);
                        JsonObject object = (JsonObject) new JsonParser().parse(reader);
                        JsonObject properties = object.getAsJsonObject("Properties");
                        JsonObject textures = properties.getAsJsonObject("textures");
                        JsonArray value = textures.getAsJsonArray("value");
                        JsonObject entry = (JsonObject) value.get(0);
                        String texture = entry.get("Value").getAsString();
                        if (texture.equals(player.getTextureValue()) || Creative.getInstance().getHeadUtil().getHashes().contains(texture)) {
                            valid = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        valid = false;
                    }
                    if (!valid) {
                        event.setCancelled(true);
                        player.getInventory().setItem(player.getHeldItemSlot(), new ItemStack(Material.AIR));
                        player.sendMessage(ChatColor.RED + "You do not have permission to place that head!");
                        player.sendMessage(ChatColor.AQUA + "You can place heads from the Head Shop, or from /myhead.");
                    }
                }
            }
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
            PlayerInventory inv = player.getInventory();
            Material main = inv.getItemInMainHand().getType();
            Material offhand = inv.getItemInOffHand().getType();
            if (itemBlackList.contains(main)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You are not permitted to interact with " +
                        ChatColor.GREEN + main.toString() + "!");
            } else if (itemBlackList.contains(offhand)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You are not permitted to interact with " +
                        ChatColor.GREEN + offhand.toString() + "!");
            }
        }
    }
}
