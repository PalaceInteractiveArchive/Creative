package network.palace.creative.utils;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.flag.Flag;
import com.intellectualcrafters.plot.flag.FlagManager;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotArea;
import com.intellectualcrafters.plot.object.PlotId;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.util.EventUtil;
import com.intellectualcrafters.plot.util.MainUtil;
import com.intellectualcrafters.plot.util.PlotWeather;
import com.plotsquared.bukkit.util.BukkitUtil;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.ItemUtil;
import network.palace.creative.Creative;
import network.palace.creative.handlers.*;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Marc on 7/29/15
 */
@SuppressWarnings("deprecation")
public class MenuUtil implements Listener {
    private HashMap<UUID, Plot> adding = new HashMap<>();
    private HashMap<UUID, Plot> trusting = new HashMap<>();
    private HashMap<UUID, Plot> denying = new HashMap<>();
    private ItemStack bannerCreator = new ItemStack(Material.BANNER);
    private ItemStack plotTime = ItemUtil.create(Material.WATCH, ChatColor.GREEN + "Plot Settings");
    private ItemStack myPlots = ItemUtil.create(Material.GRASS, ChatColor.GREEN + "My Plots");
    private ItemStack spawn = ItemUtil.create(Material.ENDER_PEARL, ChatColor.GREEN + "Spawn");
    private ItemStack buildingPlots = ItemUtil.create(Material.DIRT, ChatColor.GREEN + "Building Plots");
    private ItemStack headshop;
    private ItemStack showCreator = ItemUtil.create(Material.FIREWORK, ChatColor.GREEN + "Show Creator");
    private ItemStack teleport = ItemUtil.create(Material.ENDER_PEARL, ChatColor.GREEN + "Teleport to Plot");
    private ItemStack deny = ItemUtil.create(Material.BARRIER, ChatColor.GREEN + "Deny a Player");
    private ItemStack members = ItemUtil.create(Material.BOOK, ChatColor.GREEN + "Added Players");
    private ItemStack denied = ItemUtil.create(Material.BOOK, ChatColor.GREEN + "Denied Players");
    private ItemStack purchase = ItemUtil.create(Material.DIAMOND, ChatColor.GREEN + "Purchase Second Plot",
            Collections.singletonList(ChatColor.GRAY + "Cost: " + ChatColor.GREEN + "$5000"));
    private ItemStack particles = ItemUtil.create(Material.BLAZE_POWDER, ChatColor.GREEN + "Particles");
    private ItemStack shop = ItemUtil.create(Material.EMERALD, ChatColor.GREEN + "Creative Shop");
    public ItemStack next = ItemUtil.create(Material.ARROW, ChatColor.GREEN + "Next Page");
    public ItemStack back = ItemUtil.create(Material.ARROW, ChatColor.GREEN + "Back");
    public ItemStack last = ItemUtil.create(Material.ARROW, ChatColor.GREEN + "Last Page");
    private ItemStack loading = ItemUtil.create(Material.STAINED_CLAY, 1, (byte) 9,
            ChatColor.AQUA + "Loading...", new ArrayList<>());
    private ItemStack more = ItemUtil.create(Material.STAINED_CLAY, 1, (byte) 4, ChatColor.RED + "Too many!",
            Arrays.asList(ChatColor.RED + "We can only list up to", ChatColor.RED +
                            "45 Plots here. You're added", ChatColor.RED + "to more than 45 Plots. To",
                    ChatColor.RED + "get to a Plot you're added to", ChatColor.RED +
                            "that isn't listed here, you have", ChatColor.RED + "to send a /tpa request to the",
                    ChatColor.RED + "Plot Owner"));
    private ItemStack member = ItemUtil.create(Material.REDSTONE_TORCH_ON, ChatColor.GREEN + "Member",
            Arrays.asList(ChatColor.YELLOW + "This type of Member can only", ChatColor.YELLOW +
                    "build when you are online."));
    private ItemStack trusted = ItemUtil.create(Material.TORCH, ChatColor.GOLD + "Trusted",
            Arrays.asList(ChatColor.YELLOW + "This type of Member can build", ChatColor.YELLOW +
                    "even if you're not online."));
    private ItemStack note = ItemUtil.create(Material.NOTE_BLOCK, ChatColor.GREEN + "Notes",
            new ArrayList<>());
    private ItemStack spark = ItemUtil.create(Material.FIREWORK, ChatColor.GREEN + "Firework Spark",
            new ArrayList<>());
    private ItemStack mickey = ItemUtil.create(Material.APPLE, ChatColor.GREEN + "Mickey Head",
            new ArrayList<>());
    private ItemStack enchant = ItemUtil.create(Material.ENCHANTMENT_TABLE, ChatColor.GREEN + "Enchantment",
            new ArrayList<>());
    private ItemStack flame = ItemUtil.create(Material.FLINT_AND_STEEL, ChatColor.GREEN + "Flame",
            new ArrayList<>());
    private ItemStack heart = ItemUtil.create(Material.DIAMOND, ChatColor.GREEN + "Hearts",
            new ArrayList<>());
    private ItemStack portal = ItemUtil.create(Material.BLAZE_POWDER, ChatColor.GREEN + "Portal",
            new ArrayList<>());
    private ItemStack lava = ItemUtil.create(Material.LAVA_BUCKET, ChatColor.GREEN + "Lava",
            new ArrayList<>());
    private ItemStack witch = ItemUtil.create(Material.POTION, 1, (byte) 8196, ChatColor.GREEN + "Witch Magic",
            new ArrayList<>());
    private ItemStack none = ItemUtil.create(Material.STAINED_GLASS_PANE, ChatColor.RED + "Clear Particle",
            new ArrayList<>());
    private PlotAPI api;
    private List<UUID> denyTask = new ArrayList<>();

    public MenuUtil() {
        api = new PlotAPI(Creative.getInstance());
        BannerMeta bm = (BannerMeta) bannerCreator.getItemMeta();
        bm.setBaseColor(DyeColor.BLUE);
        bm.addPattern(new Pattern(DyeColor.RED, PatternType.TRIANGLE_BOTTOM));
        bm.addPattern(new Pattern(DyeColor.RED, PatternType.TRIANGLE_TOP));
        bm.addPattern(new Pattern(DyeColor.ORANGE, PatternType.CIRCLE_MIDDLE));
        bm.setDisplayName(ChatColor.GREEN + "Banner Creator");
        bannerCreator.setItemMeta(bm);
        String hash = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv" +
                "Y2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0=";
        headshop = network.palace.core.utils.HeadUtil.getPlayerHead(hash, ChatColor.GREEN + "Headshop");
        Bukkit.getScheduler().runTaskTimer(Creative.getInstance(), () -> {
            for (UUID uuid : new ArrayList<>(denyTask)) {
                Player tp = Bukkit.getPlayer(uuid);
                denyTask.remove(uuid);
                if (tp == null) {
                    continue;
                }
                tp.teleport(Creative.getInstance().getSpawn());
            }
        }, 0L, 20L);
    }

    public void openMenu(Player player, CreativeInventoryType type) {
        if (player == null) return;
        if (type == null) return;

        PlayerData data = Creative.getInstance().getPlayerData(player.getUniqueId());

        if (data == null) return;
        switch (type) {
            case MAIN: {
                Inventory main = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Creative Menu");
                Plot plot = api.getPlot(player);
                boolean owns = false;
                if (plot != null) {
                    for (Plot pl : api.getPlayerPlots(Bukkit.getWorld("plotworld"), player)) {
                        if (plot.getId().equals(pl.getId())) {
                            owns = true;
                            break;
                        }
                    }
                }
                if (owns) {
                    main.setItem(4, plotTime);
                }
                main.setItem(10, bannerCreator);
                main.setItem(11, myPlots);
                main.setItem(12, shop);
                main.setItem(13, spawn);
                main.setItem(14, particles);
                main.setItem(15, buildingPlots);
                main.setItem(16, headshop);
                if (data.hasShowCreator()) {
                    main.setItem(22, showCreator);
                }
                player.openInventory(main);
                break;
            }
            case MY_PLOTS: {
                Inventory myPlots = Bukkit.createInventory(player, 27, ChatColor.BLUE + "My Plots");
                List<Plot> plots = new ArrayList<>(api.getPlayerPlots(Bukkit.getWorld("plotworld"), player));
                if (plots.isEmpty()) {
                    ItemStack empty = ItemUtil.create(Material.WOOL, 1, (byte) 14, ChatColor.RED +
                            "You don't have any plots!", Arrays.asList(ChatColor.GREEN + "Click here to get",
                            ChatColor.GREEN + "your own plot!"));
                    myPlots.setItem(13, empty);
                    myPlots.setItem(22, back);
                    player.openInventory(myPlots);
                    break;
                }
                for (int i = 0; i < plots.size(); i++) {
                    if (i >= 7) {
                        break;
                    }
                    Plot plot = plots.get(i);
                    ItemStack stack = ItemUtil.create(Material.GRASS, ChatColor.GREEN + "Plot ID: " +
                            plot.getId().toString(), Collections.singletonList(ChatColor.GOLD +
                            "Click to Manage this Plot!"));
                    myPlots.setItem(i + 10, stack);
                }
                myPlots.setItem(22, back);
                player.openInventory(myPlots);
                break;
            }
            case BUILDING_PLOTS: {
                Inventory building = Bukkit.createInventory(player, 54, ChatColor.BLUE + "Building Plots");
                building.setItem(22, loading);
                player.openInventory(building);
                Bukkit.getScheduler().runTaskAsynchronously(Creative.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        final List<Plot> plotList = new ArrayList<>(PS.get().getPlots("plotworld"));
                        int i = 0;
                        for (Plot p : plotList) {
                            if (building.getViewers().isEmpty()) {
                                return;
                            }
                            if (i >= 45) {
                                building.setItem(45, more);
                                building.setItem(46, more);
                                building.setItem(47, more);
                                building.setItem(48, more);
                                building.setItem(49, back);
                                building.setItem(50, more);
                                building.setItem(51, more);
                                building.setItem(52, more);
                                building.setItem(53, more);
                                break;
                            }
                            if (p.getMembers().contains(player.getUniqueId())) {
                                building.addItem(ItemUtil.create(Material.GRASS, ChatColor.GREEN + getOwner(p) + "'s Plot " +
                                        p.getId().toString(), Collections.singletonList(ChatColor.GREEN + "Rank: " +
                                        ChatColor.YELLOW + "Member")));
                                i++;
                            }
                            if (p.getTrusted().contains(player.getUniqueId())) {
                                building.addItem(ItemUtil.create(Material.GRASS, ChatColor.GREEN + getOwner(p) + "'s Plot " +
                                        p.getId().toString(), Collections.singletonList(ChatColor.GREEN + "Rank: " +
                                        ChatColor.GOLD + "" + ChatColor.ITALIC + "Trusted")));
                                i++;
                            }
                        }
                        if (building.getItem(22).getType().equals(Material.STAINED_CLAY)) {
                            building.setItem(22, new ItemStack(Material.AIR));
                        }
                        building.setItem(49, back);
                    }
                });
                break;
            }
            case HEADSHOP: {
                Inventory hs = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Heads");
                HashMap<String, List<ItemStack>> map = Creative.getInstance().getHeadUtil().getCategories();
                List<String> categories = new ArrayList<>(map.keySet());
                int place = 10;
                for (String s : categories) {
                    if (place >= 17) {
                        break;
                    }
                    ItemStack item = map.get(s).get(0).clone();
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(ChatColor.GREEN + s);
                    item.setItemMeta(meta);
                    hs.setItem(place, item);
                    place++;
                }
                hs.setItem(22, back);
                player.openInventory(hs);
                break;
            }
            case PARTICLE: {
                Inventory pt = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Particle Menu");
                pt.setItem(4, none);
                pt.setItem(9, note);
                pt.setItem(10, spark);
                pt.setItem(11, flame);
                pt.setItem(12, enchant);
                pt.setItem(13, mickey);
                pt.setItem(14, heart);
                pt.setItem(15, portal);
                pt.setItem(16, lava);
                pt.setItem(17, witch);
                pt.setItem(22, back);
                player.openInventory(pt);
                break;
            }
            case CREATIVESHOP: {
                Inventory inv = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Creative Shop");
                int limit = data.getRPLimit();
                if (limit >= 10) {
                    if (limit >= 15) {
                        if (limit >= 20) {
                            inv.setItem(1, ItemUtil.create(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.GREEN +
                                    "Role Play Expansion (10 Player)", Arrays.asList(ChatColor.GREEN + "You own this!")));
                            inv.setItem(2, ItemUtil.create(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.GREEN +
                                    "Role Play Expansion (15 Player)", Arrays.asList(ChatColor.GREEN + "You own this!")));
                            inv.setItem(3, ItemUtil.create(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.GREEN +
                                    "Role Play Expansion (20 Player)", Arrays.asList(ChatColor.GREEN + "You own this!")));
                        } else {
                            inv.setItem(1, ItemUtil.create(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.GREEN +
                                    "Role Play Expansion (10 Player)", Arrays.asList(ChatColor.GREEN + "You own this!")));
                            inv.setItem(2, ItemUtil.create(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.GREEN +
                                    "Role Play Expansion (15 Player)", Arrays.asList(ChatColor.GREEN + "You own this!")));
                            inv.setItem(3, ItemUtil.create(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.GREEN +
                                    "Role Play Expansion (20 Player)", Arrays.asList(ChatColor.YELLOW + "Price: " +
                                    ChatColor.GREEN + "$350", ChatColor.RED + "This can't be undone!")));
                        }
                    } else {
                        inv.setItem(1, ItemUtil.create(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.GREEN +
                                "Role Play Expansion (10 Player)", Arrays.asList(ChatColor.GREEN + "You own this!")));
                        inv.setItem(2, ItemUtil.create(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.GREEN +
                                "Role Play Expansion (15 Player)", Arrays.asList(ChatColor.YELLOW + "Price: " +
                                ChatColor.GREEN + "$300", ChatColor.RED + "This can't be undone!")));
                        inv.setItem(3, ItemUtil.create(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.GREEN +
                                "Role Play Expansion (20 Player)", Arrays.asList(ChatColor.YELLOW + "Price: " +
                                ChatColor.GREEN + "$350", ChatColor.RED + "This can't be undone!")));
                    }
                } else {
                    inv.setItem(1, ItemUtil.create(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.GREEN +
                            "Role Play Expansion (10 Player)", Arrays.asList(ChatColor.YELLOW + "Price: " +
                            ChatColor.GREEN + "$250", ChatColor.RED + "This can't be undone!")));
                    inv.setItem(2, ItemUtil.create(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.GREEN +
                            "Role Play Expansion (15 Player)", Arrays.asList(ChatColor.YELLOW + "Price: " +
                            ChatColor.GREEN + "$300", ChatColor.RED + "This can't be undone!")));
                    inv.setItem(3, ItemUtil.create(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.GREEN +
                            "Role Play Expansion (20 Player)", Arrays.asList(ChatColor.YELLOW + "Price: " +
                            ChatColor.GREEN + "$350", ChatColor.RED + "This can't be undone!")));
                }
                if (data.hasRPTag()) {
                    inv.setItem(11, ItemUtil.create(Material.SIGN, ChatColor.GREEN + "Role Play Tag",
                            Arrays.asList(ChatColor.GREEN + "You own this!")));
                } else {
                    inv.setItem(11, ItemUtil.create(Material.SIGN, ChatColor.GREEN + "Role Play Tag",
                            Arrays.asList(ChatColor.YELLOW + "Price: " + ChatColor.GREEN + "✪ 100", ChatColor.RED +
                                    "This can't be undone!")));
                }
                if (api.getPlayerPlots(Bukkit.getWorld("plotworld"), player).size() == 1) {
                    inv.setItem(13, purchase);
                }
                if (data.hasShowCreator()) {
                    inv.setItem(15, ItemUtil.create(Material.FIREWORK, ChatColor.GREEN + "Show Creator",
                            Arrays.asList(ChatColor.GREEN + "You own this!")));
                } else {
                    inv.setItem(15, ItemUtil.create(Material.FIREWORK, ChatColor.GREEN + "Show Creator",
                            Arrays.asList(ChatColor.YELLOW + "Price: " + ChatColor.GREEN + "$500", ChatColor.RED +
                                    "This can't be undone!")));
                }
                inv.setItem(22, back);
                player.openInventory(inv);
                break;
            }
            case PLOT_SETTINGS: {
                Plot plot = api.getPlot(player);
                HashMap<Flag<?>, Object> flags = plot.getFlags();
                long time = 3000;
                PlotWeather weather = PlotWeather.CLEAR;
                for (Map.Entry<Flag<?>, Object> entry : flags.entrySet()) {
                    if (entry.getKey().getName().equalsIgnoreCase("time")) {
                        time = (long) entry.getValue();
                    } else if (entry.getKey().getName().equalsIgnoreCase("weather")) {
                        weather = (PlotWeather) entry.getValue();
                    }
                }
                Inventory inv = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Plot Settings");
                List<String> current = Arrays.asList(ChatColor.YELLOW + "Currently Selected!");
                List<String> not = Arrays.asList(ChatColor.GRAY + "Click to Select!");
                inv.setItem(3, ItemUtil.create(Material.DOUBLE_PLANT, ChatColor.GREEN + "Clear",
                        weather.equals(PlotWeather.CLEAR) ? current : not));
                inv.setItem(4, ItemUtil.create(Material.LONG_GRASS, 1, (byte) 1, ChatColor.DARK_GREEN +
                        "Change Biome", new ArrayList<>()));
                inv.setItem(5, ItemUtil.create(Material.WATER_BUCKET, ChatColor.GREEN + "Rain",
                        weather.equals(PlotWeather.RAIN) ? current : not));
                inv.setItem(9, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "6AM", time == 0 ? current : not));
                inv.setItem(10, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "9AM", time == 3000 ? current : not));
                inv.setItem(11, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "12PM", time == 6000 ? current : not));
                inv.setItem(12, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "3PM", time == 9000 ? current : not));
                inv.setItem(14, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "6PM", time == 12000 ? current : not));
                inv.setItem(15, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "9PM", time == 15000 ? current : not));
                inv.setItem(16, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "12AM", time == 18000 ? current : not));
                inv.setItem(17, ItemUtil.create(Material.WATCH, ChatColor.GREEN + "3AM", time == 21000 ? current : not));
                inv.setItem(22, back);
                player.openInventory(inv);
                break;
            }
            case CHANGE_BIOME: {
                Plot plot = api.getPlot(player);
                String biome = plot.getBiome();
                List<String> empty = new ArrayList<>();
                List<String> selected = Arrays.asList(ChatColor.YELLOW + "Currently Selected");
                Inventory inv = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Change Biome");
                inv.setItem(10, ItemUtil.create(Material.LONG_GRASS, 1, (byte) 1, ChatColor.GREEN +
                        "Plains", biome.equalsIgnoreCase("plains") ? selected : empty));
                inv.setItem(11, ItemUtil.create(Material.DEAD_BUSH, ChatColor.YELLOW + "Desert",
                        biome.equalsIgnoreCase("desert") ? selected : empty));
                inv.setItem(12, ItemUtil.create(Material.SAPLING, 1, (byte) 1, ChatColor.DARK_GREEN +
                        "Forest", biome.equalsIgnoreCase("forest") ? selected : empty));
                inv.setItem(13, ItemUtil.create(Material.VINE, ChatColor.DARK_GREEN + "Swampland",
                        biome.equalsIgnoreCase("swampland") ? selected : empty));
                inv.setItem(14, ItemUtil.create(Material.SAPLING, 1, (byte) 3, ChatColor.DARK_GREEN +
                        "Jungle", biome.equalsIgnoreCase("jungle") ? selected : empty));
                inv.setItem(15, ItemUtil.create(Material.STAINED_CLAY, 1, (byte) 1, ChatColor.GOLD +
                        "Mesa", biome.equalsIgnoreCase("mesa") ? selected : empty));
                inv.setItem(16, ItemUtil.create(Material.PACKED_ICE, ChatColor.AQUA + "Ice Plains (Snow)",
                        biome.equalsIgnoreCase("ice_flats") ? selected : empty));
                inv.setItem(22, back);
                player.openInventory(inv);
                break;
            }
        }
    }

    private String getOwner(Plot p) {
        List<UUID> list = new ArrayList<>(p.getOwners());
        return Bukkit.getOfflinePlayer(list.get(0)).getName();
    }

    @SuppressWarnings("unchecked")
    public void handleClick(InventoryClickEvent event, CreativeInventoryType type) {
        try {
            final Player player = (Player) event.getWhoClicked();
            final PlotPlayer tp = BukkitUtil.getPlayer(player);
            ItemStack item = event.getCurrentItem();
            if (item == null) {
                return;
            }
            ItemMeta meta = item.getItemMeta();
            if (meta == null || meta.getDisplayName() == null) {
                return;
            }
            String name = ChatColor.stripColor(meta.getDisplayName());
            boolean isBack = item.getType().equals(Material.ARROW);
            event.setCancelled(true);
            switch (type) {
                case MAIN: {
                    switch (name.toLowerCase()) {
                        case "banner creator":
                            Creative.getInstance().getBannerUtil().openMenu(player, BannerInventoryType.SELECT_BASE);
                            break;
                        case "my plots":
                            openMenu(player, CreativeInventoryType.MY_PLOTS);
                            break;
                        case "creative shop":
                            openMenu(player, CreativeInventoryType.CREATIVESHOP);
                            break;
                        case "plot settings":
                            openMenu(player, CreativeInventoryType.PLOT_SETTINGS);
                            break;
                        case "spawn":
                            player.performCommand("spawn");
                            player.closeInventory();
                            break;
                        case "particles":
                            player.performCommand("pt");
                            break;
                        case "building plots":
                            openMenu(player, CreativeInventoryType.BUILDING_PLOTS);
                            break;
                        case "headshop":
                            openMenu(player, CreativeInventoryType.HEADSHOP);
                            break;
                        case "show creator":
                            Creative.getInstance().getShowManager().editShow(player);
                            break;
                    }
                    break;
                }
                case MY_PLOTS: {
                    if (isBack) {
                        openMenu(player, CreativeInventoryType.MAIN);
                        return;
                    }
                    if (item.getType().equals(Material.WOOL)) {
                        givePlot(player, true);
                        return;
                    }
                    Plot pl = null;
                    for (Plot p : new ArrayList<>(api.getPlayerPlots(player))) {
                        if (p.getId().toString().equals(name.replace("Plot ID: ", ""))) {
                            pl = p;
                        }
                    }
                    openManagePlot(player, pl);
                    break;
                }
                case MANAGE_PLOT: {
                    if (isBack) {
                        openMenu(player, CreativeInventoryType.MY_PLOTS);
                        return;
                    }
                    Plot plot = null;
                    for (Plot p : new ArrayList<>(api.getPlayerPlots(player))) {
                        if (p.getId().toString().equals(event.getInventory().getName().replace(ChatColor.BLUE +
                                "Manage Plot ", ""))) {
                            plot = p;
                        }
                    }
                    if (Core.getPlayerManager().getPlayer(player).getRank().getRankId() >= Rank.KNIGHT.getRankId()) {
                        if (plot == null) {
                            plot = MainUtil.getPlotFromString(tp, event.getInventory().getName().replace(ChatColor.BLUE +
                                    "Manage Plot ", ""), false);
                        }
                    }
                    if (plot == null) {
                        player.sendMessage(ChatColor.RED + "There was a problem performing this action! (Error Code 110)");
                        player.closeInventory();
                        return;
                    }
                    switch (name.toLowerCase()) {
                        case "add a player":
                            openAddOrTrust(player, plot);
                            break;
                        case "deny a player":
                            denying.put(player.getUniqueId(), plot);
                            player.closeInventory();
                            Core.getPlayerManager().getPlayer(player).getTitle().show(ChatColor.RED + "Deny a Player",
                                    ChatColor.GREEN + "Type the player's name in chat", 0, 0, 200);
                            break;
                        case "teleport to plot":
                            Location loc = getHome(plot);
                            player.teleport(loc);
                            player.sendMessage(ChatColor.GREEN + "Teleported to Plot " + plot.getId().toString());
                            break;
                        case "added players":
                            openAddedPlayers(player, tp, plot);
                            break;
                        case "denied players":
                            openDeniedPlayers(player, tp, plot);
                            break;
                    }
                    break;
                }
                case ADD_PLAYER: {
                    Plot plawrt = null;
                    for (Plot p : new ArrayList<>(api.getPlayerPlots(player))) {
                        if (p.getId().toString().equals(event.getInventory().getName().replace(ChatColor.BLUE +
                                "Add Player to Plot ", ""))) {
                            plawrt = p;
                        }
                    }
                    if (isBack) {
                        if (plawrt == null) {
                            openMenu(player, CreativeInventoryType.MAIN);
                            return;
                        }
                        openManagePlot(player, plawrt);
                        return;
                    }
                    if (Core.getPlayerManager().getPlayer(player).getRank().getRankId() >= Rank.KNIGHT.getRankId()) {
                        if (plawrt == null) {
                            plawrt = MainUtil.getPlotFromString(tp, event.getInventory().getName().replace(ChatColor.BLUE +
                                    "Add Player to Plot ", ""), false);
                        }
                    }
                    if (plawrt == null) {
                        player.sendMessage(ChatColor.RED + "There was a problem performing this action! (Error Code 110)");
                        player.closeInventory();
                        return;
                    }
                    switch (name.toLowerCase()) {
                        case "member":
                            adding.put(player.getUniqueId(), plawrt);
                            player.closeInventory();
                            Core.getPlayerManager().getPlayer(player).getTitle().show(ChatColor.GREEN + "Add a Member",
                                    ChatColor.GREEN + "Type the player's name in chat", 0, 0, 200);
                            break;
                        case "trusted":
                            trusting.put(player.getUniqueId(), plawrt);
                            player.closeInventory();
                            Core.getPlayerManager().getPlayer(player).getTitle().show(ChatColor.GREEN + "Trust a Player",
                                    ChatColor.GREEN + "Type the player's name in chat", 0, 0, 200);
                            break;
                    }
                    break;
                }
                case BUILDING_PLOTS: {
                    if (isBack) {
                        openMenu(player, CreativeInventoryType.MAIN);
                        return;
                    }
                    if (!name.contains(" ")) {
                        return;
                    }
                    String id = name.split(" ")[2];
                    Plot plot = null;
                    for (PlotArea a : api.getPlotAreas(Bukkit.getWorld("plotworld"))) {
                        plot = a.getPlot(PlotId.fromString(id));
                        if (plot != null) break;
                    }
                    if (plot == null) {
                        player.sendMessage(ChatColor.RED + "There was a problem performing this action! (Error Code 110)");
                        player.closeInventory();
                        return;
                    }
                    Location loc = getHome(plot);
                    player.teleport(loc);
                    player.sendMessage(ChatColor.GREEN + "Teleported to " + name);
                    break;
                }
                case HEADSHOP: {
                    if (isBack) {
                        openMenu(player, CreativeInventoryType.MAIN);
                        return;
                    }
                    Creative.getInstance().getHeadUtil().openCategory(player, name);
                    break;
                }
                case ADDED_PLAYERS: {
                    Plot plert = null;
                    for (Plot p : api.getPlayerPlots(player)) {
                        if (p.getId().toString().equals(event.getInventory().getName().replace(ChatColor.BLUE +
                                "Added Players ", ""))) {
                            plert = p;
                        }
                    }
                    if (Core.getPlayerManager().getPlayer(player).getRank().getRankId() >= Rank.KNIGHT.getRankId()) {
                        if (plert == null) {
                            plert = MainUtil.getPlotFromString(tp, event.getInventory().getName().replace(ChatColor.BLUE +
                                    "Added Players ", ""), false);
                        }
                    }
                    if (plert == null) {
                        player.sendMessage(ChatColor.RED + "There was a problem performing this action! (Error Code 110)");
                        player.closeInventory();
                        return;
                    }
                    if (isBack) {
                        openManagePlot(player, plert);
                        return;
                    }
                    boolean left = event.isLeftClick();
                    List<String> lore = item.getItemMeta().getLore();
                    UUID uuid = UUID.fromString(ChatColor.stripColor(lore.get(lore.size() - 1)));
                    if (event.isRightClick()) {
                        if (plert.getTrusted().contains(uuid)) {
                            plert.removeTrusted(uuid);
                        } else if (plert.getMembers().contains(uuid)) {
                            plert.removeMember(uuid);
                        } else if (plert.getDenied().contains(uuid)) {
                            plert.removeDenied(uuid);
                        }
                        player.sendMessage(ChatColor.GREEN + name + " is no longer Added to Plot " +
                                plert.getId().toString());
                        openAddedPlayers(player, tp, plert);
                    } else {
                        if (plert.getTrusted().contains(uuid)) {
                            if (!plert.removeTrusted(uuid)) {
                                if (plert.getDenied().contains(uuid)) {
                                    plert.removeDenied(uuid);
                                }
                            }
                            plert.addMember(uuid);
                            EventUtil.manager.callMember(PlotPlayer.wrap(player), plert, uuid, true);
                            player.sendMessage(ChatColor.GREEN + name + " is now a " + ChatColor.YELLOW + "Member " +
                                    ChatColor.GREEN + "on Plot " + plert.getId().toString());
                        } else if (plert.getMembers().contains(uuid)) {
                            if (!plert.removeMember(uuid)) {
                                if (plert.getDenied().contains(uuid)) {
                                    plert.removeDenied(uuid);
                                }
                            }
                            plert.addTrusted(uuid);
                            EventUtil.manager.callTrusted(PlotPlayer.wrap(player), plert, uuid, true);
                            player.sendMessage(ChatColor.GREEN + name + " is now " + ChatColor.GOLD + "" +
                                    ChatColor.ITALIC + "trusted " + ChatColor.GREEN + "on Plot " +
                                    plert.getId().toString());
                        }
                        openAddedPlayers(player, tp, plert);
                    }
                    break;
                }
                case CREATIVESHOP: {
                    if (isBack) {
                        openMenu(player, CreativeInventoryType.MAIN);
                        return;
                    }
                    PlayerData data = Creative.getInstance().getPlayerData(player.getUniqueId());
                    int limit = data.getRPLimit();
                    switch (name.toLowerCase()) {
                        case "role play expansion (10 player)": {
                            if (limit >= 10) {
                                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 25, 1);
                                player.sendMessage(ChatColor.RED + "You have this already!");
                                return;
                            }
                            int balance = Core.getEconomy().getBalance(player.getUniqueId());
                            if (balance < 250) {
                                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 25, 1);
                                player.sendMessage(ChatColor.RED + "You can't afford this!");
                                return;
                            }
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 5f, 2f);
                            player.closeInventory();
                            purchaseParticle(player);
                            Core.getEconomy().addBalance(player.getUniqueId(), -250);
                            data.setRPLimit(10);
                            setValue(player.getUniqueId(), "rplimit", 10);
                            break;
                        }
                        case "role play expansion (15 player)": {
                            if (limit >= 15) {
                                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 25, 1);
                                player.sendMessage(ChatColor.RED + "You have this already!");
                                return;
                            }
                            if (limit < 10) {
                                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 25, 1);
                                player.sendMessage(ChatColor.RED + "You must purchase the previous tier first!!");
                                return;
                            }
                            int balance = Core.getEconomy().getBalance(player.getUniqueId());
                            if (balance < 300) {
                                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 25, 1);
                                player.sendMessage(ChatColor.RED + "You can't afford this!");
                                return;
                            }
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 5f, 2f);
                            player.closeInventory();
                            purchaseParticle(player);
                            Core.getEconomy().addBalance(player.getUniqueId(), -300);
                            data.setRPLimit(15);
                            setValue(player.getUniqueId(), "rplimit", 15);
                            break;
                        }
                        case "role play expansion (20 player)": {
                            if (limit >= 20) {
                                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 25, 1);
                                player.sendMessage(ChatColor.RED + "You have this already!");
                                return;
                            }
                            if (limit < 15) {
                                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 25, 1);
                                player.sendMessage(ChatColor.RED + "You must purchase the previous tier first!!");
                                return;
                            }
                            int balance = Core.getEconomy().getBalance(player.getUniqueId());
                            if (balance < 350) {
                                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 25, 1);
                                player.sendMessage(ChatColor.RED + "You can't afford this!");
                                return;
                            }
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 5f, 2f);
                            player.closeInventory();
                            purchaseParticle(player);
                            Core.getEconomy().addBalance(player.getUniqueId(), -350);
                            data.setRPLimit(20);
                            setValue(player.getUniqueId(), "rplimit", 20);
                            break;
                        }
                        case "role play tag": {
                            if (data.hasRPTag()) {
                                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 25, 1);
                                player.sendMessage(ChatColor.RED + "You have this already!");
                                return;
                            }
                            int tokens = Core.getEconomy().getTokens(player.getUniqueId());
                            if (tokens < 100) {
                                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 25, 1);
                                player.sendMessage(ChatColor.RED + "You can't afford this!");
                                return;
                            }
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 5f, 2f);
                            player.closeInventory();
                            purchaseParticle(player);
                            Core.getEconomy().addTokens(player.getUniqueId(), -100);
                            data.setHasRPTag(true);
                            setValue(player.getUniqueId(), "rptag", 1);
                            break;
                        }
                        case "purchase second plot": {
                            int balance = Core.getEconomy().getBalance(player.getUniqueId());
                            if (balance < 5000) {
                                player.sendMessage(ChatColor.RED + "You cannot afford a Second Plot! You need "
                                        + ChatColor.GREEN + "$" + (5000 - balance) + "!");
                                player.closeInventory();
                                return;
                            }
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 5f, 2f);
                            player.closeInventory();
                            purchaseParticle(player);
                            Core.getEconomy().addBalance(player.getUniqueId(), -5000);
                            givePlot(player, true);
                            break;
                        }
                        case "show creator": {
                            int balance = Core.getEconomy().getBalance(player.getUniqueId());
                            if (data.hasShowCreator()) {
                                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 25, 1);
                                player.sendMessage(ChatColor.RED + "You have this already!");
                                return;
                            }
                            if (balance < 500) {
                                player.sendMessage(ChatColor.RED + "You cannot afford the Show Creator! You need "
                                        + ChatColor.GREEN + "$" + (500 - balance) + "!");
                                player.closeInventory();
                                return;
                            }
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 5f, 2f);
                            player.closeInventory();
                            purchaseParticle(player);
                            Core.getEconomy().addBalance(player.getUniqueId(), -500);
                            data.setHasShowCreator(true);
                            player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "\nHOW TO USE: " + ChatColor.GREEN +
                                    "Type /show to use the Show Creator!\n ");
                            setValue(player.getUniqueId(), "showcreator", 1);
                            break;
                        }
                    }
                    break;
                }
                case DENIED_PLAYERS: {
                    Plot plawt = null;
                    for (Plot p : api.getPlayerPlots(player)) {
                        if (p.getId().toString().equals(event.getInventory().getName().replace(ChatColor.BLUE +
                                "Denied Players ", ""))) {
                            plawt = p;
                        }
                    }
                    if (Core.getPlayerManager().getPlayer(player).getRank().getRankId() >= Rank.KNIGHT.getRankId()) {
                        if (plawt == null) {
                            plawt = MainUtil.getPlotFromString(tp, event.getInventory().getName().replace(ChatColor.BLUE +
                                    "Denied Players ", ""), false);
                        }
                    }
                    if (plawt == null) {
                        player.sendMessage(ChatColor.RED + "There was a problem performing this action! (Error Code 110)");
                        player.closeInventory();
                        return;
                    }
                    if (isBack) {
                        openManagePlot(player, plawt);
                        return;
                    }
                    plawt.removeDenied(Bukkit.getOfflinePlayer(name).getUniqueId());
                    player.sendMessage(ChatColor.GREEN + name + " is no longer Denied on Plot " + plawt.getId().toString());
                    openDeniedPlayers(player, tp, plawt);
                    break;
                }
                case PARTICLE: {
                    if (isBack) {
                        openMenu(player, CreativeInventoryType.MAIN);
                        return;
                    }
                    if (item.getType().equals(Material.STAINED_GLASS_PANE)) {
                        Creative.getInstance().getParticleManager().clearParticle(Core.getPlayerManager().getPlayer(player));
                        return;
                    }
                    Creative.getInstance().getParticleManager().setParticle(Core.getPlayerManager().getPlayer(player), name.toLowerCase(), meta.getDisplayName());
                    break;
                }
                case PLOT_SETTINGS: {
                    if (isBack) {
                        openMenu(player, CreativeInventoryType.MAIN);
                        return;
                    }
                    Plot plot = api.getPlot(player);
                    if (event.getSlot() == 4) {
                        openMenu(player, CreativeInventoryType.CHANGE_BIOME);
                        return;
                    }
                    if (event.getSlot() < 9) {
                        //Weather
                        PlotWeather weather = getWeather(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                        final Flag flag = FlagManager.getFlag("weather");//new Flag(FlagManager.getFlag("weather", true), weather.getType().toLowerCase());
                        Object parsed = flag.parseValue(weather.name());
                        final boolean result = plot.setFlag(flag, parsed);
                        if (result) {
                            player.sendMessage(ChatColor.GREEN + "Set Plot Weather to " + item.getItemMeta().getDisplayName() + "!");
                            openMenu(player, CreativeInventoryType.PLOT_SETTINGS);
                        } else {
                            player.sendMessage(ChatColor.RED + "Error setting Plot Weather! Please report this to a Cast Member.");
                        }
                        break;
                    }
                    long time = getTime(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                    final Flag flag = FlagManager.getFlag("time");//new Flag(FlagManager.getFlag("time", true), time);
                    Object parsed = flag.parseValue(String.valueOf(time));
                    final boolean result = plot.setFlag(flag, parsed);
                    if (result) {
                        player.sendMessage(ChatColor.GREEN + "Set Plot Time to " + item.getItemMeta().getDisplayName() + "!");
                        openMenu(player, CreativeInventoryType.PLOT_SETTINGS);
                    } else {
                        player.sendMessage(ChatColor.RED + "Error setting Plot Time! Please report this to a Cast Member.");
                    }
                    break;
                }
                case CHANGE_BIOME: {
                    if (isBack) {
                        openMenu(player, CreativeInventoryType.PLOT_SETTINGS);
                        return;
                    }
                    Plot plot = api.getPlot(player);
                    String biome = "plains";
                    switch (event.getSlot()) {
                        case 10:
                            biome = "plains";
                            break;
                        case 11:
                            biome = "desert";
                            break;
                        case 12:
                            biome = "forest";
                            break;
                        case 13:
                            biome = "swampland";
                            break;
                        case 14:
                            biome = "jungle";
                            break;
                        case 15:
                            biome = "mesa";
                            break;
                        case 16:
                            biome = "ice_flats";
                            break;
                    }
                    if (plot.getBiome().equalsIgnoreCase(biome)) {
                        player.sendMessage(ChatColor.RED + "Your plot is already set to this biome!");
                        return;
                    }
                    if (plot.getRunning() > 0) {
                        player.sendMessage(ChatColor.RED + "Your plot is currently executing a task, try again in a few minutes.");
                        return;
                    }
                    player.sendMessage(ChatColor.GREEN + "Changing your plot's biome...");
                    player.closeInventory();
                    plot.addRunning();
                    String finalBiome = biome;
                    plot.setBiome(biome.toUpperCase(), () -> {
                        plot.removeRunning();
                        player.sendMessage(ChatColor.GREEN + "Your plot's biome was set to " + ChatColor.YELLOW +
                                finalBiome.toLowerCase());
                    });
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PlotWeather getWeather(String s) {
        switch (s.toLowerCase()) {
            case "clear":
                return PlotWeather.CLEAR;
            case "rain":
                return PlotWeather.RAIN;
        }
        return PlotWeather.RESET;
    }

    private long getTime(String s) {
        switch (s) {
            case "6AM":
                return 0;
            case "9AM":
                return 3000;
            case "12PM":
                return 6000;
            case "3PM":
                return 9000;
            case "6PM":
                return 12000;
            case "9PM":
                return 15000;
            case "12AM":
                return 18000;
            case "3AM":
                return 21000;
        }
        return 0;
    }

    private void setValue(UUID uuid, String name, Object o) {
        Bukkit.getScheduler().runTaskAsynchronously(Creative.getInstance(), new Runnable() {
            @Override
            public void run() {
                try (Connection connection = Core.getSqlUtil().getConnection()) {
                    PreparedStatement sql = connection.prepareStatement("UPDATE creative SET " + name + "=? WHERE uuid=?");
                    sql.setObject(1, o);
                    sql.setString(2, uuid.toString());
                    sql.execute();
                    sql.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void playRecord(Player player, String name) {
        List<PlotPlayer> list = MainUtil.getPlotFromString(BukkitUtil.getPlayer(player), null, false).getPlayersInPlot();
        for (PlotPlayer pl : list) {
            Player tp = Bukkit.getPlayer(pl.getUUID());
            tp.playEffect(tp.getLocation(), Effect.RECORD_PLAY, getId(name));
        }
    }

    private int getId(String name) {
        switch (name) {
            case "13":
                return 2256;
            case "cat":
                return 2257;
            case "blocks":
                return 2258;
            case "chirp":
                return 2259;
            case "far":
                return 2260;
            case "mall":
                return 2261;
            case "mellohi":
                return 2262;
            case "stal":
                return 2263;
            case "strad":
                return 2264;
            case "ward":
                return 2265;
            case "11":
                return 2266;
            case "wait":
                return 2267;
        }
        return 0;
    }

    public Location getHome(Plot plot) {
        com.intellectualcrafters.plot.object.Location home = plot.getHome();
        return new Location(Bukkit.getWorld(home.getWorld()), home.getX(), home.getY(), home.getZ(),
                home.getYaw(), home.getPitch());
    }

    private void openAddOrTrust(Player tp, Plot plot) {
        Inventory inv = Bukkit.createInventory(tp, 27, ChatColor.BLUE + "Add Player to Plot " + plot.getId().toString());
        inv.setItem(11, member);
        inv.setItem(15, trusted);
        inv.setItem(22, back);
        tp.openInventory(inv);
    }

    public void openManagePlot(Player tp, Plot plot) {
        if (!plot.hasOwner()) {
            tp.sendMessage(ChatColor.RED + "This plot is not owned right now!");
            return;
        }
        Inventory manage = Bukkit.createInventory(tp, 27, ChatColor.BLUE + "Manage Plot " +
                plot.getId().toString());
        manage.setItem(9, network.palace.core.utils.HeadUtil.getPlayerHead(Core.getPlayerManager().getPlayer(tp)
                .getTextureValue(), ChatColor.GREEN + "Add a Player"));
        manage.setItem(11, deny);
        manage.setItem(13, teleport);
        manage.setItem(15, members);
        manage.setItem(17, denied);
        manage.setItem(22, back);
        tp.openInventory(manage);
    }

    public void givePlot(final Player player, boolean spawn) {
        player.closeInventory();
        final long time = System.currentTimeMillis();
        player.sendMessage(ChatColor.GREEN + "Finding you a plot right now...");
        PlotPlayer plr = BukkitUtil.getPlayer(player);
        String world;
        if (PS.get().getPlotWorldStrings().size() == 1) {
            world = PS.get().getPlotWorldStrings().iterator().next();
        } else {
            world = plr.getLocation().getWorld();
            if (spawn) {
                world = "plotworld";
            }
            if (!PS.get().isPlotWorld(world)) {
                player.sendMessage(ChatColor.RED + "You're not in a PlotWorld!");
                return;
            }
        }
        if (!world.equalsIgnoreCase("plotworld")) {
            world = "plotworld";
        }
        final String worldname = world;
        PlotArea plotarea = PS.get().getPlotArea(worldname, worldname);
        plotarea.setMeta("lastPlot", new PlotId(0, 0));
        while (true) {
            PlotId start = getNextPlotId(getLastPlotId(plotarea), 1);
            PlotId end = new PlotId(start.x, start.y);
            plotarea.setMeta("lastPlot", start);
            if (plotarea.canClaim(plr, start, end)) {
                for (int i = start.x; i <= end.x; i++) {
                    for (int j = start.y; j <= end.y; j++) {
                        Plot plot = plotarea.getPlotAbs(new PlotId(i, j));
                        boolean teleport = i == end.x && j == end.y;
                        plot.claim(plr, teleport, null);
                    }
                }
                break;
            }
        }
        player.sendMessage(ChatColor.GREEN + "Here's your Plot! Get to it with /menu. " + ChatColor.DARK_AQUA +
                "(Took " + (System.currentTimeMillis() - time) + "ms)");
        Core.getPlayerManager().getPlayer(player.getUniqueId()).giveAchievement(9);
    }

    public static PlotId getNextPlotId(PlotId id, int step) {
        int absX = Math.abs(id.x);
        int absY = Math.abs(id.y);
        if (absX > absY) {
            if (id.x > 0) {
                return new PlotId(id.x, id.y + 1);
            } else {
                return new PlotId(id.x, id.y - 1);
            }
        } else if (absY > absX) {
            if (id.y > 0) {
                return new PlotId(id.x - 1, id.y);
            } else {
                return new PlotId(id.x + 1, id.y);
            }
        } else {
            if (id.x == id.y && id.x > 0) {
                return new PlotId(id.x, id.y + step);
            }
            if (id.x == absX) {
                return new PlotId(id.x, id.y + 1);
            }
            if (id.y == absY) {
                return new PlotId(id.x, id.y - 1);
            }
            return new PlotId(id.x + 1, id.y);
        }
    }

    public PlotId getLastPlotId(PlotArea area) {
        PlotId value = (PlotId) area.getMeta("lastPlot");
        if (value == null) {
            value = new PlotId(0, 0);
            area.setMeta("lastPlot", value);
            return value;
        }
        return value;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Creative.getInstance().getPlayerData(player.getUniqueId()).resetAction();
        if (!trusting.containsKey(player.getUniqueId())) {
            if (!adding.containsKey(player.getUniqueId())) {
                if (!denying.containsKey(player.getUniqueId())) {
                    event.setCancelled(true);
                    if (Creative.getInstance().getShowManager().isEditing(player.getUniqueId())) {
                        Creative.getInstance().getShowManager().handleChat(event, Core.getPlayerManager().getPlayer(player));
                        return;
                    }
                    RolePlay rp = Creative.getInstance().getRolePlayUtil().getRolePlay(player.getUniqueId());
                    if (rp != null) {
                        rp.chat(player, event.getMessage());
                        return;
                    }
                    CPlayer cplayer = Core.getPlayerManager().getPlayer(player);
                    Rank rank = cplayer.getRank();
                    PlayerData data = Creative.getInstance().getPlayerData(player.getUniqueId());
                    String msg;
                    if (rank.getRankId() > Rank.SQUIRE.getRankId()) {
                        msg = ChatColor.translateAlternateColorCodes('&', event.getMessage());
                    } else {
                        msg = event.getMessage();
                    }
                    String messageToSend = (data.hasCreatorTag() ? (ChatColor.WHITE + "[" + ChatColor.BLUE + "Creator"
                            + ChatColor.WHITE + "] ") : "") + rank.getFormattedName() + " " + ChatColor.GRAY +
                            player.getName() + ": " + rank.getChatColor() + msg;
                    Bukkit.getOnlinePlayers().stream().filter(tp -> Creative.getInstance().getRolePlayUtil()
                            .getRolePlay(tp.getUniqueId()) == null).forEach(tp -> tp.sendMessage(messageToSend));
                    return;
                }
                event.setCancelled(true);
                String name = event.getMessage();
                Plot plot = denying.remove(player.getUniqueId());
                if (name.equalsIgnoreCase(player.getName()) && plot.getOwners().contains(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "You cannot deny yourself, you'll never get on again!");
                    return;
                }
                if (name.equals("*")) {
                    player.sendMessage(ChatColor.RED + "You should never deny " + ChatColor.ITALIC + "everyone " +
                            ChatColor.RED + "from your plot!");
                    return;
                }
                Player tp = getPlayer(name);
                if (tp == null) {
                    player.sendMessage(ChatColor.RED + "No player was found by that name! (They have to be online)");
                    return;
                }
                if (plot.getDenied().size() >= 18) {
                    player.sendMessage(ChatColor.RED + "You cannot deny more than 18 people on your Plot!");
                    return;
                }
                if (plot.getDenied().contains(tp.getUniqueId().toString())) {
                    player.sendMessage(ChatColor.RED + "This player is already denied on this Plot!");
                    return;
                }
                if (api.getPlot(tp.getLocation()) != null) {
                    if (api.getPlot(tp.getLocation()).getId().toString().equals(plot.getId().toString())) {
                        denyTask.add(tp.getUniqueId());
                        tp.sendMessage(ChatColor.RED + "You were denied from " + player.getName() + "'s Plot!");
                    }
                }
                if (isAdded(plot, tp)) {
                    plot.removeMember(tp.getUniqueId());
                    plot.removeTrusted(tp.getUniqueId());
                }
                plot.addDenied(tp.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "Successfully denied " + tp.getName() + " from Plot " +
                        plot.getId().toString());
                return;
            }
            event.setCancelled(true);
            String name = event.getMessage();
            Plot plot = adding.remove(player.getUniqueId());
            String owner = getOwner(plot);
            if (name.equalsIgnoreCase(player.getName()) && plot.getOwners().contains(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You're already added to this Plot!");
                return;
            }
            if (name.equals("*")) {
                player.sendMessage(ChatColor.RED + "You should never add " + ChatColor.ITALIC + "everyone " +
                        ChatColor.RED + "to your plot!");
                return;
            }
            Player tp = getPlayer(name);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "No player was found by that name! (They have to be online)");
                return;
            }
            if (plot.isDenied(tp.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "This player is Denied from this Plot! You must un-deny them first.");
                return;
            }
            if ((plot.getMembers().size() + plot.getTrusted().size()) >= 18) {
                player.sendMessage(ChatColor.RED + "You cannot add more than 18 people to your Plot!");
                return;
            }
            if (isAdded(plot, tp)) {
                player.sendMessage(ChatColor.RED + "This player is already added to this Plot!");
                return;
            }
            plot.addMember(tp.getUniqueId());
            EventUtil.manager.callMember(PlotPlayer.wrap(player), plot, tp.getUniqueId(), true);
            player.sendMessage(ChatColor.GREEN + "Successfully added " + tp.getName() + " to Plot " +
                    plot.getId().toString());
            tp.sendMessage(ChatColor.GREEN + "You were added to " + ChatColor.YELLOW + owner + "'s Plot! " +
                    ChatColor.GREEN + "Use /menu to get to it.");
            return;
        }
        event.setCancelled(true);
        String name = event.getMessage();
        Plot plot = trusting.remove(player.getUniqueId());
        String owner = getOwner(plot);
        if (name.equalsIgnoreCase(player.getName()) && plot.getOwners().contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You're already added to this Plot!");
            return;
        }
        if (name.equals("*")) {
            player.sendMessage(ChatColor.RED + "You should never add " + ChatColor.ITALIC + "everyone " +
                    ChatColor.RED + "to your plot!");
            return;
        }
        Player tp = getPlayer(name);
        if (tp == null) {
            player.sendMessage(ChatColor.RED + "No player was found by that name! (They have to be online)");
            return;
        }
        if (plot.isDenied(tp.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "This player is Denied from this Plot! You must un-deny them first.");
            return;
        }
        if ((plot.getMembers().size() + plot.getTrusted().size()) >= 18) {
            player.sendMessage(ChatColor.RED + "You cannot add more than 18 people to your Plot!");
            return;
        }
        if (isAdded(plot, tp)) {
            player.sendMessage(ChatColor.RED + "This player is already added to this Plot!");
            return;
        }
        plot.addTrusted(tp.getUniqueId());
        EventUtil.manager.callTrusted(PlotPlayer.wrap(player), plot, tp.getUniqueId(), true);
        player.sendMessage(ChatColor.GREEN + "Successfully trusted " + tp.getName() + " to Plot " +
                plot.getId().toString());
        tp.sendMessage(ChatColor.GREEN + "You were " + ChatColor.GOLD + ChatColor.ITALIC + "trusted " + ChatColor.GREEN
                + "to " + ChatColor.YELLOW + owner + "'s Plot! " + ChatColor.GREEN + "Use /menu to get to it.");
    }

    private boolean isAdded(Plot plot, Player tp) {
        return plot.getMembers().contains(tp.getUniqueId()) || plot.getTrusted().contains(tp.getUniqueId());
    }

    private Player getPlayer(String name) {
        Player p = null;
        for (Player tp : Bukkit.getOnlinePlayers()) {
            if (tp.getName().equalsIgnoreCase(name)) {
                p = tp;
                break;
            }
        }
        return p;
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        event.setCancelled(true);
    }

    //@EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (Core.getPlayerManager().getPlayer(player).getRank().getRankId() >= Rank.KNIGHT.getRankId()) {
            return;
        }
        String msg = event.getMessage();
        if (event.getMessage().startsWith("/plot")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Don't use /plot anymore, use /menu!");
        }
    }

    private void openAddedPlayers(Player player, PlotPlayer tp, Plot plot) {
        Inventory inv = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Added Players " + plot.getId().toString());
        HashMap<UUID, MemberState> added = new HashMap<>();
        for (UUID uuid : plot.getTrusted()) {
            added.put(uuid, MemberState.TRUSTED);
        }
        for (UUID uuid : plot.getMembers()) {
            added.put(uuid, MemberState.MEMBER);
        }
        int i = 0;
        for (Map.Entry<UUID, MemberState> entry : added.entrySet()) {
            if (i >= 18) {
                break;
            }
            try {
                UUID uuid = entry.getKey();
                MemberState state = entry.getValue();
                ItemStack item;
                CPlayer cplayer = Core.getPlayerManager().getPlayer(uuid);
                if (cplayer == null) {
                    item = ItemUtil.create(Material.SKULL_ITEM, ChatColor.GRAY + Bukkit.getOfflinePlayer(uuid).getName());
                    ItemMeta meta = item.getItemMeta();
                    item.setItemMeta(meta);
                } else {
                    item = network.palace.core.utils.HeadUtil.getPlayerHead(cplayer.getTextureValue(), cplayer.getRank().getTagColor() + cplayer.getName());
                    ItemMeta meta = item.getItemMeta();
                    item.setItemMeta(meta);
                }
                ItemMeta meta = item.getItemMeta();
                switch (state) {
                    case MEMBER:
                        meta.setLore(Arrays.asList(ChatColor.GREEN + "Left-Click to change this Player from", ChatColor.YELLOW
                                + "Member " + ChatColor.GREEN + "to " + ChatColor.GOLD + "" + ChatColor.ITALIC +
                                "Trusted", ChatColor.RED + "Right-Click to Remove this Player!", ChatColor.BLACK +
                                "" + uuid));
                        break;
                    case TRUSTED:
                        meta.setLore(Arrays.asList(ChatColor.GREEN + "Left-Click to change this Player from", ChatColor.GOLD
                                + "" + ChatColor.ITALIC + "Trusted " + ChatColor.GREEN + "to " + ChatColor.YELLOW +
                                "Member", ChatColor.RED + "Right-Click to Remove this Player!", ChatColor.BLACK +
                                "" + uuid));
                        break;
                }
                item.setItemMeta(meta);
                inv.addItem(item);
            } catch (Exception ignored) {
            }
            i++;
        }
        inv.setItem(22, back);
        player.openInventory(inv);
    }

    private void openDeniedPlayers(Player player, PlotPlayer tp, Plot plot) {
        Inventory inv = Bukkit.createInventory(player, 27, ChatColor.BLUE + "Denied Players " + plot.getId().toString());
        List<UUID> denied = new ArrayList<>(plot.getDenied());
        for (int i = 0; i < denied.size(); i++) {
            if (i == 18) {
                break;
            }
            try {
                UUID uuid = denied.get(i);
                ItemStack item;
                CPlayer cplayer = Core.getPlayerManager().getPlayer(uuid);
                if (cplayer == null) {
                    item = ItemUtil.create(Material.SKULL_ITEM, ChatColor.GRAY + Bukkit.getOfflinePlayer(uuid).getName(),
                            Collections.singletonList(ChatColor.RED + "Click to Un-Deny this Player!"));
                } else {
                    item = network.palace.core.utils.HeadUtil.getPlayerHead(cplayer.getTextureValue(), cplayer.getRank().getTagColor() + cplayer.getName());
                    ItemMeta meta = item.getItemMeta();
                    meta.setLore(Collections.singletonList(ChatColor.RED + "Click to Un-Deny this Player!"));
                    item.setItemMeta(meta);
                }
                inv.addItem(item);
            } catch (Exception ignored) {
            }
        }
        inv.setItem(22, back);
        player.openInventory(inv);
    }

    private void purchaseParticle(Player player) {
        Core.getPlayerManager().getPlayer(player).getParticles().send(player.getLocation(), Particle.FIREWORKS_SPARK,
                30, 0, 0, 0, 0.25f);
    }
}