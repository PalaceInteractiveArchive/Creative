package network.palace.creative.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import network.palace.core.utils.ItemUtil;
import network.palace.creative.Creative;
import network.palace.creative.handlers.BannerInventoryType;
import network.palace.creative.inventory.Menu;
import network.palace.creative.inventory.MenuButton;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

/**
 * Created by Marc on 6/12/15
 */
public class BannerUtil {
    private HashMap<String, ItemStack> colors = new HashMap<>();
    private HashMap<PatternType, ItemStack> banners = new HashMap<>();
    private HashMap<UUID, ItemStack> userBanners = new HashMap<>();

    public BannerUtil() {
        //--Colors--
        List<String> empty = new ArrayList<>();
        colors.put("red", ItemUtil.create(Material.WOOL, 1, (byte) 14, ChatColor.DARK_RED + "Red", empty));
        colors.put("orange", ItemUtil.create(Material.WOOL, 1, (byte) 1, ChatColor.GOLD + "Orange", empty));
        colors.put("yellow", ItemUtil.create(Material.WOOL, 1, (byte) 4, ChatColor.YELLOW + "Yellow", empty));
        colors.put("lime", ItemUtil.create(Material.WOOL, 1, (byte) 5, ChatColor.GREEN + "Lime", empty));
        colors.put("green", ItemUtil.create(Material.WOOL, 1, (byte) 13, ChatColor.DARK_GREEN + "Green", empty));
        colors.put("lightblue", ItemUtil.create(Material.WOOL, 1, (byte) 3, ChatColor.AQUA + "Light Blue", empty));
        colors.put("cyan", ItemUtil.create(Material.WOOL, 1, (byte) 9, ChatColor.DARK_AQUA + "Cyan", empty));
        colors.put("blue", ItemUtil.create(Material.WOOL, 1, (byte) 11, ChatColor.BLUE + "Blue", empty));
        colors.put("purple", ItemUtil.create(Material.WOOL, 1, (byte) 10, ChatColor.DARK_PURPLE + "Purple", empty));
        colors.put("magenta", ItemUtil.create(Material.WOOL, 1, (byte) 2, ChatColor.LIGHT_PURPLE + "Magenta", empty));
        colors.put("pink", ItemUtil.create(Material.WOOL, 1, (byte) 6, ChatColor.RED + "Pink", empty));
        colors.put("brown", ItemUtil.create(Material.WOOL, 1, (byte) 12, ChatColor.DARK_GRAY + "Brown", empty));
        colors.put("gray", ItemUtil.create(Material.WOOL, 1, (byte) 8, ChatColor.GRAY + "Gray", empty));
        colors.put("darkgray", ItemUtil.create(Material.WOOL, 1, (byte) 7, ChatColor.DARK_GRAY + "Dark Gray", empty));
        colors.put("white", ItemUtil.create(Material.WOOL, 1, (byte) 0, ChatColor.WHITE + "White", empty));
        colors.put("black", ItemUtil.create(Material.WOOL, 1, (byte) 15, ChatColor.DARK_GRAY + "Black", empty));
        //--Banners--
        PatternType[] types = new PatternType[]{PatternType.SQUARE_BOTTOM_LEFT, PatternType.SQUARE_BOTTOM_RIGHT,
                PatternType.SQUARE_TOP_LEFT, PatternType.SQUARE_TOP_RIGHT, PatternType.STRIPE_BOTTOM, PatternType.STRIPE_TOP,
                PatternType.STRIPE_LEFT, PatternType.STRIPE_RIGHT, PatternType.STRIPE_CENTER, PatternType.STRIPE_MIDDLE,
                PatternType.STRIPE_DOWNRIGHT, PatternType.STRIPE_DOWNLEFT, PatternType.STRIPE_SMALL,
                PatternType.CROSS, PatternType.STRAIGHT_CROSS, PatternType.TRIANGLE_BOTTOM, PatternType.TRIANGLE_TOP,
                PatternType.TRIANGLES_BOTTOM, PatternType.TRIANGLES_TOP, PatternType.DIAGONAL_LEFT,
                PatternType.DIAGONAL_RIGHT, PatternType.DIAGONAL_LEFT_MIRROR, PatternType.DIAGONAL_RIGHT_MIRROR,
                PatternType.CIRCLE_MIDDLE, PatternType.RHOMBUS_MIDDLE, PatternType.HALF_VERTICAL, PatternType.HALF_HORIZONTAL,
                PatternType.HALF_VERTICAL_MIRROR, PatternType.HALF_HORIZONTAL_MIRROR, PatternType.BORDER,
                PatternType.CURLY_BORDER, PatternType.CREEPER, PatternType.GRADIENT, PatternType.GRADIENT_UP,
                PatternType.BRICKS, PatternType.SKULL, PatternType.FLOWER, PatternType.MOJANG};
        for (PatternType type : types) {
            banners.put(type, getExampleBanner(type, getName(type.toString().toLowerCase())));
        }
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Creative.getInstance(),
                PacketType.Play.Client.CLOSE_WINDOW) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                cancel(player.getUniqueId());
                Creative.getInstance().getShowManager().cancelEdit(player);
            }
        });
    }

    private String getName(String name) {
        StringBuilder done = new StringBuilder();
        String[] list = name.split("_");
        for (String s : list) {
            StringBuilder st = new StringBuilder();
            boolean first = true;
            for (char c : s.toCharArray()) {
                if (first) {
                    st.append(Character.toUpperCase(c));
                    first = false;
                } else {
                    st.append(c);
                }
            }
            done.append(st).append(" ");
        }
        return ChatColor.RESET + done.toString();
    }

    public void openMenu(Player player, BannerInventoryType type) {
        List<MenuButton> buttons = new ArrayList<>();
        ItemStack banner = userBanners.getOrDefault(player.getUniqueId(), new ItemStack(Material.BANNER));
        BannerMeta meta = (BannerMeta) banner.getItemMeta();
        buttons.add(new MenuButton(4, banner, ImmutableMap.of(ClickType.LEFT, p -> {
            p.getInventory().addItem(banner);
            p.closeInventory();
            cancel(p.getUniqueId());
        })));

        if (type == BannerInventoryType.ADD_LAYER) {
            int place = 9;
            for (ItemStack item : banners.values()) {
                buttons.add(new MenuButton(place, item, ImmutableMap.of(ClickType.LEFT, p -> {
                    PatternType pattern = ((BannerMeta) item.getItemMeta()).getPattern(0).getPattern();
                    meta.addPattern(new Pattern(DyeColor.BLACK, pattern));
                    banner.setItemMeta(meta);
                    userBanners.put(p.getUniqueId(), banner);
                    openMenu(p, BannerInventoryType.LAYER_COLOR);
                })));
                place++;
            }
        }
        else {
            int place = 10;
            List<Integer> margin = Arrays.asList(16, 25, 34, 43);
            for (ItemStack item : colors.values()) {
                buttons.add(new MenuButton(place, item, ImmutableMap.of(ClickType.LEFT, p -> {
                    if (type == BannerInventoryType.SELECT_BASE && item.getType() == Material.WOOL) {
                        meta.setBaseColor(colorFromString(ChatColor.stripColor(item.getItemMeta().getDisplayName())));
                    }
                    else {
                        DyeColor color = colorFromString(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                        int current = meta.getPatterns().size() - 1;
                        PatternType pattern = meta.getPattern(current).getPattern();
                        meta.setPattern(current, new Pattern(color, pattern));
                    }

                    banner.setItemMeta(meta);
                    userBanners.put(p.getUniqueId(), banner);
                    openMenu(p, BannerInventoryType.ADD_LAYER);
                })));
                if (margin.contains(place)) {
                    place += 3;
                }
                else {
                    place++;
                }
            }
        }

        new Menu(createInventory(type), player, buttons);
    }

    private ItemStack getExampleBanner(PatternType type, String name) {
        ItemStack banner = ItemUtil.create(Material.BANNER, name);
        BannerMeta bm = (BannerMeta) banner.getItemMeta();
        bm.setBaseColor(DyeColor.WHITE);
        bm.addPattern(new Pattern(DyeColor.BLACK, type));
        bm.setLore(Collections.singletonList(ChatColor.GRAY + "Click to apply!"));
        banner.setItemMeta(bm);
        return banner;
    }

    private Inventory createInventory(BannerInventoryType type) {
        String n = "";
        switch (type) {
            case SELECT_BASE:
                n = "Select Base Color";
                break;
            case ADD_LAYER:
                n = "Add Layer";
                break;
            case LAYER_COLOR:
                n = "Choose Layer Color";
                break;
        }

        return Bukkit.createInventory(null, 54, ChatColor.GREEN + n);
    }

    private DyeColor colorFromString(String name) {
        switch (name.toLowerCase()) {
            case "red":
                return DyeColor.RED;
            case "orange":
                return DyeColor.ORANGE;
            case "yellow":
                return DyeColor.YELLOW;
            case "lime":
                return DyeColor.LIME;
            case "green":
                return DyeColor.GREEN;
            case "light blue":
                return DyeColor.LIGHT_BLUE;
            case "cyan":
                return DyeColor.CYAN;
            case "blue":
                return DyeColor.BLUE;
            case "purple":
                return DyeColor.PURPLE;
            case "magenta":
                return DyeColor.MAGENTA;
            case "pink":
                return DyeColor.PINK;
            case "brown":
                return DyeColor.BROWN;
            case "gray":
                return DyeColor.SILVER;
            case "dark gray":
                return DyeColor.GRAY;
            case "white":
                return DyeColor.WHITE;
            case "black":
                return DyeColor.BLACK;
        }
        return DyeColor.BLACK;
    }

    public void cancel(UUID uuid) {
        userBanners.remove(uuid);
    }
}
