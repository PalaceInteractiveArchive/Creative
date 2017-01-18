package network.palace.creative.utils;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.resource.ResourcePack;
import network.palace.core.utils.ItemUtil;
import network.palace.creative.Creative;
import network.palace.creative.handlers.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Marc on 1/3/17.
 */
public class ResourceUtil {
    private Random random = new Random();
    private HashMap<String, ItemStack> packs = new HashMap<>();
    private ItemStack noPrefer = ItemUtil.create(Material.BARRIER, ChatColor.RED + "No Resource Pack",
            Arrays.asList(ChatColor.GRAY + "No pack will be sent to you", ChatColor.GRAY + "when you connect to Creative"));
    private ItemStack noPreferSelected = ItemUtil.create(Material.BARRIER, ChatColor.RED + "No Resource Pack",
            Arrays.asList(ChatColor.LIGHT_PURPLE + "(SELECTED)", ChatColor.GRAY + "No pack will be sent to you",
                    ChatColor.GRAY + "when you connect to Creative"));

    public ResourceUtil() {
        loadPacks();
    }

    public void loadPacks() {
        packs.clear();
        File f = new File("plugins/Creative/packs.yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Core.logMessage("Creative", ChatColor.RED + "No Resource Packs defined!");
            return;
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
        List<String> s = config.getStringList("packs");
        if (s == null || s.isEmpty()) {
            Core.logMessage("Creative", ChatColor.RED + "No Resource Packs defined!");
            return;
        }
        for (String p : s) {
            ResourcePack pack = Core.getResourceManager().getPack(p);
            if (pack == null) {
                Core.logMessage("Creative", ChatColor.RED + "Could not load resource pack " +
                        ChatColor.GREEN + p + ChatColor.RED + ", it doesn't exist!");
                continue;
            }
            List<String> a = p.equals("Blank") ? Arrays.asList(ChatColor.GRAY + "Remove all packs downloaded",
                    ChatColor.GRAY + "while on our servers and", ChatColor.GRAY + "use default Minecraft or",
                    ChatColor.GRAY + "your own Resource Pack!") : new ArrayList<>();
            packs.put(p, ItemUtil.create(randomDisc(), ChatColor.GREEN + p, a));
        }
    }

    public void handle(InventoryClickEvent event) {
        final CPlayer player = Core.getPlayerManager().getPlayer((Player) event.getWhoClicked());
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || meta.getDisplayName() == null) {
            return;
        }
        String name = ChatColor.stripColor(meta.getDisplayName());
        event.setCancelled(true);
        PlayerData data = Creative.getInstance().getPlayerData(player.getUniqueId());
        if (name.equalsIgnoreCase("No Resource Pack")) {
            Creative.getInstance().getSqlUtil().setResourcePack(player.getUniqueId(), "NoPrefer");
            data.setResourcePack("NoPrefer");
            player.sendMessage(ChatColor.GREEN + "You will not be sent a Resource Pack when you join Creative!");
            player.closeInventory();
            return;
        }
        ResourcePack pack = Core.getResourceManager().getPack(name);
        if (pack == null) {
            player.sendMessage(ChatColor.RED + "We couldn't find the pack you clicked on! Try another one.");
            player.closeInventory();
            return;
        }
        data.setResourcePack(pack.getName());
        Bukkit.getScheduler().runTaskAsynchronously(Creative.getInstance(), () -> {
            player.sendMessage(ChatColor.GREEN + "You set your Creative Resource Pack to " + ChatColor.YELLOW +
                    pack.getName() + ChatColor.GREEN + "! It will automatically download when you join Creative.");
            Creative.getInstance().getSqlUtil().setResourcePack(player.getUniqueId(), pack.getName());
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 0);
            player.closeInventory();
            if (!player.getPack().equalsIgnoreCase(pack.getName())) {
                Core.getResourceManager().sendPack(player, pack);
            }
        });
    }

    public void openMenu(CPlayer player) {
        Inventory inv = Bukkit.createInventory(player.getBukkitPlayer(), 27, ChatColor.BLUE + "Resource Pack");
        String selected = Creative.getInstance().getPlayerData(player.getUniqueId()).getResourcePack();
        if (selected.equalsIgnoreCase("NoPrefer")) {
            inv.setItem(8, noPreferSelected);
        } else {
            inv.setItem(8, noPrefer);
        }
        int place = 13;
        int a = 1;
        boolean add = true;
        for (Map.Entry<String, ItemStack> entry : packs.entrySet()) {
            ItemStack i = entry.getValue().clone();
            if (entry.getKey().equalsIgnoreCase(selected)) {
                ItemMeta m = i.getItemMeta();
                List<String> l = m.getLore() == null ? new ArrayList<>() : m.getLore();
                if (!l.isEmpty()) {
                    l.set(0, ChatColor.LIGHT_PURPLE + "(SELECTED)");
                } else {
                    l.add(ChatColor.LIGHT_PURPLE + "(SELECTED)");
                }
                m.setLore(l);
                i.setItemMeta(m);
            }
            inv.setItem(place, i);
            if (add) {
                place += a;
            } else {
                place -= a;
            }
            a++;
            add = !add;
        }
        player.openInventory(inv);
    }

    public Material randomDisc() {
        switch (random.nextInt(12) + 1) {
            case 1:
                return Material.GOLD_RECORD;
            case 2:
                return Material.GREEN_RECORD;
            case 3:
                return Material.RECORD_3;
            case 4:
                return Material.RECORD_4;
            case 5:
                return Material.RECORD_5;
            case 6:
                return Material.RECORD_6;
            case 7:
                return Material.RECORD_7;
            case 8:
                return Material.RECORD_8;
            case 9:
                return Material.RECORD_9;
            case 10:
                return Material.RECORD_10;
            case 11:
                return Material.RECORD_11;
            default:
                return Material.RECORD_12;
        }
    }
}
