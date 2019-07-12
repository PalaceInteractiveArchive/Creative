package network.palace.creative.plotreview;

import com.github.intellectualsites.plotsquared.bukkit.util.BukkitUtil;
import com.github.intellectualsites.plotsquared.plot.PlotSquared;
import com.github.intellectualsites.plotsquared.plot.object.Plot;
import com.github.intellectualsites.plotsquared.plot.object.PlotId;
import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import network.palace.core.Core;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import network.palace.creative.Creative;
import network.palace.creative.handlers.PlayerData;
import network.palace.creative.utils.CreativeRank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class PlotReview {

    private final Map<UUID, PlotReviewData> pending = new HashMap<>();
    private final Map<UUID, String> accepted = new HashMap<>();
    private final Map<UUID, String> denied = new HashMap<>();

    public PlotReview() {
        load();
    }

    public void accept(PlotId plot, String message) {
        getPlotReviewData(plot).ifPresent(data -> {
            pending.remove(data.getUUID());
            accepted.put(data.getUUID(), message);
            check(data.getUUID());
        });
    }

    public void deny(PlotId plot, String message) {
        getPlotReviewData(plot).ifPresent(data -> {
            pending.remove(data.getUUID());
            denied.put(data.getUUID(), message);
            check(data.getUUID());
        });
    }

    public void check(UUID uuid) {
        CPlayer cPlayer = Core.getPlayerManager().getPlayer(uuid);
        if (cPlayer.isOnline()) {
            cPlayer.sendMessage(ChatColor.GREEN + "Your plot has been reviewed!");
            if (accepted.containsKey(uuid)) {
                cPlayer.sendMessage(ChatColor.GREEN + accepted.get(uuid));
                accepted.remove(uuid);
            }
            else if (denied.containsKey(uuid)) {
                cPlayer.sendMessage(ChatColor.GREEN + accepted.get(uuid));
                denied.remove(uuid);
            }
        }
    }

    public void addPending(UUID uuid, PlotId plotId, World world) {
        pending.put(uuid, new PlotReviewData(uuid, plotId, world));
    }

    private void load() {
        File file = new File(Creative.getInstance().getDataFolder(), "plot_review.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            ConfigurationSection pending = yaml.getConfigurationSection("pending");
            pending.getKeys(false).forEach(key -> {
                UUID uuid = UUID.fromString(key);
                PlotId plotId = PlotId.fromString(Objects.requireNonNull(pending.getString(key + ".plot_id")));
                World world = Bukkit.getWorld(Objects.requireNonNull(pending.getString(key + ".world")));
                this.pending.put(uuid, new PlotReviewData(uuid, plotId, world));
            });
            ConfigurationSection messages = yaml.createSection("messages");
            ConfigurationSection accepted = messages.createSection("accepted");
            accepted.getKeys(false).forEach(key -> {
                UUID uuid = UUID.fromString(key);
                this.accepted.put(uuid, messages.getString(key));
            });
            ConfigurationSection denied = messages.createSection("denied");
            denied.getKeys(false).forEach(key -> {
                UUID uuid = UUID.fromString(key);
                this.denied.put(uuid, messages.getString(key));
            });
        }
    }

    public Optional<PlotReviewData> getPlotReviewData(PlotId plotId) {
        return pending.values().stream().filter(data -> data.getPlotId().equals(plotId)).findFirst();
    }

    public void openMenu(CPlayer player, int page) {
        List<MenuButton> buttons = new ArrayList<>();
        List<PlotReviewData> pending = new ArrayList<>(this.pending.values());
        for (int x = 0; x < 45; x++) {
            try {
                int i = x + (page - 1) * 45;
                PlotReviewData data = pending.get(i);
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(data.getUUID());
                PlayerData playerData = Creative.getInstance().getPlayerData(data.getUUID());
                if (playerData == null) {
                    buttons.add(new MenuButton(x, ItemUtil.create(Material.BARRIER, "Database Error!", Collections.singletonList(ChatColor.RED + data.getUUID().toString()))));
                }
                else {
                    CreativeRank rank = playerData.getRank();
                    ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
                    Objects.requireNonNull(meta).setDisplayName(offlinePlayer.getName());
                    meta.setLore(Arrays.asList(ChatColor.YELLOW + "Rank: " + rank.getName(), ChatColor.YELLOW + "World: " + data.getWorld().getName()));
                    itemStack.setItemMeta(meta);
                    buttons.add(new MenuButton(x, itemStack, ImmutableMap.of(ClickType.LEFT, p -> {
                        Optional<Plot> plot = PlotSquared.get().getPlotAreas(data.getWorld().getName()).stream().map(plotArea -> plotArea.getPlot(data.getPlotId())).findFirst();
                        if (plot.isPresent()) {
                            p.teleport(BukkitUtil.getHomeLocation(plot.get()));
                            p.sendMessage(ChatColor.GREEN + "You have arrived.");
                        }
                        else {
                            p.sendMessage(ChatColor.RED + "That plot doesn't exist!");
                        }
                    })));
                }
            }
            catch (IndexOutOfBoundsException ignored) {

            }
        }

        if (page - 1 > 0) {
            buttons.add(new MenuButton(45, Creative.getInstance().getMenuUtil().last, ImmutableMap.of(ClickType.LEFT, p -> openMenu(p, page - 1))));
        }

        if (page + 1 <= new Double(Math.ceil(pending.size() / 45D)).intValue()) {
            buttons.add(new MenuButton(53, Creative.getInstance().getMenuUtil().next, ImmutableMap.of(ClickType.LEFT, p -> openMenu(p, page + 1))));
        }

        buttons.add(new MenuButton(49, Creative.getInstance().getMenuUtil().back, ImmutableMap.of(ClickType.LEFT, CPlayer::closeInventory)));
        new Menu(54, "Plot Reviews", player, buttons).open();
    }

    public void save() {
        try {
            File file = new File(Creative.getInstance().getDataFolder(), "plot_review.yml");
            if (!file.exists()) {
                file.createNewFile();
            }

            YamlConfiguration yaml = new YamlConfiguration();
            pending.forEach((uuid, data) -> {
                Map<String, Object> map = new HashMap<>();
                map.put("plot_id", data.getPlotId().toString());
                map.put("world", data.getWorld().getName());
                yaml.set("pending." + uuid.toString(), map);
            });
            accepted.forEach((uuid, message) -> yaml.set("messages.accepted." + uuid.toString(), message));
            denied.forEach((uuid, message) -> yaml.set("messages.denied." + uuid.toString(), message));
            yaml.save(file);
        }
        catch (IOException e) {
            Creative.getInstance().getLogger().warning("Failed to save plot review info.");
            e.printStackTrace();
        }
    }
}
