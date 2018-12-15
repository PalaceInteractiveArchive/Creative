package network.palace.creative.utils;

import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.resource.ResourcePack;
import network.palace.core.utils.ItemUtil;
import network.palace.creative.Creative;
import network.palace.creative.handlers.PlayerData;
import network.palace.creative.inventory.Menu;
import network.palace.creative.inventory.MenuButton;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

    public void openMenu(CPlayer player) {
        List<MenuButton> buttons = new ArrayList<>();
        String selected = Creative.getInstance().getPlayerData(player.getUniqueId()).getResourcePack();
        if (selected == null) {
            selected = "none";
        }

        PlayerData data = Creative.getInstance().getPlayerData(player.getUniqueId());
        buttons.add(new MenuButton(8, selected.equalsIgnoreCase("NoPrefer") ? noPreferSelected : noPrefer, ImmutableMap.of(ClickType.LEFT, p -> {
            Core.getMongoHandler().setCreativeValue(player.getUniqueId(), "pack", "NoPrefer");
            data.setResourcePack("NoPrefer");
            player.sendMessage(ChatColor.GREEN + "You will not be sent a Resource Pack when you join Creative!");
            player.closeInventory();
        })));

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

            buttons.add(new MenuButton(place, i, getResourcePackAction(ChatColor.stripColor(i.getItemMeta().getDisplayName()), data)));
            if (add) {
                place += a;
            } else {
                place -= a;
            }

            a++;
            add = !add;
        }

        new Menu(Bukkit.createInventory(player.getBukkitPlayer(), 27, ChatColor.BLUE + "Resource Pack"), player.getBukkitPlayer(), buttons);
    }

    private ImmutableMap<ClickType, Consumer<Player>> getResourcePackAction(String name, PlayerData data) {
        return ImmutableMap.of(ClickType.LEFT, p -> {
            CPlayer cPlayer = Core.getPlayerManager().getPlayer(p);
            ResourcePack pack = Core.getResourceManager().getPack(name);
            if (pack == null) {
                p.sendMessage(ChatColor.RED + "We couldn't find the pack you clicked on! Try another one.");
                p.closeInventory();
                return;
            }
            data.setResourcePack(pack.getName());
            Bukkit.getScheduler().runTaskAsynchronously(Creative.getInstance(), () -> {
                p.sendMessage(ChatColor.GREEN + "You set your Creative Resource Pack to " + ChatColor.YELLOW +
                        pack.getName() + ChatColor.GREEN + "! It will automatically download when you join Creative.");
                Core.getMongoHandler().setCreativeValue(p.getUniqueId(), "pack", pack.getName());
                p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 0);
                p.closeInventory();
                if (!cPlayer.getPack().equalsIgnoreCase(pack.getName())) {
                    Core.getResourceManager().sendPack(cPlayer, pack);
                }
            });
        });
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
