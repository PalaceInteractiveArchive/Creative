package network.palace.creative.utils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.player.CPlayer;
import network.palace.creative.Creative;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PlotFloorUtil {

    private final List<ItemStack> materials;
    private final List<PlotId> active = new ArrayList<>();
    private final Map<UUID, LogSection> logs = new HashMap<>();

    @SuppressWarnings("deprecation")
    public PlotFloorUtil() {
        this.materials = Stream.of(Material.values()).filter(Material::isSolid).filter(material -> !material.isLegacy()).filter(material -> !material.hasGravity())
                .filter(material -> {
                    switch (material) {
                        case BARRIER:
                        case BEDROCK:
                        case CACTUS:
                        case CAKE:
                        case CAULDRON:
                        case CONDUIT:
                        case CRAFTING_TABLE:
                        case DAYLIGHT_DETECTOR:
                        case ENCHANTING_TABLE:
                        case ENDER_CHEST:
                        case END_PORTAL_FRAME:
                        case INFESTED_CHISELED_STONE_BRICKS:
                        case INFESTED_COBBLESTONE:
                        case INFESTED_CRACKED_STONE_BRICKS:
                        case INFESTED_MOSSY_STONE_BRICKS:
                        case INFESTED_STONE:
                        case INFESTED_STONE_BRICKS:
                        case JUKEBOX:
                        case REDSTONE_LAMP:
                        case SPAWNER:
                        case STRUCTURE_BLOCK:
                        case TNT:
                        case TURTLE_EGG:
                            return false;
                    }

                    BlockData data = material.createBlockData();
                    if (data instanceof AnaloguePowerable) {
                        return false;
                    } else if (data instanceof Bed) {
                        return false;
                    } else if (data instanceof CommandBlock) {
                        return false;
                    } else if (data instanceof CoralWallFan) {
                        return false;
                    } else if (data instanceof Door) {
                        return false;
                    } else if (data instanceof Fence) {
                        return false;
                    } else if (data instanceof Gate) {
                        return false;
                    } else if (data instanceof GlassPane) {
                        return false;
                    } else if (data instanceof Piston) {
                        return false;
                    } else if (data instanceof Powerable) {
                        return false;
                    } else if (data instanceof Sapling) {
                        return false;
                    } else if (data instanceof Sign) {
                        return false;
                    } else if (data instanceof Slab) {
                        return false;
                    } else if (data instanceof Stairs) {
                        return false;
                    } else if (data instanceof TechnicalPiston) {
                        return false;
                    } else if (data instanceof WallSign) {
                        return false;
                    } else if (data instanceof Waterlogged) {
                        return false;
                    }

                    ItemMeta itemMeta = new ItemStack(material).getItemMeta();
                    if (itemMeta instanceof BlockStateMeta && ((BlockStateMeta) itemMeta).getBlockState() instanceof Container) {
                        return false;
                    } else {
                        return !(itemMeta instanceof BannerMeta);
                    }
                })
                .map(ItemStack::new).collect(Collectors.toList());
        loadLogs();
    }

    public LogSection getLog(UUID uuid) {
        return logs.get(uuid);
    }

    private void loadLogs() {
        Creative plugin = Creative.getInstance();
        Logger logger = plugin.getLogger();
        File file = new File(plugin.getDataFolder(), "plot_floor_logs.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                logger.warning("Failed to load plot floor logs!");
                return;
            }
        }

        YamlConfiguration logs = YamlConfiguration.loadConfiguration(file);
        logs.getKeys(false).forEach(key -> {
            try {
                long timeStamp = logs.getLong(key + ".timestamp");
                Material block = Material.valueOf(logs.getString(key + ".block"));
                UUID uuid = UUID.fromString(key);
                this.logs.put(uuid, new LogSection(timeStamp, block, uuid));
            } catch (Exception e) {
                logger.warning("Failed to load log for " + key + "!");
            }
        });
    }

    private void log(LogSection logSection) {
        long timeStamp = logSection.timeStamp;
        Material block = logSection.block;
        UUID uuid = logSection.uuid;
        logs.put(uuid, logSection);
        Logger logger = Creative.getInstance().getLogger();
        File file = new File(Creative.getInstance().getDataFolder(), "plot_floor_logs.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                logger.warning("Failed to load plot floor logs!");
                return;
            }
        }

        YamlConfiguration logs = YamlConfiguration.loadConfiguration(file);
        logs.set(uuid.toString() + ".timestamp", timeStamp);
        logs.set(uuid.toString() + ".block", block.toString());
        try {
            logs.save(file);
        } catch (IOException e) {
            logger.warning("Failed to update log for " + uuid.toString() + "!");
        }
    }

    public void open(CPlayer player, int page) {
        Plot plot = PlotPlayer.wrap(player.getBukkitPlayer()).getCurrentPlot();
        if (plot == null || !plot.getOwners().contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You must be in your own plot to do this.");
            return;
        }

        List<MenuButton> buttons = new ArrayList<>();
        for (int i = 0; i < 45; i++) {
            try {
                ItemStack itemStack = materials.get(i + (page - 1) * 45);
                buttons.add(new MenuButton(i, itemStack, ImmutableMap.of(ClickType.LEFT, p -> {
                    p.closeInventory();
                    if (PlotPlayer.wrap(p.getBukkitPlayer()).getCurrentPlot().getId() != plot.getId()) {
                        p.sendMessage(ChatColor.RED + "You must be in your own plot to do this.");
                        return;
                    }

                    if (active.contains(plot.getId())) {
                        p.sendMessage(ChatColor.RED + "The floor of your plot is still being updated. Please wait until it is completed.");
                        return;
                    }

                    if (plot.getRunning() > 0) {
                        p.sendMessage(ChatColor.RED + "Your plot is currently executing a task, try again in a few minutes.");
                        return;
                    }

                    Location minCorner = getFloorCorner(plot.getBottomAbs());
                    Location maxCorner = getFloorCorner(plot.getTopAbs());
                    List<Location> locations = new ArrayList<>();
                    for (int x = minCorner.getBlockX(); x < maxCorner.getBlockX() + 1; x++) {
                        for (int z = minCorner.getBlockZ(); z < maxCorner.getBlockZ() + 1; z++) {
                            locations.add(new Location(p.getWorld(), x, 64, z));
                        }
                    }
                    plot.addRunning();
                    log(new LogSection(System.currentTimeMillis(), itemStack.getType(), p.getUniqueId()));
                    List<List<Location>> lines = Lists.partition(locations, 10);
                    IntStream.range(0, lines.size()).forEach(j -> Bukkit.getScheduler().scheduleSyncDelayedTask(Creative.getInstance(), () -> lines.get(j).forEach(location -> {
                        Block original = location.getBlock();
                        original.setType(itemStack.getType());
                    }), j));
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Creative.getInstance(), () -> {
                        p.sendMessage(ChatColor.GREEN + "Floor update complete.");
                        active.remove(plot.getId());
                        plot.removeRunning();
                    }, lines.size());
                    active.add(plot.getId());
                    p.sendMessage(ChatColor.GREEN + "We are updating the floor to your plot. This may take a few moments.");
                })));
            } catch (IndexOutOfBoundsException ignored) {

            }
        }

        MenuUtil menuUtil = Creative.getInstance().getMenuUtil();
        if (page - 1 > 0) {
            buttons.add(new MenuButton(45, menuUtil.last, ImmutableMap.of(ClickType.LEFT, p -> open(p, page - 1))));
        }

        buttons.add(new MenuButton(49, menuUtil.back, ImmutableMap.of(ClickType.LEFT, menuUtil::openMenu)));
        if (page + 1 <= (int) (Math.ceil(materials.size() / 45D))) {
            buttons.add(new MenuButton(53, menuUtil.next, ImmutableMap.of(ClickType.LEFT, p -> open(p, page + 1))));
        }

        new Menu(54, ChatColor.BLUE + "Set the floor of your plot.", player, buttons).open();
    }

    private Location getFloorCorner(com.plotsquared.core.location.Location psLocation) {
        return new Location(Bukkit.getWorld(psLocation.getWorld()), psLocation.getX(), 64, psLocation.getZ());
    }

    @Getter
    @AllArgsConstructor
    public static class LogSection {
        private final long timeStamp;
        private final Material block;
        private final UUID uuid;
    }
}
