package network.palace.creative.utils;

import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import network.palace.core.Core;
import network.palace.core.player.Rank;
import network.palace.core.utils.ItemUtil;
import network.palace.creative.Creative;
import network.palace.creative.handlers.Warp;
import network.palace.creative.inventory.Menu;
import network.palace.creative.inventory.MenuButton;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class PlotWarpUtil {

    private Map<UUID, Warp> warps = new HashMap<>();
    private Map<UUID, Warp> pendingWarps = new HashMap<>();

    public PlotWarpUtil() {
        load();
    }

    public Optional<Warp> getPendingWarp(String name) {
        return getWarp(name, pendingWarps.values());
    }

    public Optional<Warp> getWarp(String name) {
        return getWarp(name, warps.values());
    }

    private Optional<Warp> getWarp(String name, Collection<Warp> warps) {
        return warps.stream().filter(warp -> name.equalsIgnoreCase(warp.getName())).findFirst();
    }

    public void load() {
        File file = new File(Creative.getInstance().getDataFolder(), "plot_warps.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            warps.clear();
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            loadWarps(yaml.getConfigurationSection("warps"), warps);
            loadWarps(yaml.getConfigurationSection("pending"), pendingWarps);
        }
    }

    private void loadWarps(ConfigurationSection cs, Map<UUID, Warp> map) {
        if (cs == null) {
            return;
        }

        cs.getKeys(false).forEach(name -> {
            ConfigurationSection warp = cs.getConfigurationSection(name);
            UUID uuid = UUID.fromString(warp.getString("submitter"));
            Warp w = new Warp(name, warp.getDouble("x"),
                    warp.getDouble("y"), warp.getDouble("z"),
                    (float) warp.getInt("yaw"), (float) warp.getInt("pitch"),
                    warp.getString("world"), Rank.SETTLER);
            map.put(uuid, w);
        });
    }

    public void openWarpsMenu(Player player, int page) {
        List<MenuButton> buttons = new ArrayList<>();
        List<Entry<UUID, Warp>> warps = new ArrayList<>(this.warps.entrySet());
        for (int x = 0; x < 45; x++) {
            try {
                Entry<UUID, Warp> warp = warps.get(x + (page - 1) * 45);
                Warp w = warp.getValue();
                ItemStack itemStack = ItemUtil.create(Material.EYE_OF_ENDER, w.getName(), Arrays.asList(ChatColor.YELLOW + "Submitted by " + Bukkit.getOfflinePlayer(warp.getKey()).getName()));
                buttons.add(new MenuButton(x, itemStack, ImmutableMap.of(ClickType.LEFT, () -> {
                    player.closeInventory();
                    player.teleport(w.getLocation());
                    player.sendMessage(ChatColor.GREEN + "You have been warped to " + ChatColor.GOLD + w.getName());
                })));
            }
            catch (IndexOutOfBoundsException ignored) {

            }
        }

        if (MenuUtil.isStaff(player) && Core.getPlayerManager().getPlayer(player).getRank() != Rank.TRAINEE) {
            buttons.add(new MenuButton(47, ItemUtil.create(Material.ENDER_PEARL, ChatColor.GREEN + "Review Warps"), ImmutableMap.of(ClickType.LEFT, () -> openWarpsReviewMenu(player, 1))));
        }

        MenuUtil menuUtil = Creative.getInstance().getMenuUtil();
        buttons.add(new MenuButton(45, menuUtil.last, ImmutableMap.of(ClickType.LEFT, () -> {
            if (page > 1) {
                openWarpsMenu(player, page - 1);
            }
        })));
        buttons.add(new MenuButton(49, menuUtil.back, ImmutableMap.of(ClickType.LEFT, player::closeInventory)));
        buttons.add(new MenuButton(53, menuUtil.next, ImmutableMap.of(ClickType.LEFT, () -> {
            if (page <= new Double(Math.ceil(warps.size() / 45D)).intValue()) {
                openWarpsMenu(player, page + 1);
            }
        })));
        new Menu(Bukkit.createInventory(player, 54, ChatColor.BLUE + "Plot Warps"), player, buttons);
    }

    private void openWarpsReviewMenu(Player player, int page) {
        List<MenuButton> buttons = new ArrayList<>();
        List<Entry<UUID, Warp>> pending = new ArrayList<>(pendingWarps.entrySet());
        for (int x = 0; x < 45; x++) {
            try {
                Entry<UUID, Warp> pendingWarp = pending.get(x + (page - 1) * 45);
                UUID uuid = pendingWarp.getKey();
                Warp warp = pendingWarp.getValue();
                ItemStack itemStack = ItemUtil.create(Material.EYE_OF_ENDER, ChatColor.GREEN + warp.getName(), Arrays.asList(ChatColor.YELLOW + "Submitted by " + Bukkit.getOfflinePlayer(uuid).getName(),
                        ChatColor.GREEN + "Left-Click" + ChatColor.YELLOW + " to approve.", ChatColor.RED + "Right-Click" + ChatColor.YELLOW + " to deny."));
                buttons.add(new MenuButton(x, itemStack, ImmutableMap.<ClickType, Runnable>builder().put(ClickType.LEFT, () -> {
                    player.sendMessage(ChatColor.GREEN + "Warp approved!");
                    pendingWarps.remove(uuid);
                    warps.put(uuid, warp);
                    try {
                        save();
                    }
                    catch (IOException e) {
                        player.sendMessage(ChatColor.RED + "An error has occurred. Please alert a dev!");
                        e.printStackTrace();
                        return;
                    }

                    openWarpsReviewMenu(player, page);
                }).put(ClickType.RIGHT, () -> {
                    pendingWarps.remove(uuid);
                    player.sendMessage(ChatColor.RED + "Warp denied!");
                    try {
                        save();
                    }
                    catch (IOException e) {
                        player.sendMessage(ChatColor.RED + "An error has occurred. Please alert a dev!");
                        e.printStackTrace();
                        return;
                    }

                    openWarpsReviewMenu(player, page);
                }).build()));
            }
            catch (IndexOutOfBoundsException ignored) {

            }
        }

        MenuUtil menuUtil = Creative.getInstance().getMenuUtil();
        buttons.add(new MenuButton(45, menuUtil.last, ImmutableMap.of(ClickType.LEFT, () -> {
            if (page > 1) {
                openWarpsReviewMenu(player, page - 1);
            }
        })));
        buttons.add(new MenuButton(49, menuUtil.back, ImmutableMap.of(ClickType.LEFT, () -> openWarpsMenu(player, 1))));
        buttons.add(new MenuButton(53, menuUtil.next, ImmutableMap.of(ClickType.LEFT, () -> {
            if (page <= new Double(Math.ceil(pendingWarps.size() / 45D)).intValue()) {
                openWarpsReviewMenu(player, page + 1);
            }
        })));
        new Menu(Bukkit.createInventory(player, 54, ChatColor.BLUE + "Pending Plot Warps"), player, buttons);
    }

    public void submitWarp(String name, Player player) throws IOException {
        Location loc = player.getLocation();
        pendingWarps.put(player.getUniqueId(), new Warp(name, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(), player.getWorld().getName(), Rank.SETTLER));
        File file = new File(Creative.getInstance().getDataFolder(), "plot_warps.yml");
        if (!file.exists()) {
            file.createNewFile();
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        saveWarps(pendingWarps, yaml, "pending");
        yaml.save(file);
    }

    public void save() throws IOException {
        File file = new File(Creative.getInstance().getDataFolder(), "plot_warps.yml");
        if (!file.exists()) {
            file.createNewFile();
        }

        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        saveWarps(warps, yaml, "warps");
        saveWarps(pendingWarps, yaml, "pending");
        yaml.save(file);
    }

    private void saveWarps(Map<UUID, Warp> warps, YamlConfiguration yaml, String type) {
        warps.forEach((uuid, warp) -> {
            String path = type + "." + warp.getName() + ".";
            yaml.set(path + "submitter", uuid.toString());
            yaml.set(path + "x", warp.getX());
            yaml.set(path + "y", warp.getY());
            yaml.set(path + "z", warp.getZ());
            yaml.set(path + "yaw", warp.getYaw());
            yaml.set(path + "pitch", warp.getPitch());
            yaml.set(path + "world", warp.getWorld().getName());
            yaml.set(path + "rank", Rank.SETTLER.toString());
        });
    }
}
