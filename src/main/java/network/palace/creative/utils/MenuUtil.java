package network.palace.creative.utils;

import com.google.common.collect.ImmutableMap;
import com.plotsquared.bukkit.player.BukkitPlayer;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import com.plotsquared.core.plot.PlotId;
import com.plotsquared.core.plot.PlotWeather;
import com.plotsquared.core.plot.flag.PlotFlag;
import com.plotsquared.core.plot.flag.implementations.FlyFlag;
import com.plotsquared.core.plot.flag.implementations.TimeFlag;
import com.plotsquared.core.plot.flag.implementations.WeatherFlag;
import com.sk89q.worldedit.world.biome.BiomeType;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import network.palace.core.Core;
import network.palace.core.economy.currency.CurrencyType;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.player.RankTag;
import network.palace.core.utils.HeadUtil;
import network.palace.core.utils.ItemUtil;
import network.palace.creative.Creative;
import network.palace.creative.handlers.BannerInventoryType;
import network.palace.creative.handlers.MemberState;
import network.palace.creative.handlers.PlayerData;
import network.palace.creative.handlers.RolePlay;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;

/**
 * Created by Marc on 7/29/15
 */
@SuppressWarnings("deprecation")
public class MenuUtil implements Listener {
    private final ItemStack bannerCreator = new ItemStack(Material.BLUE_BANNER);
    private final ItemStack plotTime = ItemUtil.create(Material.CLOCK, ChatColor.GREEN + "Plot Settings");
    private final ItemStack myPlots = ItemUtil.create(Material.GRASS_BLOCK, ChatColor.GREEN + "My Plots");
    private final ItemStack spawn = ItemUtil.create(Material.ENDER_PEARL, ChatColor.GREEN + "Spawn");
    private final ItemStack buildingPlots = ItemUtil.create(Material.DIRT, ChatColor.GREEN + "Building Plots");
    private final ItemStack headShop;
    private final ItemStack showCreator = ItemUtil.create(Material.FIREWORK_ROCKET, ChatColor.GREEN + "Show Creator");
    private final ItemStack teleport = ItemUtil.create(Material.ENDER_PEARL, ChatColor.GREEN + "Teleport to Plot");
    private final ItemStack deny = ItemUtil.create(Material.BARRIER, ChatColor.GREEN + "Deny a Player");
    private final ItemStack members = ItemUtil.create(Material.BOOK, ChatColor.GREEN + "Added Players");
    private final ItemStack denied = ItemUtil.create(Material.BOOK, ChatColor.GREEN + "Denied Players");
    private final ItemStack purchase = ItemUtil.create(Material.DIAMOND, ChatColor.GREEN + "Purchase Second Plot",
            Collections.singletonList(ChatColor.GRAY + "Cost: " + ChatColor.GREEN + "$5000"));
    private final ItemStack particles = ItemUtil.create(Material.BLAZE_POWDER, ChatColor.GREEN + "Particles");
    private final ItemStack shop = ItemUtil.create(Material.EMERALD, ChatColor.GREEN + "Creative Shop");
    public ItemStack next = ItemUtil.create(Material.ARROW, ChatColor.GREEN + "Next Page");
    public ItemStack back = ItemUtil.create(Material.ARROW, ChatColor.GREEN + "Back");
    public ItemStack last = ItemUtil.create(Material.ARROW, ChatColor.GREEN + "Last Page");
    private final ItemStack loading = ItemUtil.create(Material.CYAN_TERRACOTTA, 1, ChatColor.AQUA + "Loading...", new ArrayList<>());
    private final ItemStack more = ItemUtil.create(Material.YELLOW_TERRACOTTA, 1, ChatColor.RED + "Too many!",
            Arrays.asList(ChatColor.RED + "We can only list up to", ChatColor.RED +
                            "45 Plots here. You're added", ChatColor.RED + "to more than 45 Plots. To",
                    ChatColor.RED + "get to a Plot you're added to", ChatColor.RED +
                            "that isn't listed here, you have", ChatColor.RED + "to send a /tpa request to the",
                    ChatColor.RED + "Plot Owner"));
    private final ItemStack member = ItemUtil.create(Material.REDSTONE_TORCH, ChatColor.GREEN + "Member",
            Arrays.asList(ChatColor.YELLOW + "This type of Member can only", ChatColor.YELLOW +
                    "build when you are online."));
    private final ItemStack trusted = ItemUtil.create(Material.TORCH, ChatColor.GOLD + "Trusted",
            Arrays.asList(ChatColor.YELLOW + "This type of Member can build", ChatColor.YELLOW +
                    "even if you're not online."));
    private final ItemStack note = ItemUtil.create(Material.NOTE_BLOCK, ChatColor.GREEN + "Notes",
            new ArrayList<>());
    private final ItemStack spark = ItemUtil.create(Material.FIREWORK_ROCKET, ChatColor.GREEN + "Firework Spark",
            new ArrayList<>());
    private final ItemStack mickey = ItemUtil.create(Material.APPLE, ChatColor.GREEN + "Mickey Head",
            new ArrayList<>());
    private final ItemStack enchant = ItemUtil.create(Material.ENCHANTING_TABLE, ChatColor.GREEN + "Enchantment",
            new ArrayList<>());
    private final ItemStack flame = ItemUtil.create(Material.FLINT_AND_STEEL, ChatColor.GREEN + "Flame",
            new ArrayList<>());
    private final ItemStack heart = ItemUtil.create(Material.DIAMOND, ChatColor.GREEN + "Hearts",
            new ArrayList<>());
    private final ItemStack portal = ItemUtil.create(Material.BLAZE_POWDER, ChatColor.GREEN + "Portal",
            new ArrayList<>());
    private final ItemStack lava = ItemUtil.create(Material.LAVA_BUCKET, ChatColor.GREEN + "Lava",
            new ArrayList<>());
    private final ItemStack witch = new ItemStack(Material.POTION);
    private final ItemStack none = ItemUtil.create(Material.WHITE_STAINED_GLASS, ChatColor.RED + "Clear Particle",
            new ArrayList<>());
    private final PlotSquared plotSquared;
    private final List<UUID> denyTask = new ArrayList<>();
    @Getter @Setter private boolean chatMuted = false;

    public MenuUtil() {
        plotSquared = PlotSquared.get();
        BannerMeta bm = (BannerMeta) bannerCreator.getItemMeta();
        bm.addPattern(new Pattern(DyeColor.RED, PatternType.TRIANGLE_BOTTOM));
        bm.addPattern(new Pattern(DyeColor.RED, PatternType.TRIANGLE_TOP));
        bm.addPattern(new Pattern(DyeColor.ORANGE, PatternType.CIRCLE_MIDDLE));
        bm.setDisplayName(ChatColor.GREEN + "Banner Creator");
        bannerCreator.setItemMeta(bm);
        PotionMeta pm = (PotionMeta) witch.getItemMeta();
        pm.setBasePotionData(new PotionData(PotionType.POISON));
        witch.setItemMeta(pm);
        String hash = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv" +
                "Y2RhZGYxNzQ0NDMzZTFjNzlkMWQ1OWQyNzc3ZDkzOWRlMTU5YTI0Y2Y1N2U4YTYxYzgyYmM0ZmUzNzc3NTUzYyJ9fX0=";
        headShop = network.palace.core.utils.HeadUtil.getPlayerHead(hash, ChatColor.GREEN + "Headshop");
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

    public void openMenu(CPlayer player) {
        Creative plugin = Creative.getInstance();
        PlayerData data = plugin.getPlayerData(player.getUniqueId());
        if (data == null) return;
        Plot plot = PlotPlayer.wrap(player.getBukkitPlayer()).getCurrentPlot();
        boolean owns = false;
        if (plot != null) {
            for (Plot pl : PlotPlayer.wrap(player.getBukkitPlayer()).getPlots("plotworld")) {
                if (plot.getId().equals(pl.getId())) {
                    owns = true;
                    break;
                }
            }
        }

        List<MenuButton> buttons = new ArrayList<>();
        if (owns) {
            buttons.add(new MenuButton(4, plotTime, ImmutableMap.of(ClickType.LEFT, this::openPlotSettings)));
        }

        buttons.add(new MenuButton(10, bannerCreator, ImmutableMap.of(ClickType.LEFT, p -> plugin.getBannerUtil().openMenu(p, BannerInventoryType.SELECT_BASE))));
        buttons.add(new MenuButton(11, myPlots, ImmutableMap.of(ClickType.LEFT, this::openMyPlots)));
        buttons.add(new MenuButton(12, shop, ImmutableMap.of(ClickType.LEFT, this::openShop)));
        buttons.add(new MenuButton(13, spawn, ImmutableMap.of(ClickType.LEFT, p -> {
            p.performCommand("spawn");
            p.closeInventory();
        })));
        buttons.add(new MenuButton(14, particles, ImmutableMap.of(ClickType.LEFT, p -> p.performCommand("pt"))));
        buttons.add(new MenuButton(15, buildingPlots, ImmutableMap.of(ClickType.LEFT, this::openBuildingPlots)));
        buttons.add(new MenuButton(16, headShop, ImmutableMap.of(ClickType.LEFT, this::openHeadShop)));
        if (data.hasShowCreator()) {
            buttons.add(new MenuButton(22, showCreator, ImmutableMap.of(ClickType.LEFT, p -> plugin.getShowManager().selectShow(p))));
        }

        new Menu(27, ChatColor.BLUE + "Creative Menu", player, buttons).open();
    }

    public void openBuildingPlots(CPlayer player) {
        List<MenuButton> buttons = new ArrayList<>();
        buttons.add(new MenuButton(22, loading));
        Menu menu = new Menu(54, ChatColor.BLUE + "Building Plots", player, buttons);
        menu.open();
        Bukkit.getScheduler().runTaskAsynchronously(Creative.getInstance(), () -> {
            final List<Plot> plotList = new ArrayList<>(plotSquared.getPlots("plotworld"));
            Bukkit.getScheduler().runTask(Creative.getInstance(), () -> {
                int i = 0;
                for (Plot plot : plotList) {
                    if (i >= 45) {
                        menu.setButton(new MenuButton(45, more));
                        menu.setButton(new MenuButton(46, more));
                        menu.setButton(new MenuButton(47, more));
                        menu.setButton(new MenuButton(48, more));
                        menu.setButton(new MenuButton(49, back, ImmutableMap.of(ClickType.LEFT, this::openMenu)));
                        menu.setButton(new MenuButton(50, more));
                        menu.setButton(new MenuButton(51, more));
                        menu.setButton(new MenuButton(52, more));
                        menu.setButton(new MenuButton(53, more));
                        break;
                    }

                    Map<ClickType, Consumer<CPlayer>> actions = ImmutableMap.of(ClickType.LEFT, p -> {
                        if (plot == null) {
                            player.sendMessage(ChatColor.RED + "There was a problem performing this action! (Error Code 110)");
                            player.closeInventory();
                            return;
                        }

                        Location loc = getHome(plot);
                        player.teleport(loc);
                        player.sendMessage(ChatColor.GREEN + "Teleported to " + getOwner(plot) + "'s Plot");
                    });
                    if (plot.getMembers().contains(player.getUniqueId())) {
                        menu.setButton(new MenuButton(i++, ItemUtil.create(Material.GRASS_BLOCK, ChatColor.GREEN + getOwner(plot) + "'s Plot " +
                                plot.getId().toString(), Collections.singletonList(ChatColor.GREEN + "Rank: " +
                                ChatColor.YELLOW + "Member")), actions));
                    }

                    if (plot.getTrusted().contains(player.getUniqueId())) {
                        menu.setButton(new MenuButton(i++, ItemUtil.create(Material.GRASS_BLOCK, ChatColor.GREEN + getOwner(plot) + "'s Plot " +
                                plot.getId().toString(), Collections.singletonList(ChatColor.GREEN + "Rank: " +
                                ChatColor.GOLD + "" + ChatColor.ITALIC + "Trusted")), actions));
                    }
                }

                menu.getButton(22).filter(b -> b.getItemStack().getType() == Material.CYAN_TERRACOTTA).ifPresent(b -> menu.removeButton(22));
                menu.setButton(new MenuButton(49, back, ImmutableMap.of(ClickType.LEFT, this::openMenu)));
            });
        });
    }

    public void openHeadShop(CPlayer player) {
        List<MenuButton> buttons = new ArrayList<>();
        HashMap<String, List<ItemStack>> map = Creative.getInstance().getHeadUtil().getCategories();
        List<String> categories = new ArrayList<>(map.keySet());
        int size = categories.size();
        int invSize = size <= 7 ? 27 : (size <= 14 ? 36 : (size <= 21 ? 45 : 54));
        int place = 10;
        for (String s : categories) {
            if (place % 9 == 8) {
                place += 2;
            }
            if (place >= invSize) {
                break;
            }
            ItemStack item = map.get(s).get(0).clone();
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + s);
            item.setItemMeta(meta);
            buttons.add(new MenuButton(place++, item, ImmutableMap.of(ClickType.LEFT, p -> Creative.getInstance().getHeadUtil().openCategory(p, s, 1))));
        }

        buttons.add(new MenuButton(invSize - 5, back, ImmutableMap.of(ClickType.LEFT, this::openMenu)));
        new Menu(invSize, ChatColor.BLUE + "Heads", player, buttons).open();
    }

    public void openMyPlots(CPlayer player) {
        List<MenuButton> buttons = new ArrayList<>();
        List<Plot> plots = new ArrayList<>(PlotPlayer.wrap(player.getBukkitPlayer()).getPlots("plotworld"));
        if (plots.isEmpty()) {
            ItemStack empty = ItemUtil.create(Material.RED_WOOL, 1, ChatColor.RED +
                    "You don't have any plots!", Arrays.asList(ChatColor.GREEN + "Click here to get",
                    ChatColor.GREEN + "your own plot!"));
            buttons.add(new MenuButton(13, empty));
        } else {
            for (int i = 0; i < plots.size(); i++) {
                if (i >= 7) break;
                Plot plot = plots.get(i);
                ItemStack stack = ItemUtil.create(Material.GRASS_BLOCK, ChatColor.GREEN + "Plot ID: " +
                        plot.getId().toString(), Collections.singletonList(ChatColor.GOLD + "Click to Manage this Plot!"));
                buttons.add(new MenuButton(i + 10, stack, ImmutableMap.of(ClickType.LEFT, p -> openManagePlot(p, plot))));
            }
        }

        buttons.add(new MenuButton(22, back, ImmutableMap.of(ClickType.LEFT, this::openMenu)));
        new Menu(27, ChatColor.BLUE + "My Plots", player, buttons).open();
    }

    public void openPlotSettings(CPlayer player) {
        Plot plot = PlotPlayer.wrap(player.getBukkitPlayer()).getCurrentPlot();
        Set<PlotFlag<?, ?>> flags = plot.getFlags();
        long time = 3000;
        boolean flightEnabled = plot.getFlag(FlyFlag.class) == null || plot.getFlag(FlyFlag.class) == FlyFlag.FlyStatus.ENABLED;
        PlotWeather weather = PlotWeather.CLEAR;
        for (PlotFlag<?, ?> flag : flags) {
            if (flag.getName().equalsIgnoreCase("time")) {
                time = (long) flag.getValue();
            } else if (flag.getName().equalsIgnoreCase("weather")) {
                weather = (PlotWeather) flag.getValue();
            }
        }
        List<String> current = Collections.singletonList(ChatColor.YELLOW + "Currently Selected!");
        List<String> not = Collections.singletonList(ChatColor.GRAY + "Click to Select!");
        List<MenuButton> buttons = new ArrayList<>();
        buttons.add(new MenuButton(2, ItemUtil.create(Material.GRASS_BLOCK, ChatColor.GREEN + "Set the floor of your plot"), ImmutableMap.of(ClickType.LEFT, p -> Creative.getInstance().getPlotFloorUtil().open(p, 1))));
        buttons.add(new MenuButton(3, ItemUtil.create(Material.SUNFLOWER, ChatColor.GREEN + "Clear", weather.equals(PlotWeather.CLEAR) ? current : not), getWeatherAction(plot, PlotWeather.CLEAR)));
        buttons.add(new MenuButton(4, ItemUtil.create(Material.DEAD_BUSH, 1, ChatColor.DARK_GREEN + "Change Biome", new ArrayList<>()), ImmutableMap.of(ClickType.LEFT, p -> openChangeBiome(p, plot))));
        buttons.add(new MenuButton(5, ItemUtil.create(Material.WATER_BUCKET, ChatColor.GREEN + "Rain", weather.equals(PlotWeather.RAIN) ? current : not), getWeatherAction(plot, PlotWeather.RAIN)));
        buttons.add(new MenuButton(6, ItemUtil.create(Material.ELYTRA, ChatColor.GREEN + "Toggle Flight", flightEnabled ? Collections.singletonList(ChatColor.YELLOW + "Visitors can fly") : Collections.singletonList(ChatColor.GRAY + "Visitors cannot fly")), ImmutableMap.of(ClickType.LEFT, p -> {
            if (plot == null) return;
            FlyFlag.FlyStatus flight = plot.getFlag(FlyFlag.class);
            FlyFlag newValue;
            switch (flight) {
                case ENABLED:
                    newValue = FlyFlag.FLIGHT_FLAG_DISABLED;
                    break;
                case DISABLED:
                case DEFAULT:
                default:
                    newValue = FlyFlag.FLIGHT_FLAG_ENABLED;
            }
            plot.setFlag(newValue);
            plot.getPlayersInPlot().stream().map(ply -> Bukkit.getPlayer(ply.getUUID())).filter(Objects::nonNull).filter(ply -> !plot.getOwners().contains(ply.getUniqueId()) && !isStaff(player)).forEach(ply -> ply.setAllowFlight(newValue == FlyFlag.FLIGHT_FLAG_ENABLED));
            if (newValue == FlyFlag.FLIGHT_FLAG_DISABLED) {
                p.sendMessage(ChatColor.GREEN + "You have disabled flight for visitors of your plot.");
            } else {
                p.sendMessage(ChatColor.GREEN + "You have enabled flight for visitors of your plot.");
            }

            p.closeInventory();
        })));
        buttons.add(new MenuButton(8, ItemUtil.create(Material.BARRIER, ChatColor.RED + "Delete Plot", Arrays.asList(ChatColor.GRAY + "" + ChatColor.BOLD + "This is not reversible!", "", ChatColor.GRAY + "You will be asked to confirm", ChatColor.GRAY + "before your plot is cleared.")), ImmutableMap.of(ClickType.LEFT, pl -> {
            if (plot == null) return;
            new Menu(27, ChatColor.RED + "Clear Plot " + plot.getId().toString() + "?", pl, Arrays.asList(
                    new MenuButton(12, ItemUtil.create(Material.GRAY_TERRACOTTA, ChatColor.GRAY + "Cancel - Do Not Clear Plot"), ImmutableMap.of(ClickType.LEFT, CPlayer::closeInventory)),
                    new MenuButton(14, ItemUtil.create(Material.YELLOW_TERRACOTTA, ChatColor.YELLOW + "Confirm - Clear Plot", Collections.singletonList(ChatColor.RED + "" + ChatColor.BOLD + "This is " + ChatColor.ITALIC + "not" + ChatColor.RED + "" + ChatColor.BOLD + " reversible!")), ImmutableMap.of(ClickType.LEFT, p -> {
                        p.closeInventory();
                        File plotClearsFile = new File("plugins/Creative/plot_clear_delays.yml");
                        if (!plotClearsFile.exists()) {
                            try {
                                plotClearsFile.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                                p.sendMessage(ChatColor.RED + "An error occurred while processing that task!");
                                return;
                            }
                        }
                        YamlConfiguration config = YamlConfiguration.loadConfiguration(plotClearsFile);
                        if (config.contains(p.getUniqueId().toString())) {
                            long lastClear = config.getLong(p.getUniqueId().toString());
                            if (System.currentTimeMillis() - lastClear < (1000 * 60 * 60 * 24)) {
                                int hours = (int) ((System.currentTimeMillis() - lastClear) / (1000 * 60 * 60));
                                int minutes = ((int) ((System.currentTimeMillis() - lastClear) / (1000 * 60))) - (hours * 60);
                                p.sendMessage(ChatColor.RED + "You must wait 24 hours between plot clears.");
                                p.sendMessage(ChatColor.RED + "You can clear a plot again in " + ChatColor.YELLOW + hours + " hours and " + minutes + " minutes.");
                                return;
                            }
                        }
                        if (plot == null) return;
                        if (!plot.getOwners().contains(p.getUniqueId())) {
                            p.sendMessage(ChatColor.RED + "Only the plot owner can clear the plot!");
                            return;
                        }
                        if (plot.getRunning() > 0) {
                            p.sendMessage(ChatColor.RED + "Your plot is currently executing a task, try again in a few minutes.");
                            return;
                        }
                        config.set(p.getUniqueId().toString(), System.currentTimeMillis());
                        try {
                            config.save(plotClearsFile);
                            p.sendMessage(ChatColor.YELLOW + "The plot clear process will start shortly...");
                            plot.clear(true, false, () -> {
                                plot.removeRunning();
                                p.sendMessage(ChatColor.YELLOW + "Your plot has finished being cleared.");
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                            p.sendMessage(ChatColor.RED + "An error occurred while processing that task!");
                        }
                    }))
            )).open();
        })));

        if (player.getRank() != Rank.SETTLER) {
            buttons.add(new MenuButton(13, ItemUtil.create(Material.MUSIC_DISC_CAT, ChatColor.GREEN + "Set park loop music."), ImmutableMap.of(ClickType.LEFT, p -> Creative.getInstance().getParkLoopUtil().open(p, 1))));
        }

        buttons.add(new MenuButton(9, ItemUtil.create(Material.CLOCK, ChatColor.GREEN + "6AM", time == 0 ? current : not), getTimeAction(plot, 0)));
        buttons.add(new MenuButton(10, ItemUtil.create(Material.CLOCK, ChatColor.GREEN + "9AM", time == 3000 ? current : not), getTimeAction(plot, 3000)));
        buttons.add(new MenuButton(11, ItemUtil.create(Material.CLOCK, ChatColor.GREEN + "12PM", time == 6000 ? current : not), getTimeAction(plot, 6000)));
        buttons.add(new MenuButton(12, ItemUtil.create(Material.CLOCK, ChatColor.GREEN + "3PM", time == 9000 ? current : not), getTimeAction(plot, 9000)));
        buttons.add(new MenuButton(14, ItemUtil.create(Material.CLOCK, ChatColor.GREEN + "6PM", time == 12000 ? current : not), getTimeAction(plot, 12000)));
        buttons.add(new MenuButton(15, ItemUtil.create(Material.CLOCK, ChatColor.GREEN + "9PM", time == 15000 ? current : not), getTimeAction(plot, 15000)));
        buttons.add(new MenuButton(16, ItemUtil.create(Material.CLOCK, ChatColor.GREEN + "12AM", time == 18000 ? current : not), getTimeAction(plot, 18000)));
        buttons.add(new MenuButton(17, ItemUtil.create(Material.CLOCK, ChatColor.GREEN + "3AM", time == 21000 ? current : not), getTimeAction(plot, 21000)));
        buttons.add(new MenuButton(22, back, ImmutableMap.of(ClickType.LEFT, this::openMenu)));
        new Menu(27, ChatColor.BLUE + "Plot Settings", player, buttons).open();
    }

    private Map<ClickType, Consumer<CPlayer>> getTimeAction(Plot plot, long time) {
        return ImmutableMap.of(ClickType.LEFT, p -> {
            if (plot.setFlag(TimeFlag.class, String.valueOf(time))) {
                p.sendMessage(ChatColor.GREEN + "Set Plot Time to " + time + "!");
                openMenu(p);
            } else {
                p.sendMessage(ChatColor.RED + "Error setting Plot Time! Please report this to a Cast Member.");
            }
        });
    }

    public void openChangeBiome(CPlayer player, Plot plot) {
        List<MenuButton> buttons = new ArrayList<>();
        plot.getBiome(biomeType -> {
            String biome = biomeType.toString();
            List<String> empty = new ArrayList<>();
            List<String> selected = Collections.singletonList(ChatColor.YELLOW + "Currently Selected");
            buttons.add(new MenuButton(10, ItemUtil.create(Material.DEAD_BUSH, 1, ChatColor.GREEN +
                    "Plains", biome.equalsIgnoreCase("plains") ? selected : empty), getBiomeAction("plains", plot)));
            buttons.add(new MenuButton(11, ItemUtil.create(Material.DEAD_BUSH, ChatColor.YELLOW + "Desert",
                    biome.equalsIgnoreCase("desert") ? selected : empty), getBiomeAction("desert", plot)));
            buttons.add(new MenuButton(12, ItemUtil.create(Material.SPRUCE_SAPLING, 1, ChatColor.DARK_GREEN +
                    "Forest", biome.equalsIgnoreCase("forest") ? selected : empty), getBiomeAction("forest", plot)));
            buttons.add(new MenuButton(13, ItemUtil.create(Material.VINE, ChatColor.DARK_GREEN + "Swampland",
                    biome.equalsIgnoreCase("swampland") ? selected : empty), getBiomeAction("swampland", plot)));
            buttons.add(new MenuButton(14, ItemUtil.create(Material.JUNGLE_SAPLING, 1, ChatColor.DARK_GREEN +
                    "Jungle", biome.equalsIgnoreCase("jungle") ? selected : empty), getBiomeAction("jungle", plot)));
            buttons.add(new MenuButton(15, ItemUtil.create(Material.ORANGE_TERRACOTTA, 1, ChatColor.GOLD +
                    "Mesa", biome.equalsIgnoreCase("mesa") ? selected : empty), getBiomeAction("mesa", plot)));
            buttons.add(new MenuButton(16, ItemUtil.create(Material.PACKED_ICE, ChatColor.AQUA + "Ice Plains (Snow)",
                    biome.equalsIgnoreCase("ice_flats") ? selected : empty), getBiomeAction("ice_flats", plot)));
            buttons.add(new MenuButton(22, back, ImmutableMap.of(ClickType.LEFT, MenuUtil.this::openPlotSettings)));
            new Menu(27, ChatColor.BLUE + "Change Biome", player, buttons).open();
        });
    }

    private Map<ClickType, Consumer<CPlayer>> getBiomeAction(String newBiome, Plot plot) {
        return ImmutableMap.of(ClickType.LEFT, p -> plot.getBiome(biomeType -> {
            String biome = biomeType.toString();
            if (biome.equalsIgnoreCase(newBiome)) {
                p.sendMessage(ChatColor.RED + "Your plot is already set to this biome!");
                return;
            }
            if (plot.getRunning() > 0) {
                p.sendMessage(ChatColor.RED + "Your plot is currently executing a task, try again in a few minutes.");
                return;
            }
            p.sendMessage(ChatColor.GREEN + "Changing your plot's biome...");
            p.closeInventory();
            plot.addRunning();
            plot.setBiome(BiomeType.REGISTRY.get(newBiome), () -> {
                plot.removeRunning();
                p.sendMessage(ChatColor.GREEN + "Your plot's biome was set to " + ChatColor.YELLOW + newBiome.toLowerCase());
            });
        }));
    }

    private Map<ClickType, Consumer<CPlayer>> getWeatherAction(Plot plot, PlotWeather plotWeather) {
        return ImmutableMap.of(ClickType.LEFT, p -> {
            if (plot.setFlag(WeatherFlag.class, String.valueOf(WeatherFlag.PLOT_WEATHER_FLAG_CLEAR.merge(plotWeather)))) {
                p.sendMessage(ChatColor.GREEN + "Set Plot Weather to " + StringUtils.capitalize(plotWeather.toString().toLowerCase()) + "!");
                openMenu(p);
            } else {
                p.sendMessage(ChatColor.RED + "Error setting Plot Weather! Please report this to a Cast Member.");
            }
        });
    }

    public void openShop(CPlayer player) {
        List<MenuButton> buttons = new ArrayList<>();
        PlayerData data = Creative.getInstance().getPlayerData(player.getUniqueId());
        int limit = data.getRPLimit();
        int balance = Core.getMongoHandler().getCurrency(player.getUniqueId(), CurrencyType.BALANCE);
        if (limit >= 10) {
            buttons.add(new MenuButton(1, ItemUtil.create(Material.PLAYER_HEAD, 1, ChatColor.GREEN +
                    "Role Play Expansion (10 Player)", Collections.singletonList(ChatColor.GREEN + "You own this!"))));
        } else {
            buttons.add(new MenuButton(1, ItemUtil.create(Material.PLAYER_HEAD, 1, ChatColor.GREEN +
                    "Role Play Expansion (10 Player)", Arrays.asList(ChatColor.YELLOW + "Price: " +
                    ChatColor.GREEN + "$250", ChatColor.RED + "This can't be undone!")), ImmutableMap.of(ClickType.LEFT, p -> {
                if (balance < 250) {
                    p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 25, 1);
                    p.sendMessage(ChatColor.RED + "You can't afford this!");
                    return;
                }

                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5, 2);
                p.closeInventory();
                purchaseParticle(p);

            })));
        }

        if (limit >= 15) {
            buttons.add(new MenuButton(2, ItemUtil.create(Material.PLAYER_HEAD, 1, ChatColor.GREEN +
                    "Role Play Expansion (15 Player)", Collections.singletonList(ChatColor.GREEN + "You own this!"))));
        } else {
            buttons.add(new MenuButton(2, ItemUtil.create(Material.PLAYER_HEAD, 1, ChatColor.GREEN +
                    "Role Play Expansion (15 Player)", Arrays.asList(ChatColor.YELLOW + "Price: " +
                    ChatColor.GREEN + "$300", ChatColor.RED + "This can't be undone!")), ImmutableMap.of(ClickType.LEFT, p -> {
                if (limit < 10) {
                    p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 25, 1);
                    p.sendMessage(ChatColor.RED + "You must purchase the previous tier first!!");
                    return;
                }

                if (balance < 300) {
                    p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 25, 1);
                    p.sendMessage(ChatColor.RED + "You can't afford this!");
                    return;
                }

                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5f, 2f);
                p.closeInventory();
                purchaseParticle(p);
                Core.getMongoHandler().changeAmount(p.getUniqueId(), -300, "role play expansion (15 player)", CurrencyType.BALANCE, false);
                data.setRPLimit(15);
                setValue(p.getUniqueId(), "rplimit", 15);
            })));
        }

        if (limit >= 20) {
            buttons.add(new MenuButton(3, ItemUtil.create(Material.PLAYER_HEAD, 1, ChatColor.GREEN +
                    "Role Play Expansion (20 Player)", Collections.singletonList(ChatColor.GREEN + "You own this!"))));
        } else {
            buttons.add(new MenuButton(3, ItemUtil.create(Material.PLAYER_HEAD, 1, ChatColor.GREEN +
                    "Role Play Expansion (20 Player)", Arrays.asList(ChatColor.YELLOW + "Price: " +
                    ChatColor.GREEN + "$350", ChatColor.RED + "This can't be undone!")), ImmutableMap.of(ClickType.LEFT, p -> {
                if (limit < 15) {
                    p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 25, 1);
                    p.sendMessage(ChatColor.RED + "You must purchase the previous tier first!!");
                    return;
                }

                if (balance < 350) {
                    p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 25, 1);
                    p.sendMessage(ChatColor.RED + "You can't afford this!");
                    return;
                }

                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5f, 2f);
                p.closeInventory();
                purchaseParticle(p);
                Core.getMongoHandler().changeAmount(p.getUniqueId(), -350,
                        "role play expansion (20 player)", CurrencyType.BALANCE, false);
                data.setRPLimit(20);
                setValue(p.getUniqueId(), "rplimit", 20);
            })));
        }

        if (data.hasRPTag()) {
            buttons.add(new MenuButton(11, ItemUtil.create(Material.OAK_SIGN, ChatColor.GREEN + "Role Play Tag",
                    Collections.singletonList(ChatColor.GREEN + "You own this!"))));
        } else {
            buttons.add(new MenuButton(11, ItemUtil.create(Material.OAK_SIGN, ChatColor.GREEN + "Role Play Tag",
                    Arrays.asList(ChatColor.YELLOW + "Price: " + ChatColor.GREEN + "✪ 100", ChatColor.RED +
                            "This can't be undone!")), ImmutableMap.of(ClickType.LEFT, p -> {
                int tokens = Core.getMongoHandler().getCurrency(p.getUniqueId(), CurrencyType.TOKENS);
                if (tokens < 100) {
                    p.playSound(p.getLocation(), Sound.ENTITY_ITEM_BREAK, 25, 1);
                    p.sendMessage(ChatColor.RED + "You can't afford this!");
                    return;
                }

                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5f, 2f);
                p.closeInventory();
                purchaseParticle(p);
                Core.getMongoHandler().changeAmount(p.getUniqueId(), -100,
                        "role play tag", CurrencyType.TOKENS, false);
                data.setHasRPTag(true);
                setValue(p.getUniqueId(), "rptag", true);
            })));
        }

        if (PlotPlayer.wrap(player.getBukkitPlayer()).getPlots("plotworld").size() == 1) {
            buttons.add(new MenuButton(13, purchase, ImmutableMap.of(ClickType.LEFT, p -> {
                if (balance < 5000) {
                    p.sendMessage(ChatColor.RED + "You cannot afford a Second Plot! You need "
                            + ChatColor.GREEN + "$" + (5000 - balance) + "!");
                    p.closeInventory();
                    return;
                }

                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5f, 2f);
                p.closeInventory();
                purchaseParticle(p);
                Core.getMongoHandler().changeAmount(p.getUniqueId(), -5000,
                        "second plot", CurrencyType.BALANCE, false);
                givePlot(p, true);
            })));
        }

        if (data.hasShowCreator()) {
            buttons.add(new MenuButton(15, ItemUtil.create(Material.FIREWORK_ROCKET, ChatColor.GREEN + "Show Creator",
                    Collections.singletonList(ChatColor.GREEN + "You own this!"))));
        } else {
            buttons.add(new MenuButton(15, ItemUtil.create(Material.FIREWORK_ROCKET, ChatColor.GREEN + "Show Creator",
                    Arrays.asList(ChatColor.YELLOW + "Price: " + ChatColor.GREEN + "$500", ChatColor.RED +
                            "This can't be undone!")), ImmutableMap.of(ClickType.LEFT, p -> {
                if (balance < 500) {
                    p.sendMessage(ChatColor.RED + "You cannot afford the Show Creator! You need "
                            + ChatColor.GREEN + "$" + (500 - balance) + "!");
                    p.closeInventory();
                    return;
                }
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 5f, 2f);
                p.closeInventory();
                purchaseParticle(p);
                Core.getMongoHandler().changeAmount(p.getUniqueId(), -500,
                        "show creator", CurrencyType.BALANCE, false);
                data.setHasShowCreator(true);
                p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "\nHOW TO USE: " + ChatColor.GREEN +
                        "Type /show to use the Show Creator!\n ");
                setValue(p.getUniqueId(), "showcreator", true);
            })));
        }

        buttons.add(new MenuButton(22, back, ImmutableMap.of(ClickType.LEFT, this::openMenu)));
        new Menu(27, ChatColor.BLUE + "Creative Shop", player, buttons).open();
    }

    public void openParticle(CPlayer player) {
        List<MenuButton> buttons = new ArrayList<>();
        buttons.add(new MenuButton(4, none, ImmutableMap.of(ClickType.LEFT, p -> Creative.getInstance().getParticleManager().clearParticle(p))));
        buttons.add(new MenuButton(9, note, particleAction(note)));
        buttons.add(new MenuButton(10, spark, particleAction(spark)));
        buttons.add(new MenuButton(11, flame, particleAction(flame)));
        buttons.add(new MenuButton(12, enchant, particleAction(enchant)));
        buttons.add(new MenuButton(13, mickey, particleAction(mickey)));
        buttons.add(new MenuButton(14, heart, particleAction(heart)));
        buttons.add(new MenuButton(15, portal, particleAction(portal)));
        buttons.add(new MenuButton(16, lava, particleAction(lava)));
        buttons.add(new MenuButton(17, witch, particleAction(witch)));
        buttons.add(new MenuButton(22, back, ImmutableMap.of(ClickType.LEFT, this::openMenu)));
        new Menu(27, ChatColor.BLUE + "Particle Menu", player, buttons).open();
    }

    private Map<ClickType, Consumer<CPlayer>> particleAction(ItemStack itemStack) {
        String name = itemStack.getItemMeta().getDisplayName();
        return ImmutableMap.of(ClickType.LEFT, p -> Creative.getInstance().getParticleManager().setParticle(p, ChatColor.stripColor(name).toLowerCase(), name));
    }

    private String getOwner(Plot p) {
        List<UUID> list = new ArrayList<>(p.getOwners());
        return Bukkit.getOfflinePlayer(list.get(0)).getName();
    }

    private void setValue(UUID uuid, String name, Object o) {
        Core.runTaskAsynchronously(Creative.getInstance(), () -> Core.getMongoHandler().setCreativeValue(uuid, name, o));
    }

    public Location getHome(Plot plot) {
        com.plotsquared.core.location.Location home = plot.getHomeSynchronous();
        return new Location(Bukkit.getWorld(home.getWorld()), home.getX(), home.getY(), home.getZ(), home.getYaw(), home.getPitch());
    }

    private void openAddOrTrust(CPlayer tp, Plot plot) {
        List<MenuButton> buttons = new ArrayList<>();
        buttons.add(new MenuButton(11, member, ImmutableMap.of(ClickType.LEFT, p -> {
            p.closeInventory();
            p.getBukkitPlayer().sendTitle(ChatColor.GREEN + "Add a Member", ChatColor.GREEN + "Type the player's name in chat", 0, 0, 200);
            new TextInput(p, (ply, s) -> {
                String owner = getOwner(plot);
                if (s.equalsIgnoreCase(ply.getName()) && plot.getOwners().contains(ply.getUniqueId())) {
                    ply.sendMessage(ChatColor.RED + "You're already added to this Plot!");
                    return;
                }
                if (s.equals("*")) {
                    ply.sendMessage(ChatColor.RED + "You should never add " + ChatColor.ITALIC + "everyone " +
                            ChatColor.RED + "to your plot!");
                    return;
                }
                Player op = getPlayer(s);
                if (op == null) {
                    ply.sendMessage(ChatColor.RED + "No player was found by that name! (They have to be online)");
                    return;
                }
                if (plot.isDenied(op.getUniqueId())) {
                    ply.sendMessage(ChatColor.RED + "This player is Denied from this Plot! You must un-deny them first.");
                    return;
                }
                if ((plot.getMembers().size() + plot.getTrusted().size()) >= 18) {
                    ply.sendMessage(ChatColor.RED + "You cannot add more than 18 people to your Plot!");
                    return;
                }
                if (isAdded(plot, op)) {
                    ply.sendMessage(ChatColor.RED + "This player is already added to this Plot!");
                    return;
                }
                plot.addMember(op.getUniqueId());
                PlotSquared.get().getEventDispatcher().callMember(PlotPlayer.wrap(ply.getBukkitPlayer()), plot, op.getUniqueId(), true);
                ply.sendMessage(ChatColor.GREEN + "Successfully added " + op.getName() + " to Plot " + plot.getId().toString());
                op.sendMessage(ChatColor.GREEN + "You were added to " + ChatColor.YELLOW + owner + "'s Plot! " +
                        ChatColor.GREEN + "Use /menu to get to it.");
            });
        })));
        buttons.add(new MenuButton(15, trusted, ImmutableMap.of(ClickType.LEFT, p -> {
            p.closeInventory();
            p.getTitle().show(ChatColor.GREEN + "Add a Member", ChatColor.GREEN + "Type the player's name in chat", 0, 0, 200);
            new TextInput(p, (ply, s) -> {
                String owner = getOwner(plot);
                if (s.equalsIgnoreCase(ply.getName()) && plot.getOwners().contains(ply.getUniqueId())) {
                    ply.sendMessage(ChatColor.RED + "You're already added to this Plot!");
                    return;
                }
                if (s.equals("*")) {
                    ply.sendMessage(ChatColor.RED + "You should never add " + ChatColor.ITALIC + "everyone " +
                            ChatColor.RED + "to your plot!");
                    return;
                }
                Player op = getPlayer(s);
                if (op == null) {
                    ply.sendMessage(ChatColor.RED + "No player was found by that name! (They have to be online)");
                    return;
                }
                if (plot.isDenied(op.getUniqueId())) {
                    ply.sendMessage(ChatColor.RED + "This player is Denied from this Plot! You must un-deny them first.");
                    return;
                }
                if ((plot.getMembers().size() + plot.getTrusted().size()) >= 18) {
                    ply.sendMessage(ChatColor.RED + "You cannot add more than 18 people to your Plot!");
                    return;
                }
                if (isAdded(plot, op)) {
                    ply.sendMessage(ChatColor.RED + "This player is already added to this Plot!");
                    return;
                }
                plot.addTrusted(op.getUniqueId());
                PlotSquared.get().getEventDispatcher().callTrusted(PlotPlayer.wrap(ply.getBukkitPlayer()), plot, op.getUniqueId(), true);
                ply.sendMessage(ChatColor.GREEN + "Successfully trusted " + op.getName() + " to Plot " + plot.getId().toString());
                op.sendMessage(ChatColor.GREEN + "You were " + ChatColor.GOLD + ChatColor.ITALIC + "trusted " + ChatColor.GREEN
                        + "to " + ChatColor.YELLOW + owner + "'s Plot! " + ChatColor.GREEN + "Use /menu to get to it.");
            });
        })));
        buttons.add(new MenuButton(22, back, ImmutableMap.of(ClickType.LEFT, p -> openManagePlot(p, plot))));
        new Menu(27, ChatColor.BLUE + "Add Player to Plot " + plot.getId().toString(), tp, buttons).open();
    }

    public void openManagePlot(CPlayer player, Plot plot) {
        if (!plot.hasOwner()) {
            player.sendMessage(ChatColor.RED + "This plot is not owned right now!");
            return;
        }

        BukkitPlayer plotPlayer = (BukkitPlayer) PlotPlayer.wrap(player.getBukkitPlayer());
        if (plotPlayer == null) return;

        List<MenuButton> buttons = new ArrayList<>();
        buttons.add(new MenuButton(9, HeadUtil.getPlayerHead(player.getTextureValue(), ChatColor.GREEN + "Add a Player"), ImmutableMap.of(ClickType.LEFT, p -> openAddOrTrust(p, plot))));
        buttons.add(new MenuButton(11, deny, ImmutableMap.of(ClickType.LEFT, p -> {
            new TextInput(p, (ply, s) -> {
                if (s.equalsIgnoreCase(player.getName()) && plot.getOwners().contains(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "You cannot deny yourself, you'll never get on again!");
                    return;
                }
                if (s.equals("*")) {
                    player.sendMessage(ChatColor.RED + "You should never deny " + ChatColor.ITALIC + "everyone " +
                            ChatColor.RED + "from your plot!");
                    return;
                }
                Player tp = getPlayer(s);
                if (tp == null) {
                    player.sendMessage(ChatColor.RED + "No player was found by that name! (They have to be online)");
                    return;
                }
                if (plot.getDenied().size() >= 18) {
                    player.sendMessage(ChatColor.RED + "You cannot deny more than 18 people on your Plot!");
                    return;
                }
                if (plot.getDenied().contains(tp.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "This player is already denied on this Plot!");
                    return;
                }

                Plot tpPlot = plotSquared.getPlotAreaAbs(Creative.wrapLocation(tp.getLocation())).getPlot(Creative.wrapLocation(tp.getLocation()));
                if (tpPlot != null) {
                    if (tpPlot.getId().toString().equals(plot.getId().toString())) {
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
            });
            p.closeInventory();
            p.getTitle().show(ChatColor.RED + "Deny a Player", ChatColor.GREEN + "Type the player's name in chat", 0, 0, 200);
        })));
        buttons.add(new MenuButton(13, teleport, ImmutableMap.of(ClickType.LEFT, p -> {
            Location location = getHome(plot);
            p.teleport(location);
            p.sendMessage(ChatColor.GREEN + "Teleported to Plot " + plot.getId().toString());
            p.closeInventory();
        })));
        buttons.add(new MenuButton(15, members, ImmutableMap.of(ClickType.LEFT, p -> openAddedPlayers(p, plot))));
        buttons.add(new MenuButton(17, denied, ImmutableMap.of(ClickType.LEFT, p -> openDeniedPlayers(p, plot))));
        buttons.add(new MenuButton(22, back, ImmutableMap.of(ClickType.LEFT, this::openMyPlots)));
        new Menu(27, ChatColor.BLUE + "Manage Plot " + plot.getId().toString(), player, buttons).open();
    }

    public void givePlot(final CPlayer player, boolean spawn) {
        player.closeInventory();
        final long time = System.currentTimeMillis();
        player.sendMessage(ChatColor.GREEN + "Finding you a plot right now...");
        BukkitPlayer plr = (BukkitPlayer) PlotPlayer.wrap(player.getBukkitPlayer());
        String world;
        Set<String> worlds = plotSquared.worlds.getConfigurationSection("worlds").getKeys(false);
        if (worlds.size() == 1) {
            world = worlds.iterator().next();
        } else {
            world = plr.getLocation().getWorld();
            if (spawn) {
                world = "plotworld";
            }

            if (!plotSquared.hasPlotArea(world)) {
                player.sendMessage(ChatColor.RED + "You're not in a PlotWorld!");
                return;
            }
        }
        if (!world.equalsIgnoreCase("plotworld")) {
            world = "plotworld";
        }

        PlotArea plotarea = plotSquared.getPlotArea(world, world);
        plotarea.setMeta("lastPlot", new PlotId(0, 0));
        while (true) {
            PlotId start = getNextPlotId(getLastPlotId(plotarea), 1);
            PlotId end = new PlotId(start.getX(), start.getY());
            plotarea.setMeta("lastPlot", start);
            if (plotarea.canClaim(plr, start, end)) {
                for (int i = start.getX(); i <= end.getX(); i++) {
                    for (int j = start.getY(); j <= end.getY(); j++) {
                        Plot plot = plotarea.getPlotAbs(new PlotId(i, j));
                        boolean teleport = i == end.getX() && j == end.getY();
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
        int absX = Math.abs(id.getX());
        int absY = Math.abs(id.getY());
        if (absX > absY) {
            if (id.getX() > 0) {
                return new PlotId(id.getX(), id.getY() + 1);
            } else {
                return new PlotId(id.getX(), id.getY() - 1);
            }
        } else if (absY > absX) {
            if (id.getY() > 0) {
                return new PlotId(id.getX() - 1, id.getY());
            } else {
                return new PlotId(id.getX() + 1, id.getY());
            }
        } else {
            if (id.getX() == id.getY() && id.getX() > 0) {
                return new PlotId(id.getX(), id.getY() + step);
            }
            if (id.getX() == absX) {
                return new PlotId(id.getX(), id.getY() + 1);
            }
            if (id.getY() == absY) {
                return new PlotId(id.getX(), id.getY() - 1);
            }
            return new PlotId(id.getX() + 1, id.getY());
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
        if (TextInput.hasSession(player)) {
            return;
        }

        event.setCancelled(true);
        RolePlay rp = Creative.getInstance().getRolePlayUtil().getRolePlay(player.getUniqueId());
        if (rp != null) {
            rp.chat(player, event.getMessage());
            return;
        }
        CPlayer cplayer = Core.getPlayerManager().getPlayer(player);
        if (cplayer == null) {
            player.sendMessage(ChatColor.RED + "An error has occurred. Please try again later.");
            return;
        }

        Rank rank = cplayer.getRank();
        List<RankTag> tags = cplayer.getTags();
        if (isChatMuted() && rank.getRankId() < Rank.TRAINEE.getRankId()) {
            cplayer.sendMessage(ChatColor.RED + "Chat is muted right now! (You can still add/remove players and use Show Creator)");
            return;
        }
        PlayerData data = Creative.getInstance().getPlayerData(player.getUniqueId());
        String msg;
        if (rank.getRankId() >= Rank.TRAINEE.getRankId()) {
            msg = ChatColor.translateAlternateColorCodes('&', event.getMessage());
        } else {
            msg = event.getMessage();
        }
        ComponentBuilder builder = new ComponentBuilder("");
        BaseComponent[] messageToSend = builder.append(RankTag.formatChat(cplayer.getTags())).event(getPlayerHover(cplayer))
                .append(rank.getFormattedName() + " ")
                .append(cplayer.getName() + ": ").color(net.md_5.bungee.api.ChatColor.GRAY)
                .append(msg, ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.getByChar(rank.getChatColor().getChar())).create();
        RolePlayUtil rolePlayUtil = Creative.getInstance().getRolePlayUtil();
        IgnoreUtil ignoreUtil = Creative.getInstance().getIgnoreUtil();
        for (CPlayer tp : Core.getPlayerManager().getOnlinePlayers()) {
            if (rolePlayUtil.getRolePlay(tp.getUniqueId()) != null ||
                    (ignoreUtil.isIgnored(tp.getUniqueId(), player.getUniqueId()) &&
                            cplayer.getRank().getRankId() < Rank.TRAINEE.getRankId() &&
                            tp.getRank().getRankId() < Rank.TRAINEE.getRankId()))
                continue;
            tp.getBukkitPlayer().spigot().sendMessage(messageToSend);
        }
    }

    private HoverEvent getPlayerHover(CPlayer player) {
        ComponentBuilder builder = new ComponentBuilder(player.getRank().getFormattedName())
                .append(" " + player.getName() + "\n").color(net.md_5.bungee.api.ChatColor.GRAY);
        for (RankTag tag : player.getTags()) {
            builder.append(tag.getName() + "\n").color(net.md_5.bungee.api.ChatColor.getByChar(tag.getColor().getChar())).italic(true);
        }
        builder.append("Server: ", ComponentBuilder.FormatRetention.NONE).color(net.md_5.bungee.api.ChatColor.AQUA).append("Creative").color(net.md_5.bungee.api.ChatColor.GREEN);
        return new HoverEvent(HoverEvent.Action.SHOW_TEXT, builder.create());
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

    private void openAddedPlayers(CPlayer player, Plot plot) {
        List<MenuButton> buttons = new ArrayList<>();
        List<Entry<UUID, MemberState>> added = new ArrayList<>();
        for (UUID uuid : plot.getTrusted()) {
            added.add(new SimpleEntry<>(uuid, MemberState.TRUSTED));
        }

        for (UUID uuid : plot.getMembers()) {
            added.add(new SimpleEntry<>(uuid, MemberState.MEMBER));
        }

        for (int x = 0; x < 18; x++) {
            try {
                Entry<UUID, MemberState> entry = added.get(x);
                UUID uuid = entry.getKey();
                MemberState state = entry.getValue();
                CPlayer cPlayer = Core.getPlayerManager().getPlayer(uuid);
                ItemStack itemStack;
                if (cPlayer == null) {
                    itemStack = ItemUtil.create(Material.SKELETON_SKULL, ChatColor.GRAY + Bukkit.getOfflinePlayer(uuid).getName());
                } else {
                    itemStack = network.palace.core.utils.HeadUtil.getPlayerHead(cPlayer.getTextureValue(), cPlayer.getRank().getTagColor() + cPlayer.getName());
                }

                ItemMeta meta = itemStack.getItemMeta();
                switch (state) {
                    case MEMBER:
                        meta.setLore(Arrays.asList(ChatColor.GREEN + "Left-Click to change this Player from,",
                                ChatColor.YELLOW + "Member " + ChatColor.GREEN + "to " + ChatColor.GOLD + ChatColor.ITALIC + "Trusted",
                                ChatColor.RED + "Right-Click to Remove this Player!", ChatColor.BLACK + "" + uuid));
                        itemStack.setItemMeta(meta);
                        break;
                    case TRUSTED:
                        meta.setLore(Arrays.asList(ChatColor.GREEN + "Left-Click to change this Player from,",
                                ChatColor.YELLOW + "Trusted " + ChatColor.GREEN + "to " + ChatColor.GOLD + ChatColor.ITALIC + "Member",
                                ChatColor.RED + "Right-Click to Remove this Player!", ChatColor.BLACK + "" + uuid));
                        itemStack.setItemMeta(meta);
                        break;
                }

                String name = ChatColor.stripColor(meta.getDisplayName());
                buttons.add(new MenuButton(x, itemStack, ImmutableMap.of(ClickType.LEFT, p -> {
                    if (plot.getTrusted().contains(uuid)) {
                        if (!plot.removeTrusted(uuid)) {
                            if (plot.getDenied().contains(uuid)) {
                                plot.removeDenied(uuid);
                            }
                        }

                        plot.addMember(uuid);
                        PlotSquared.get().getEventDispatcher().callMember(PlotPlayer.wrap(player.getBukkitPlayer()), plot, uuid, true);
                        player.sendMessage(ChatColor.GREEN + name + " is now a " +
                                ChatColor.YELLOW + "Member " + ChatColor.GREEN + "on Plot " + plot.getId().toString());
                    } else if (plot.getMembers().contains(uuid)) {
                        if (!plot.removeMember(uuid)) {
                            if (plot.getDenied().contains(uuid)) {
                                plot.removeDenied(uuid);
                            }
                        }

                        plot.addTrusted(uuid);
                        PlotSquared.get().getEventDispatcher().callTrusted(PlotPlayer.wrap(player.getBukkitPlayer()), plot, uuid, true);
                        player.sendMessage(ChatColor.GREEN + name + " is now a " +
                                ChatColor.YELLOW + "Trusted " + ChatColor.GREEN + "on Plot " + plot.getId().toString());
                    }

                    openAddedPlayers(player, plot);
                }, ClickType.RIGHT, p -> {
                    if (plot.getTrusted().contains(uuid)) {
                        plot.removeTrusted(uuid);
                    } else if (plot.getMembers().contains(uuid)) {
                        plot.removeMember(uuid);
                    } else if (plot.getDenied().contains(uuid)) {
                        plot.removeDenied(uuid);
                    }

                    player.sendMessage(ChatColor.GREEN + name + " is no longer Added to Plot " +
                            plot.getId().toString());
                    openAddedPlayers(player, plot);
                })));
            } catch (Exception ignored) {

            }
        }

        buttons.add(new MenuButton(22, back, ImmutableMap.of(ClickType.LEFT, p -> {
            if (plot == null) {
                openMenu(p);
                return;
            }

            openManagePlot(p, plot);
        })));

        new Menu(27, ChatColor.BLUE + "Added Players " + plot.getId().toString(), player, buttons).open();
    }

    private void openDeniedPlayers(CPlayer player, Plot plot) {
        List<MenuButton> buttons = new ArrayList<>();
        List<UUID> denied = new ArrayList<>(plot.getDenied());
        for (int i = 0; i < 18; i++) {
            try {
                UUID uuid = denied.get(i);
                ItemStack item;
                CPlayer cplayer = Core.getPlayerManager().getPlayer(uuid);
                if (cplayer == null) {
                    item = ItemUtil.create(Material.SKELETON_SKULL, ChatColor.GRAY + Bukkit.getOfflinePlayer(uuid).getName(),
                            Collections.singletonList(ChatColor.RED + "Click to Un-Deny this Player!"));
                } else {
                    item = HeadUtil.getPlayerHead(cplayer.getTextureValue(), cplayer.getRank().getTagColor() + cplayer.getName());
                    ItemMeta meta = item.getItemMeta();
                    meta.setLore(Collections.singletonList(ChatColor.RED + "Click to Un-Deny this Player!"));
                    item.setItemMeta(meta);
                }
                buttons.add(new MenuButton(i, item, ImmutableMap.of(ClickType.LEFT, p -> {
                    if (plot == null) {
                        player.sendMessage(ChatColor.RED + "There was a problem performing this action! (Error Code 110)");
                        player.closeInventory();
                        return;
                    }

                    plot.removeDenied(Bukkit.getOfflinePlayer(ChatColor.stripColor(item.getItemMeta().getDisplayName())).getUniqueId());
                    player.sendMessage(ChatColor.GREEN + item.getItemMeta().getDisplayName() + " is no longer Denied on Plot " + plot.getId().toString());
                    openDeniedPlayers(player, plot);
                })));
            } catch (Exception ignored) {

            }
        }

        buttons.add(new MenuButton(22, back, ImmutableMap.of(ClickType.LEFT, p -> openManagePlot(p, plot))));
        new Menu(27, ChatColor.BLUE + "Denied Players " + plot.getId().toString(), player, buttons).open();
    }

    private void purchaseParticle(CPlayer player) {
        player.getParticles().send(player.getLocation(), Particle.FIREWORKS_SPARK,
                30, 0, 0, 0, 0.25f);
    }

    public static boolean isStaff(CPlayer player) {
        return player != null && player.getRank().getRankId() >= Rank.TRAINEE.getRankId();
    }
}
