package network.palace.creative.utils;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.ItemUtil;
import network.palace.creative.Creative;
import network.palace.creative.handlers.Warp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

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
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
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
                    warp.getString("world"), Rank.GUEST);
            map.put(uuid, w);
        });
    }

    public void openWarpsMenu(CPlayer player, int page) {
        List<MenuButton> buttons = new ArrayList<>();
        List<Entry<UUID, Warp>> warps = new ArrayList<>(this.warps.entrySet());
        for (int x = 0; x < 45; x++) {
            try {
                Entry<UUID, Warp> warp = warps.get(x + (page - 1) * 45);
                Warp w = warp.getValue();
                ItemStack itemStack = ItemUtil.create(Material.ENDER_EYE, w.getName(), Collections.singletonList(ChatColor.YELLOW + "Submitted by " + Bukkit.getOfflinePlayer(warp.getKey()).getName()));
                buttons.add(new MenuButton(x, itemStack, ImmutableMap.of(ClickType.LEFT, p -> {
                    p.closeInventory();
                    p.teleport(w.getLocation());
                    p.sendMessage(ChatColor.GREEN + "You have been warped to " + ChatColor.GOLD + w.getName());
                })));
            } catch (IndexOutOfBoundsException ignored) {

            }
        }

        if (MenuUtil.isStaff(player) && player.getRank() != Rank.TRAINEE) {
            buttons.add(new MenuButton(47, ItemUtil.create(Material.ENDER_PEARL, ChatColor.GREEN + "Review Warps"), ImmutableMap.of(ClickType.LEFT, p -> openWarpsReviewMenu(p, 1))));
        }

        MenuUtil menuUtil = Creative.getInstance().getMenuUtil();
        if (page > 1) {
            buttons.add(new MenuButton(45, menuUtil.last, ImmutableMap.of(ClickType.LEFT, p -> openWarpsMenu(p, page - 1))));
        }
        buttons.add(new MenuButton(49, menuUtil.back, ImmutableMap.of(ClickType.LEFT, CPlayer::closeInventory)));
        if (page <= (int) (Math.ceil(warps.size() / 45D))) {
            buttons.add(new MenuButton(53, menuUtil.next, ImmutableMap.of(ClickType.LEFT, p -> openWarpsMenu(p, page + 1))));
        }
        new Menu(54, ChatColor.BLUE + "Plot Warps", player, buttons).open();
    }

    private void openWarpsReviewMenu(CPlayer player, int page) {
        List<MenuButton> buttons = new ArrayList<>();
        List<Entry<UUID, Warp>> pending = new ArrayList<>(pendingWarps.entrySet());
        for (int x = 0; x < 45; x++) {
            try {
                Entry<UUID, Warp> pendingWarp = pending.get(x + (page - 1) * 45);
                UUID uuid = pendingWarp.getKey();
                Warp warp = pendingWarp.getValue();
                ItemStack itemStack = ItemUtil.create(Material.ENDER_EYE, ChatColor.GREEN + warp.getName(), Arrays.asList(ChatColor.YELLOW + "Submitted by " + Bukkit.getOfflinePlayer(uuid).getName(),
                        ChatColor.GREEN + "Left-Click" + ChatColor.YELLOW + " to approve.", ChatColor.RED + "Right-Click" + ChatColor.YELLOW + " to deny."));
                buttons.add(new MenuButton(x, itemStack, ImmutableMap.of(ClickType.LEFT, p -> {
                    p.sendMessage(ChatColor.GREEN + "Warp approved!");
                    pendingWarps.remove(uuid);
                    warps.put(uuid, warp);
                    try {
                        save();
                    } catch (IOException e) {
                        p.sendMessage(ChatColor.RED + "An error has occurred. Please alert a dev!");
                        e.printStackTrace();
                        return;
                    }

                    openWarpsReviewMenu(p, page);
                }, ClickType.RIGHT, p -> {
                    pendingWarps.remove(uuid);
                    p.sendMessage(ChatColor.RED + "Warp denied!");
                    try {
                        save();
                    } catch (IOException e) {
                        p.sendMessage(ChatColor.RED + "An error has occurred. Please alert a dev!");
                        e.printStackTrace();
                        return;
                    }

                    openWarpsReviewMenu(p, page);
                })));
            } catch (IndexOutOfBoundsException ignored) {

            }
        }

        MenuUtil menuUtil = Creative.getInstance().getMenuUtil();
        if (page > 1) {
            buttons.add(new MenuButton(45, menuUtil.last, ImmutableMap.of(ClickType.LEFT, p -> openWarpsReviewMenu(p, page - 1))));
        }

        buttons.add(new MenuButton(49, menuUtil.back, ImmutableMap.of(ClickType.LEFT, p -> openWarpsMenu(p, 1))));
        if (page <= (int) (Math.ceil(pendingWarps.size() / 45D))) {
            buttons.add(new MenuButton(53, menuUtil.next, ImmutableMap.of(ClickType.LEFT, p -> openWarpsReviewMenu(p, page + 1))));
        }

        new Menu(54, ChatColor.BLUE + "Pending Plot Warps", player, buttons).open();
    }

    public void submitWarp(String name, Player player) throws IOException {
        Location loc = player.getLocation();
        pendingWarps.put(player.getUniqueId(), new Warp(name, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(), player.getWorld().getName(), Rank.GUEST));
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

        YamlConfiguration yaml = new YamlConfiguration();
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
            yaml.set(path + "rank", Rank.GUEST.toString());
        });
    }

    public void removeWarp(Warp warp) {
        UUID uuid = getWarpOwner(warp);
        if (uuid != null) {
            warps.remove(uuid);
        }
    }

    public UUID getWarpOwner(Warp warp) {
        return HashBiMap.create(warps).inverse().get(warp);
    }
}
