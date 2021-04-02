package network.palace.creative.listeners;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.ItemUtil;
import network.palace.creative.Creative;
import network.palace.creative.handlers.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Marc on 6/12/15
 */
public class InventoryClick implements Listener {
    private List<Material> clickBlacklist = Arrays.asList(Material.SPAWNER, Material.NETHER_PORTAL, Material.END_PORTAL,
            Material.DRAGON_EGG, Material.COMMAND_BLOCK, Material.CHAIN_COMMAND_BLOCK, Material.REPEATING_COMMAND_BLOCK,
            Material.COMMAND_BLOCK_MINECART, Material.END_GATEWAY, Material.END_CRYSTAL,
            Material.STRUCTURE_BLOCK, Material.STRUCTURE_VOID);
    private ItemStack air = ItemUtil.create(Material.AIR);

    @EventHandler
    public void onInventoryCreative(InventoryCreativeEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer((Player) event.getWhoClicked());
        if (player == null)
            return;
        if (player.getRank().getRankId() >= Rank.TRAINEE.getRankId())
            return;
        ItemStack cursor = event.getCursor();
        Material type = cursor.getType();
        if (type != null && clickBlacklist.contains(type)) {
            event.setCancelled(true);
            event.setCursor(air);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        long t = System.currentTimeMillis();
        Inventory inv = event.getClickedInventory();

        if (inv == null) {
            return;
        }

        if (event.getWhoClicked() instanceof Player) {
            PlayerData data = Creative.getInstance().getPlayerData(event.getWhoClicked().getUniqueId());
            if (data != null) {
                data.resetAction();
            }
        }

        long t2 = System.currentTimeMillis();
        long diff = t2 - t;
        if (diff >= 500) {
            for (CPlayer cp : Core.getPlayerManager().getOnlinePlayers()) {
                if (cp == null)
                    continue;
                if (cp.getRank().getRankId() >= Rank.DEVELOPER.getRankId()) {
                    cp.sendMessage(ChatColor.RED + "Click event took " + diff + "ms! " + ChatColor.GREEN + event.getWhoClicked().getName());
                }
            }
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getView().getType().equals(InventoryType.SHULKER_BOX)) event.setCancelled(true);
    }
}
