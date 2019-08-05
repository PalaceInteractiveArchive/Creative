package network.palace.creative.utils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotId;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import network.palace.creative.Creative;
import network.palace.creative.inventory.Menu;
import network.palace.creative.inventory.MenuButton;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class PlotFloorUtil {

    private final List<ItemStack> materials;
    private final List<PlotId> active = new ArrayList<>();
    private final Map<UUID, LogSection> logs = new HashMap<>();
    private final List<Material> invalidMaterials = Arrays.asList(Material.BEDROCK, Material.SAND, Material.GRAVEL,
            Material.PISTON_EXTENSION, Material.BED_BLOCK, Material.POWERED_RAIL, Material.DETECTOR_RAIL, Material.WEB,
            Material.LONG_GRASS, Material.DEAD_BUSH, Material.PISTON_MOVING_PIECE, Material.YELLOW_FLOWER, Material.RED_ROSE,
            Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.TNT, Material.TORCH, Material.FIRE, Material.MOB_SPAWNER,
            Material.WOOD_STAIRS, Material.CHEST, Material.REDSTONE_WIRE, Material.CROPS, Material.BURNING_FURNACE, Material.SIGN,
            Material.WOODEN_DOOR, Material.LADDER, Material.SIGN_POST, Material.RAILS, Material.COBBLESTONE_STAIRS, Material.WALL_SIGN,
            Material.LEVER, Material.STONE_PLATE, Material.IRON_DOOR_BLOCK, Material.WOOD_PLATE, Material.GLOWING_REDSTONE_ORE,
            Material.REDSTONE_TORCH_OFF, Material.REDSTONE_TORCH_ON, Material.STONE_BUTTON, Material.SNOW, Material.CACTUS,
            Material.SUGAR_CANE_BLOCK, Material.FENCE, Material.PORTAL, Material.CAKE_BLOCK, Material.DIODE_BLOCK_OFF,
            Material.DIODE_BLOCK_ON, Material.TRAP_DOOR, Material.MONSTER_EGGS, Material.IRON_FENCE, Material.THIN_GLASS,
            Material.MELON_STEM, Material.PUMPKIN_STEM, Material.VINE, Material.FENCE_GATE, Material.BRICK_STAIRS,
            Material.SMOOTH_STAIRS, Material.WATER_LILY, Material.NETHER_FENCE, Material.NETHER_BRICK_STAIRS, Material.NETHER_STALK,
            Material.ENCHANTMENT_TABLE, Material.BREWING_STAND, Material.CAULDRON, Material.ENDER_PORTAL, Material.ENDER_PORTAL_FRAME,
            Material.DRAGON_EGG, Material.WOOD_DOUBLE_STEP, Material.WOOD_STEP, Material.REDSTONE_LAMP_ON, Material.DOUBLE_STEP,
            Material.STEP, Material.COCOA, Material.SANDSTONE_STAIRS, Material.ENDER_CHEST, Material.TRIPWIRE_HOOK, Material.TRIPWIRE,
            Material.SPRUCE_WOOD_STAIRS, Material.BIRCH_WOOD_STAIRS, Material.JUNGLE_WOOD_STAIRS, Material.COMMAND, Material.BEACON,
            Material.COBBLE_WALL, Material.FLOWER_POT, Material.CARROT, Material.POTATO, Material.WOOD_BUTTON, Material.SKULL,
            Material.ANVIL, Material.TRAPPED_CHEST, Material.GOLD_PLATE, Material.IRON_PLATE, Material.REDSTONE_COMPARATOR_OFF,
            Material.REDSTONE_COMPARATOR_ON, Material.DAYLIGHT_DETECTOR, Material.HOPPER, Material.QUARTZ_STAIRS, Material.ACTIVATOR_RAIL,
            Material.STAINED_GLASS_PANE, Material.ACACIA_STAIRS, Material.DARK_OAK_STAIRS, Material.BARRIER, Material.IRON_TRAPDOOR,
            Material.CARPET, Material.DOUBLE_PLANT, Material.STANDING_BANNER, Material.WALL_BANNER, Material.DAYLIGHT_DETECTOR_INVERTED,
            Material.RED_SANDSTONE_STAIRS, Material.DOUBLE_STONE_SLAB2, Material.STONE_SLAB2, Material.SPRUCE_FENCE_GATE,
            Material.BIRCH_FENCE_GATE, Material.JUNGLE_FENCE_GATE, Material.DARK_OAK_FENCE_GATE, Material.ACACIA_FENCE_GATE,
            Material.SPRUCE_FENCE, Material.BIRCH_FENCE, Material.JUNGLE_FENCE, Material.DARK_OAK_FENCE, Material.ACACIA_FENCE,
            Material.SPRUCE_DOOR, Material.BIRCH_DOOR, Material.JUNGLE_DOOR, Material.ACACIA_DOOR, Material.DARK_OAK_DOOR,
            Material.END_ROD, Material.CHORUS_PLANT, Material.CHORUS_FLOWER, Material.PURPUR_STAIRS, Material.PURPUR_DOUBLE_SLAB,
            Material.PURPUR_SLAB, Material.BEETROOT_BLOCK, Material.COMMAND_REPEATING, Material.COMMAND_CHAIN, Material.FROSTED_ICE,
            Material.STRUCTURE_VOID, Material.CONCRETE_POWDER, Material.STRUCTURE_BLOCK);

    public PlotFloorUtil() {
        this.materials = Stream.of(Material.values()).filter(Material::isSolid)
                .filter(material -> !invalidMaterials.contains(material) && !material.toString().contains("_SHULKER_BOX"))
                .flatMap(material -> {
                    switch (material) {
                        case LEAVES_2:
                        case LOG_2:
                        case SPONGE:
                            return getVariants(1, material);
                        case DIRT:
                        case PRISMARINE:
                        case QUARTZ_BLOCK:
                        case RED_SANDSTONE:
                        case SANDSTONE:
                            return getVariants(2, material);
                        case LOG:
                        case LEAVES:
                        case SMOOTH_BRICK:
                            return getVariants(3, material);
                        case WOOD:
                            return getVariants(5, material);
                        case STONE:
                            return getVariants(6, material);
                        case CONCRETE:
                        case STAINED_CLAY:
                        case STAINED_GLASS:
                        case WOOL:
                            return getVariants(15, material);
                        default:
                            return Stream.of(new ItemStack(material));
                    }
                }).collect(Collectors.toList());
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
            }
            catch (IOException e) {
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
            }
            catch (Exception e) {
                logger.warning("Failed to load log for " + key + "!");
            }
        });
    }

    private Stream<ItemStack> getVariants(int max, Material material) {
        return IntStream.range(0, max + 1).mapToObj(i -> new ItemStack(material, 1, (short) i));
    }

    private void log(LogSection logSection) {
        long timeStamp = logSection.timeStamp;
        Material block = logSection.block;
        UUID uuid = logSection.uuid;
        logs.put(uuid, logSection);
        Creative plugin = Creative.getInstance();
        Logger logger = plugin.getLogger();
        File file = new File(plugin.getDataFolder(), "plot_floor_logs.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            }
            catch (IOException e) {
                logger.warning("Failed to load plot floor logs!");
                return;
            }
        }

        YamlConfiguration logs = YamlConfiguration.loadConfiguration(file);
        logs.set(uuid.toString() + ".timestamp", timeStamp);
        logs.set(uuid.toString() + ".block", block.toString());
        try {
            logs.save(file);
        }
        catch (IOException e) {
            logger.warning("Failed to update log for " + uuid.toString() + "!");
        }
    }

    public void open(Player player, int page) {
        PlotAPI plotAPI = new PlotAPI();
        Plot plot = plotAPI.getPlot(player);
        if (plot == null || !plot.getOwners().contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You must be in your own plot to do this.");
            return;
        }

        List<MenuButton> buttons = new ArrayList<>();
        for (int i = 0; i < 45; i++) {
            try {
                ItemStack itemStack = materials.get(i + (page - 1) * 45);
                buttons.add(new MenuButton(i, itemStack, ImmutableMap.of(ClickType.LEFT, p -> {
                    if (plotAPI.getPlot(p).getId() != plot.getId()) {
                        p.sendMessage(ChatColor.RED + "You must be in your own plot to do this.");
                        p.closeInventory();
                        return;
                    }

                    if (active.contains(plot.getId())) {
                        p.sendMessage(ChatColor.RED + "The floor of your plot is still being updated. Please wait until it is completed.");
                        p.closeInventory();
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

                    log(new LogSection(System.currentTimeMillis(), itemStack.getType(), p.getUniqueId()));
                    List<List<Location>> lines = Lists.partition(locations, 10);
                    IntStream.range(0, lines.size()).forEach(j -> Bukkit.getScheduler().scheduleSyncDelayedTask(Creative.getInstance(), () -> lines.get(j).forEach(location -> {
                        Block original = location.getBlock();
                        BlockState block = original.getState();
                        block.setType(itemStack.getType());
                        block.setData(itemStack.getData());
                        block.update(true);
                    }), j));
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Creative.getInstance(), () -> {
                        p.sendMessage(ChatColor.GREEN + "Floor update complete.");
                        active.remove(plot.getId());
                    }, lines.size());
                    active.add(plot.getId());
                    p.closeInventory();
                    p.sendMessage(ChatColor.GREEN + "We are updating the floor to your plot. This may take a few moments.");
                })));
            }
            catch (IndexOutOfBoundsException ignored) {

            }
        }

        MenuUtil menuUtil = Creative.getInstance().getMenuUtil();
        if (page - 1 > 0) {
            buttons.add(new MenuButton(45, menuUtil.last, ImmutableMap.of(ClickType.LEFT, p -> open(p, page - 1))));
        }

        buttons.add(new MenuButton(49, menuUtil.back, ImmutableMap.of(ClickType.LEFT, menuUtil::openMenu)));
        if (page + 1 <= new Double(Math.ceil(materials.size() / 45D)).intValue()) {
            buttons.add(new MenuButton(53, menuUtil.next, ImmutableMap.of(ClickType.LEFT, p -> open(p, page + 1))));
        }

        new Menu(Bukkit.createInventory(player, 54, ChatColor.BLUE + "Set the floor of your plot."), player, buttons);
    }

    private Location getFloorCorner(com.intellectualcrafters.plot.object.Location psLocation) {
        return new Location(Bukkit.getWorld(psLocation.getWorld()), psLocation.getX(), 64, psLocation.getZ());
    }

    @AllArgsConstructor
    public class LogSection {

        @Getter
        private final long timeStamp;
        @Getter
        private final Material block;
        @Getter
        private final UUID uuid;
    }
}
