package network.palace.creative.inventory;

import java.util.List;
import java.util.Optional;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.creative.Creative;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

//This class would exist in Core if we were to use it in all of our static menus
public class Menu implements Listener {

    private final List<MenuButton> menuButtons;
    private final Inventory inventory;
    private final Player player;

    public Menu(Inventory inventory, Player player, List<MenuButton> buttons) {
        this.inventory = inventory;
        this.player = player;
        this.menuButtons = buttons;
        open();
    }

    public Optional<MenuButton> getButton(int slot) {
        return menuButtons.stream().filter(b -> b.getSlot() == slot).findFirst();
    }

    public void open() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Creative.getInstance(), () -> {
            inventory.clear();
            menuButtons.forEach(button -> inventory.setItem(button.getSlot(), button.getItemStack()));
            player.openInventory(inventory);
            Bukkit.getPluginManager().registerEvents(this, Creative.getInstance());
        });
    }

    public void removeButton(int slot) {
        menuButtons.removeIf(b -> b.getSlot() == slot);
        inventory.setItem(22, null);
    }

    public void setButton(MenuButton button) {
        menuButtons.removeIf(b -> b.getSlot() == button.getSlot());
        menuButtons.add(button);
        inventory.setItem(button.getSlot(), button.getItemStack());
    }

    @EventHandler
    public void click(InventoryClickEvent event) {
        long t = System.currentTimeMillis();
        Inventory inv = event.getClickedInventory();
        if (isSameInventory(inv)) {
            event.setCancelled(true);
            menuButtons.stream().filter(button -> button.getSlot() == event.getRawSlot() && button.getActions().containsKey(event.getClick())).findFirst().map(menuButton -> menuButton.getActions().get(event.getClick())).ifPresent(action -> action.accept((Player) event.getWhoClicked()));

            long t2 = System.currentTimeMillis();
            long diff = t2 - t;
            if (diff >= 500) {
                for (CPlayer cp : Core.getPlayerManager().getOnlinePlayers()) {
                    if (cp == null)
                        continue;
                    if (cp.getRank().getRankId() >= Rank.DEVELOPER.getRankId()) {
                        cp.sendMessage(ChatColor.RED + "Click event took " + diff + "ms! " + ChatColor.GREEN +
                                event.getWhoClicked().getName() + " " + ChatColor.stripColor(inv.getTitle()) + " ");
                    }
                }
            }
        }
    }

    @EventHandler
    public void close(InventoryCloseEvent event) {
        if (isSameInventory(event.getInventory())) {
            HandlerList.unregisterAll(this);
        }
    }

    private boolean isSameInventory(Inventory inventory) {
        if (inventory == null) {
            return false;
        }

        if (this.inventory == null) {
            return false;
        }

        return inventory.getName().equals(this.inventory.getName()) && inventory.getViewers().stream().anyMatch(p -> p.getUniqueId().equals(player.getUniqueId()));
    }
}
