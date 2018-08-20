package network.palace.creative.utils;

import com.google.common.collect.Lists;
import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import network.palace.creative.Creative;
import network.palace.creative.handlers.CreativeInventoryType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class PlotFloorUtil {

    private List<ItemStack> materials;
    private List<PlotId> active = new ArrayList<>();

    public PlotFloorUtil() {
        this.materials = Stream.of(Material.values()).filter(Material::isSolid).filter(material -> {
            switch (material) {
                case BEDROCK:
                case SAND:
                case GRAVEL:
                case PISTON_EXTENSION:
                case BED_BLOCK:
                case POWERED_RAIL:
                case DETECTOR_RAIL:
                case WEB:
                case LONG_GRASS:
                case DEAD_BUSH:
                case PISTON_MOVING_PIECE:
                case YELLOW_FLOWER:
                case RED_ROSE:
                case BROWN_MUSHROOM:
                case RED_MUSHROOM:
                case TNT:
                case TORCH:
                case FIRE:
                case MOB_SPAWNER:
                case WOOD_STAIRS:
                case CHEST:
                case REDSTONE_WIRE:
                case CROPS:
                case BURNING_FURNACE:
                case SIGN:
                case WOODEN_DOOR:
                case LADDER:
                case SIGN_POST:
                case RAILS:
                case COBBLESTONE_STAIRS:
                case WALL_SIGN:
                case LEVER:
                case STONE_PLATE:
                case IRON_DOOR_BLOCK:
                case WOOD_PLATE:
                case GLOWING_REDSTONE_ORE:
                case REDSTONE_TORCH_OFF:
                case REDSTONE_TORCH_ON:
                case STONE_BUTTON:
                case SNOW:
                case CACTUS:
                case SUGAR_CANE_BLOCK:
                case FENCE:
                case PORTAL:
                case CAKE_BLOCK:
                case DIODE_BLOCK_OFF:
                case DIODE_BLOCK_ON:
                case TRAP_DOOR:
                case MONSTER_EGGS:
                case IRON_FENCE:
                case THIN_GLASS:
                case MELON_STEM:
                case PUMPKIN_STEM:
                case VINE:
                case FENCE_GATE:
                case BRICK_STAIRS:
                case SMOOTH_STAIRS:
                case WATER_LILY:
                case NETHER_FENCE:
                case NETHER_BRICK_STAIRS:
                case NETHER_STALK:
                case ENCHANTMENT_TABLE:
                case BREWING_STAND:
                case CAULDRON:
                case ENDER_PORTAL:
                case ENDER_PORTAL_FRAME:
                case DRAGON_EGG:
                case WOOD_DOUBLE_STEP:
                case WOOD_STEP:
                case REDSTONE_LAMP_ON:
                case DOUBLE_STEP:
                case STEP:
                case COCOA:
                case SANDSTONE_STAIRS:
                case ENDER_CHEST:
                case TRIPWIRE_HOOK:
                case TRIPWIRE:
                case SPRUCE_WOOD_STAIRS:
                case BIRCH_WOOD_STAIRS:
                case JUNGLE_WOOD_STAIRS:
                case COMMAND:
                case BEACON:
                case COBBLE_WALL:
                case FLOWER_POT:
                case CARROT:
                case POTATO:
                case WOOD_BUTTON:
                case SKULL:
                case ANVIL:
                case TRAPPED_CHEST:
                case GOLD_PLATE:
                case IRON_PLATE:
                case REDSTONE_COMPARATOR_OFF:
                case REDSTONE_COMPARATOR_ON:
                case DAYLIGHT_DETECTOR:
                case HOPPER:
                case QUARTZ_STAIRS:
                case ACTIVATOR_RAIL:
                case STAINED_GLASS_PANE:
                case ACACIA_STAIRS:
                case DARK_OAK_STAIRS:
                case BARRIER:
                case IRON_TRAPDOOR:
                case CARPET:
                case DOUBLE_PLANT:
                case STANDING_BANNER:
                case WALL_BANNER:
                case DAYLIGHT_DETECTOR_INVERTED:
                case RED_SANDSTONE_STAIRS:
                case DOUBLE_STONE_SLAB2:
                case STONE_SLAB2:
                case SPRUCE_FENCE_GATE:
                case BIRCH_FENCE_GATE:
                case JUNGLE_FENCE_GATE:
                case DARK_OAK_FENCE_GATE:
                case ACACIA_FENCE_GATE:
                case SPRUCE_FENCE:
                case BIRCH_FENCE:
                case JUNGLE_FENCE:
                case DARK_OAK_FENCE:
                case ACACIA_FENCE:
                case SPRUCE_DOOR:
                case BIRCH_DOOR:
                case JUNGLE_DOOR:
                case ACACIA_DOOR:
                case DARK_OAK_DOOR:
                case END_ROD:
                case CHORUS_PLANT:
                case CHORUS_FLOWER:
                case PURPUR_STAIRS:
                case PURPUR_DOUBLE_SLAB:
                case PURPUR_SLAB:
                case BEETROOT_BLOCK:
                case COMMAND_REPEATING:
                case COMMAND_CHAIN:
                case FROSTED_ICE:
                case STRUCTURE_VOID:
                case CONCRETE_POWDER:
                case STRUCTURE_BLOCK:
                    return false;
                default:
                    return !material.toString().contains("_SHULKER_BOX");
            }
        }).flatMap(material -> {
            switch (material) {
                case STONE:
                    return getVariants(6, material);
                case DIRT:
                    return getVariants(2, material);
                case WOOD:
                    return getVariants(5, material);
                case LOG:
                    return getVariants(3, material);
                case LEAVES:
                    return getVariants(3, material);
                case SPONGE:
                    return getVariants(1, material);
                case SANDSTONE:
                    return getVariants(2, material);
                case WOOL:
                    return getVariants(15, material);
                case STAINED_GLASS:
                    return getVariants(15, material);
                case SMOOTH_BRICK:
                    return getVariants(3, material);
                case QUARTZ_BLOCK:
                    return getVariants(2, material);
                case STAINED_CLAY:
                    return getVariants(15, material);
                case LEAVES_2:
                    return getVariants(1, material);
                case LOG_2:
                    return getVariants(1, material);
                case PRISMARINE:
                    return getVariants(2, material);
                case RED_SANDSTONE:
                    return getVariants(2, material);
                case CONCRETE_POWDER:
                    return getVariants(15, material);
                case CONCRETE:
                    return getVariants(15, material);
                default:
                    return Stream.of(new ItemStack(material));
            }
        }).collect(Collectors.toList());
    }

    private Stream<ItemStack> getVariants(int max, Material material) {
        return IntStream.range(0, max + 1).mapToObj(i -> new ItemStack(material, 1, (short) i));
    }

    public void handle(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        String name = meta == null || meta.getDisplayName() == null ? null : ChatColor.stripColor(meta.getDisplayName());
        boolean isBack = name != null && name.equalsIgnoreCase("back") && item.getType() == Material.ARROW;
        int page = player.getMetadata("page").get(0).asInt();
        int maxPages = new Double(Math.ceil(materials.size() / 45D)).intValue();
        event.setCancelled(true);
        if (isBack) {
            Creative.getInstance().getMenuUtil().openMenu(player, CreativeInventoryType.PLOT_SETTINGS);
            return;
        }

        if (name != null) {
            switch (name) {
                case "Next Page":
                    if (page + 1 <= maxPages) {
                        player.removeMetadata("page", Creative.getInstance());
                        open(player, page + 1);
                    }
                    break;
                case "Last Page":
                    if (page - 1 > 0) {
                        player.removeMetadata("page", Creative.getInstance());
                        open(player, page - 1);
                    }
                    break;
            }
            return;
        }

        PlotAPI plotAPI = new PlotAPI();
        Plot plot = plotAPI.getPlot(player);
        if (plot == null || !plot.getOwners().contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You must be in your own plot to do this.");
            return;
        }

        if (active.contains(plot.getId())) {
            player.sendMessage(ChatColor.RED + "The floor of your plot is still being updated. Please wait until it is completed.");
            player.closeInventory();
            return;
        }

        Location minCorner = getFloorCorner(plot.getBottomAbs());
        Location maxCorner = getFloorCorner(plot.getTopAbs());
        List<Location> locations = new ArrayList<>();
        for (int x = minCorner.getBlockX(); x < maxCorner.getBlockX() + 1; x++) {
            for (int z = minCorner.getBlockZ(); z < maxCorner.getBlockZ() + 1; z++) {
                locations.add(new Location(player.getWorld(), x, 64, z));
            }
        }

        List<List<Location>> lines = Lists.partition(locations, 10);
        IntStream.range(0, lines.size()).forEach(i -> Bukkit.getScheduler().scheduleSyncDelayedTask(Creative.getInstance(), () -> {
            lines.get(i).forEach(location -> {
                BlockState block = location.getBlock().getState();
                block.setType(item.getType());
                block.setData(item.getData());
                block.update(true);
            });
        }, i));
        Bukkit.getScheduler().scheduleSyncDelayedTask(Creative.getInstance(), () -> {
            player.sendMessage(ChatColor.GREEN + "Floor update complete.");
            active.remove(plot.getId());
        }, lines.size());
        active.add(plot.getId());
        player.removeMetadata("page", Creative.getInstance());
        player.closeInventory();
        player.sendMessage(ChatColor.GREEN + "We are updating the floor to your plot. This may take a few moments.");
    }

    public void open(Player player, int page) {
        PlotAPI plotAPI = new PlotAPI();
        Plot plot = plotAPI.getPlot(player);
        if (plot == null || !plot.getOwners().contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You must be in your own plot to do this.");
            return;
        }

        Inventory inv = Bukkit.createInventory(player, 54, ChatColor.BLUE + "Set the floor of your plot.");
        for (int x = 0; x < 45; x++) {
            try {
                inv.setItem(x, materials.get(x + (page - 1) * 45));
            }
            catch (IndexOutOfBoundsException ignored) {

            }
        }

        MenuUtil menuUtil = Creative.getInstance().getMenuUtil();
        inv.setItem(45, menuUtil.last);
        inv.setItem(49, menuUtil.back);
        inv.setItem(53, menuUtil.next);
        player.setMetadata("page", new FixedMetadataValue(Creative.getInstance(), page));
        player.openInventory(inv);
    }

    private Location getFloorCorner(com.intellectualcrafters.plot.object.Location psLocation) {
        return new Location(Bukkit.getWorld(psLocation.getWorld()), psLocation.getX(), 64, psLocation.getZ());
    }
}
