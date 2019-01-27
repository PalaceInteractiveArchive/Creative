package network.palace.creative.show;

import com.google.common.collect.ImmutableMap;
import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import network.palace.audio.Audio;
import network.palace.audio.handlers.AudioArea;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import network.palace.creative.Creative;
import network.palace.creative.handlers.ShowColor;
import network.palace.creative.handlers.ShowFireworkData;
import network.palace.creative.inventory.Menu;
import network.palace.creative.inventory.MenuButton;
import network.palace.creative.show.actions.FireworkAction;
import network.palace.creative.show.actions.ParticleAction;
import network.palace.creative.show.actions.ShowAction;
import network.palace.creative.show.actions.TextAction;
import network.palace.creative.show.handlers.AudioTrack;
import network.palace.creative.show.handlers.PlotArea;
import network.palace.creative.show.ticker.TickEvent;
import network.palace.creative.show.ticker.Ticker;
import network.palace.creative.utils.TextInput;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;

/**
 * Created by Marc on 12/11/15
 */
public class ShowManager implements Listener {
    private final File showsDir = new File(Creative.getInstance().getDataFolder(), "shows");
    public HashMap<UUID, Show> shows = new HashMap<>();
    private TreeMap<String, AudioTrack> audioTracks = new TreeMap<>();

    public ShowManager() {
        Bukkit.getScheduler().runTaskTimer(Creative.getInstance(), new Ticker(), 1, 1);
        loadTracks();
    }

    public void loadTracks() {
        audioTracks.clear();
        File f = new File("plugins/Creative/tracks.yml");
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(f);
        if (config.getConfigurationSection("tracks") == null) {
            Core.logMessage("Creative", ChatColor.RED + "No audio tracks have been added!");
            return;
        }

        List<String> tracks = new ArrayList<>(config.getConfigurationSection("tracks").getKeys(false));
        Collections.sort(tracks);
        for (String s : tracks) {
            AudioTrack audioTrack = new AudioTrack(config.getString("tracks." + s + ".name"),
                    config.getString("tracks." + s + ".path"));
            audioTracks.put(s, audioTrack);
        }
    }

    @EventHandler
    public void onTick(TickEvent event) {
        for (Map.Entry<UUID, Show> entry : new HashSet<>(shows.entrySet())) {
            Show show = entry.getValue();
            if (show != null && show.getOwner() != null) {
                try {
                    if (show.update()) {
                        CPlayer player = Core.getPlayerManager().getPlayer(show.getOwner());
                        if (player != null) {
                            messagePlayer(player, "Your show " + ChatColor.AQUA + show.getNameColored() + ChatColor.GREEN +
                                    " has ended!");
                        }

                        stopAudio(show);
                        shows.remove(entry.getKey());
                        Creative.getInstance().getParkLoopUtil().enableRegion(show.getOwner());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void messagePlayer(CPlayer tp, String msg) {
        if (tp != null)
            tp.sendMessage(ChatColor.WHITE + "[" + ChatColor.BLUE + "Show" + ChatColor.WHITE + "] " + ChatColor.GREEN + msg);
    }

    public Show startShow(CPlayer player) {
        if (shows.containsKey(player.getUniqueId())) {
            messagePlayer(player, ChatColor.RED + "Your Show is already running!");
            return null;
        }

        PlotAPI api = new PlotAPI();
        Plot plot = api.getPlot(player.getBukkitPlayer());
        boolean owns = false;
        if (plot == null) {
            messagePlayer(player, ChatColor.RED + "You must start shows on your own Plot!");
            return null;
        }

        for (Plot pl : api.getPlayerPlots(Bukkit.getWorld("plotworld"), player.getBukkitPlayer())) {
            if (plot.getId().equals(pl.getId())) {
                owns = true;
                break;
            }
        }

        if (!owns) {
            messagePlayer(player, ChatColor.RED + "You must start shows on your own Plot!");
            return null;
        }

        List<MetadataValue> metadataValues = player.getMetadata("showname");
        if (metadataValues.isEmpty()) {
            messagePlayer(player, ChatColor.RED + "An error has occurred. Please contact staff.");
            return null;
        }

        String showName = ChatColor.stripColor(metadataValues.get(0).asString());
        messagePlayer(player, ChatColor.GREEN + "Loading show file...");
        File showFile = new File("plugins/Creative/shows/" + player.getUniqueId().toString() + "/" + showName + ".show");
        if (!showFile.exists()) {
            return null;
        }

        Show show = new Show(showFile, player, plot);
        if (show.getActions().isEmpty()) {
            return null;
        }

        if (!show.getAudioTrack().equals("none")) {
            PlotArea area;
            AudioArea temp = Audio.getInstance().getByName(player.getUniqueId().toString());
            if (temp != null) {
                area = (PlotArea) temp;
            } else {
                Audio.getInstance().removeArea(temp);
                area = new PlotArea(plot.getId(), player, show.getAudioTrack(), player.getWorld());
            }
            if (Audio.getInstance().getByName(area.getAreaName()) == null) Audio.getInstance().addArea(area);
            for (CPlayer p : Core.getPlayerManager().getOnlinePlayers()) {
                if (p == null || p.getBukkitPlayer() == null)
                    continue;
                Plot pl = api.getPlot(p.getBukkitPlayer());
                if (pl == null)
                    continue;
                if (pl.getId().equals(plot.getId())) {
                    area.triggerPlayer(p);
                }
            }
        }

        shows.put(player.getUniqueId(), show);
        player.removeMetadata("showname", Creative.getInstance());
        Creative.getInstance().getParkLoopUtil().disableRegion(plot);
        return show;
    }

    public boolean stopShow(UUID uuid) {
        Show show = shows.remove(uuid);
        if (show == null) return false;
        stopAudio(show);
        Creative.getInstance().getParkLoopUtil().enableRegion(show.getOwner());
        return true;
    }

    public void stopAllShows() {
        shows.values().forEach(this::stopAudio);
        shows.clear();
    }

    private void stopAudio(Show show) {
        Audio.getInstance().getAudioAreas().stream().filter(Objects::nonNull).filter(area -> area.getAreaName().equals(show.getOwner().toString())).forEach(area -> {
            area.removeAllPlayers(true);
            Audio.getInstance().removeArea(area);
        });
    }

    public void selectShow(Player player) {
        PlotAPI api = new PlotAPI();
        Plot plot = api.getPlot(player);
        if (plot == null || !plot.getOwners().contains(player.getUniqueId())) {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "You must edit shows on your own Plot!");
            return;
        }

        File userShowsDir = new File(showsDir, player.getUniqueId().toString());
        userShowsDir.mkdirs();
        File[] showFiles = userShowsDir.listFiles();
        List<MenuButton> buttons = new ArrayList<>();
        CPlayer cPlayer = Core.getPlayerManager().getPlayer(player);
        int maxShows = getMaxShowAmount(cPlayer);
        int showCount = 0;
        if (showFiles != null) {
            for (int x = 0; x < maxShows; x++) {
                try {
                    File file = showFiles[x];
                    Show show = new Show(showFiles[x], cPlayer, plot);
                    buttons.add(new MenuButton(x, ItemUtil.create(Material.FIREWORK, ChatColor.RESET + show.getNameColored(),
                            Arrays.asList(ChatColor.YELLOW + "Left-Click " + ChatColor.GREEN + "to Edit this Show!",
                                    ChatColor.YELLOW + "Right-Click " + ChatColor.RED + "to Remove this Show!")),
                            ImmutableMap.of(ClickType.LEFT, p -> editShow(p, 1, show), ClickType.RIGHT, p -> {
                                file.delete();
                                p.closeInventory();
                                p.sendMessage(ChatColor.GREEN + "Show successfully deleted.");
                            })));
                }
                catch (IndexOutOfBoundsException ignored) {

                }
            }
        }

        if (showCount <= maxShows) {
            buttons.add(new MenuButton(7, ItemUtil.create(Material.EMERALD_BLOCK, ChatColor.GREEN + "New Show"), ImmutableMap.of(ClickType.LEFT, p -> {
                p.closeInventory();
                p.sendTitle(ChatColor.GREEN + "Set Show Name", ChatColor.GREEN + "Type the name you want for your show.", 0, 0, 200);
                new TextInput(p, (ply, msg) -> {
                    try {
                        String name = ChatColor.stripColor(msg);
                        Pattern pattern = Pattern.compile("[^a-zA-Z0-9_ ]");
                        Matcher matcher = pattern.matcher(name);
                        if (matcher.find()) {
                            player.sendMessage(ChatColor.RED + "Show names can only contain letters, numbers, spaces and underscores (_).");
                            return;
                        }

                        File showFile = new File(userShowsDir, name + ".show");
                        if (!showFile.exists()) {
                            showFile.createNewFile();
                            return;
                        }

                        ply.sendMessage(ChatColor.RED + " A show named " + name + " already exists.");
                    }
                    catch (IOException e) {
                        ply.sendMessage(ChatColor.RED + "There was an error! Please try again.");
                    }
                });
            })));
        }

        buttons.add(new MenuButton(8, Creative.getInstance().getMenuUtil().back, ImmutableMap.of(ClickType.LEFT, Creative.getInstance().getMenuUtil()::openMenu)));
        new Menu(Bukkit.createInventory(player, 9, ChatColor.BLUE + "Select A Show To Edit"), player, buttons);
    }

    public int getMaxShowAmount(CPlayer player) {
        switch (player.getRank()) {
            case SETTLER:
                return 1;
            case DWELLER:
                return 2;
            case NOBLE:
                return 3;
            case MAJESTIC:
                return 4;
            default:
                return 5;
        }
    }

    public int getTotalShows(CPlayer player) {
        File[] files = new File("plugins/Creative/shows/" + player.getUniqueId().toString()).listFiles();
        return files == null ? 0 : files.length;
    }

    public void editShow(Player player, int page, Show show) {
        List<ShowAction> actions = show.getActions();
        actions.removeIf(action -> action.getItem() == null);
        List<MenuButton> buttons = new ArrayList<>();
        for (int x = 0; x < 45; x++) {
            try {
                int i = x + (page - 1) * 45;
                ShowAction action = actions.get(i);
                ItemStack itemStack = action.getItem();
                ItemMeta meta = itemStack.getItemMeta();
                List<String> lore = meta.getLore();
                lore.add(ChatColor.YELLOW + "Left-Click " + ChatColor.GREEN + "to Edit this Action!");
                lore.add(ChatColor.YELLOW + "Right-Click " + ChatColor.RED + "to Remove this Action!");
                meta.setLore(lore);
                itemStack.setItemMeta(meta);
                buttons.add(new MenuButton(x, itemStack, ImmutableMap.of(ClickType.LEFT, p -> editAction(p, show, action), ClickType.RIGHT, p -> {
                    show.actions.remove(i);
                    show.saveFile();
                    editShow(p, page, show);
                })));
            }
            catch (IndexOutOfBoundsException ignored) {

            }
        }

        buttons.add(new MenuButton(48, Creative.getInstance().getMenuUtil().last, ImmutableMap.of(ClickType.LEFT, p -> {
            if (page - 1 > 0) {
                editShow(p, page - 1, show);
            }
        })));
        buttons.add(new MenuButton(50, Creative.getInstance().getMenuUtil().next, ImmutableMap.of(ClickType.LEFT, p -> {
            if (page + 1 <= new Double(Math.ceil(actions.size() / 45D)).intValue()) {
                editShow(p, page + 1, show);
            }
        })));
        buttons.add(new MenuButton(53, ItemUtil.create(Material.STAINED_CLAY, 1, (byte) 5, ChatColor.GREEN + "Add Action",
                Arrays.asList(ChatColor.GREEN + "Click to add a new Action!")), ImmutableMap.of(ClickType.LEFT, p -> openAddAction(p, show))));
        buttons.add(new MenuButton(49, Creative.getInstance().getMenuUtil().back, ImmutableMap.of(ClickType.LEFT, p -> {
            cancelEdit(p, false);
            selectShow(p);
        })));
        buttons.add(new MenuButton(45, ItemUtil.create(Material.BARRIER, 1, (byte) 0, ChatColor.RED + "Delete All Actions", Collections.emptyList()),
                ImmutableMap.of(ClickType.LEFT, p -> {
                    show.actions.clear();
                    show.saveFile();
                    p.closeInventory();
                })));
        new Menu(Bukkit.createInventory(player, 54, ChatColor.BLUE + "Edit Show File"), player, buttons);
    }

    public void cancelEdit(Player player) {
        cancelEdit(player, true);
    }

    public void cancelEdit(Player player, boolean silent) {
        if (player == null) {
            return;
        }

        if (!silent) {
            player.sendMessage(ChatColor.RED + "Your Show edit session has ended!");
        }
    }

    public FireworkEffect.Type getType(String name) {
        switch (name.toLowerCase()) {
            case "ball":
                return FireworkEffect.Type.BALL;
            case "ball_large":
            case "large ball":
                return FireworkEffect.Type.BALL_LARGE;
            case "star":
                return FireworkEffect.Type.STAR;
            case "burst":
                return FireworkEffect.Type.BURST;
            case "creeper":
                return FireworkEffect.Type.CREEPER;
        }
        return FireworkEffect.Type.BALL;
    }

    private void selectTrack(Player player, int page, Show show) {
        List<MenuButton> buttons = new ArrayList<>();
        List<AudioTrack> audioTracks = new ArrayList<>(this.audioTracks.values());
        for (int x = 0; x < 27; x++) {
            try {
                AudioTrack track = audioTracks.get(x + (page - 1) * 27);
                buttons.add(new MenuButton(x, ItemUtil.create(track.getItem(), ChatColor.GREEN + track.getName()), ImmutableMap.of(ClickType.LEFT, p -> {
                    show.setAudioTrack(track.getAudioPath());
                    show.saveFile();
                    editShow(p, 1, show);
                })));
            }
            catch (IndexOutOfBoundsException ignored) {

            }
        }

        buttons.add(new MenuButton(27, Creative.getInstance().getMenuUtil().last, ImmutableMap.of(ClickType.LEFT, p -> {
            if (page - 1 > 0) {
                selectTrack(p, page - 1, show);
            }
        })));
        buttons.add(new MenuButton(31, Creative.getInstance().getMenuUtil().back, ImmutableMap.of(ClickType.LEFT, p -> editShow(p, 1, show))));
        buttons.add(new MenuButton(35, Creative.getInstance().getMenuUtil().next, ImmutableMap.of(ClickType.LEFT, p -> {
            if (page + 1 <= new Double(Math.ceil(audioTracks.size() / 27D)).intValue()) {
                selectTrack(p, page + 1, show);
            }
        })));
        new Menu(Bukkit.createInventory(player, 36, ChatColor.BLUE + "Select Track"), player, buttons);
    }

    private void editAction(Player player, Show show, ShowAction action) {
        ItemStack setTimeItem = ItemUtil.create(Material.WATCH, ChatColor.GREEN + "Set Time",
                Arrays.asList(ChatColor.YELLOW + "Time in seconds after start of", ChatColor.YELLOW +
                        "Show for an Action to execute."));
        Map<ClickType, Consumer<Player>> setTimeActions = ImmutableMap.of(ClickType.LEFT, p -> {
            p.closeInventory();
            p.sendTitle(ChatColor.GREEN + "Set a Time", ChatColor.GREEN + "Enter a number for the action to execute at", 0, 0, 200);
            new TextInput(p, (ply, msg) -> {
                Double time;
                try {
                    time = Double.parseDouble(msg);
                }
                catch (NumberFormatException e) {
                    ply.sendMessage(ChatColor.RED + msg + " is not a number! Please specify a number for the action to execute at.");
                    return;
                }

                if (time > 1200) {
                    ply.sendMessage(ChatColor.RED + "Shows cannot be longer than 20 Minutes!");
                    time = 1200D;
                }

                action.setTime(time);
                show.saveFile();
                cancelEdit(ply, true);
            });
        });
        List<MenuButton> buttons = new ArrayList<>();
        buttons.add(new MenuButton(22, Creative.getInstance().getMenuUtil().back, ImmutableMap.of(ClickType.LEFT, p -> editShow(p, 1, show))));
        if (action instanceof TextAction) {
            buttons.add(new MenuButton(11, setTimeItem, setTimeActions));
            buttons.add(new MenuButton(15, ItemUtil.create(Material.SIGN, ChatColor.GREEN + "Set Text",
                    Arrays.asList(ChatColor.YELLOW + "Supports Color Codes!")), ImmutableMap.of(ClickType.LEFT, p -> {
                        p.closeInventory();
                p.sendTitle(ChatColor.GREEN + "Set Text Message", ChatColor.GREEN +
                        "Type a message to be displayed (Color Codes work!)", 0, 0, 200);
                new TextInput(p, (ply, msg) -> {
                    ((TextAction) action).setText(msg);
                    show.saveFile();
                    cancelEdit(ply, true);
                    ply.sendMessage(ChatColor.GREEN + "Set Text Message to " + ChatColor.YELLOW +
                            ChatColor.translateAlternateColorCodes('&', msg) + ChatColor.YELLOW + "!");
                });
            })));
            new Menu(Bukkit.createInventory(player, 27, ChatColor.BLUE + "Edit Text Action"), player, buttons);
        } else if (action instanceof ParticleAction) {
            buttons.add(new MenuButton(11, setTimeItem, setTimeActions));
            buttons.add(new MenuButton(15, ItemUtil.create(Material.NETHER_STAR, ChatColor.GREEN + "Set Particle",
                    Arrays.asList(ChatColor.YELLOW + "Some Minecraft Particles are not allowed")), ImmutableMap.of(ClickType.LEFT, p -> setParticle(p, show, (ParticleAction) action))));
            new Menu(Bukkit.createInventory(player, 27, ChatColor.BLUE + "Edit Particle Action"), player, buttons);
        } else if (action instanceof FireworkAction) {
            FireworkAction a = (FireworkAction) action;
            buttons.add(new MenuButton(10, setTimeItem, setTimeActions));
            buttons.add(new MenuButton(11, ItemUtil.create(Material.FIREWORK_CHARGE, ChatColor.GREEN + "Select Type",
                    Arrays.asList(ChatColor.YELLOW + "Choose shape of the Firework!")), ImmutableMap.of(ClickType.LEFT, p -> selectType(p, show, (FireworkAction) action))));
            buttons.add(new MenuButton(12, ItemUtil.create(Material.WOOL, 1, (byte) 3, ChatColor.GREEN + "Select Colors",
                    Arrays.asList(ChatColor.YELLOW + "The first colors of the Firework")), ImmutableMap.of(ClickType.LEFT, p -> selectColors(p, show, (FireworkAction) action))));
            buttons.add(new MenuButton(13, ItemUtil.create(Material.WOOL, 1, (byte) 14, ChatColor.GREEN + "Select Fade Colors",
                    Arrays.asList(ChatColor.YELLOW + "The color the Firework fades to")), ImmutableMap.of(ClickType.LEFT, p -> selectFadeColors(p, show, (FireworkAction) action))));
            buttons.add(new MenuButton(14, ItemUtil.create(Material.FIREWORK, 1, ChatColor.GREEN + "Set Power",
                    Arrays.asList(ChatColor.YELLOW + "The power of the Firework")), ImmutableMap.of(ClickType.LEFT, p -> setPower(p, show, (FireworkAction) action))));
            buttons.add(new MenuButton(15, ItemUtil.create(Material.GLOWSTONE_DUST, 1, ChatColor.GREEN + "Flicker",
                    Arrays.asList(a.isFlicker() ? ChatColor.GREEN + "True" : ChatColor.RED + "False",
                            ChatColor.YELLOW + "Click to cycle options")), ImmutableMap.of(ClickType.LEFT, p -> {
                                FireworkAction fa = (FireworkAction) action;
                                fa.getShowData().setFlicker(!fa.isFlicker());
                                show.saveFile();
                                editAction(p, show, action);
            })));
            buttons.add(new MenuButton(16, ItemUtil.create(Material.FEATHER, 1, ChatColor.GREEN + "Trail",
                    Arrays.asList(a.isTrail() ? ChatColor.GREEN + "True" : ChatColor.RED + "False",
                            ChatColor.YELLOW + "Click to cycle options")), ImmutableMap.of(ClickType.LEFT, p -> {
                            FireworkAction fa = (FireworkAction) action;
                            fa.getShowData().setTrail(!fa.isTrail());
                            show.saveFile();
                            editAction(p, show, action);
            })));
            new Menu(Bukkit.createInventory(player, 27, ChatColor.BLUE + "Edit Firework Action"), player, buttons);
        }
    }

    private void setPower(Player player, Show show, FireworkAction action) {
        List<MenuButton> buttons = new ArrayList<>();
        Function<Integer, ImmutableMap<ClickType, Consumer<Player>>> powerAction = power -> ImmutableMap.of(ClickType.LEFT, p -> {
            action.setPower(power);
            show.saveFile();
            editAction(p, show, action);
        });
        buttons.add(new MenuButton(10, ItemUtil.create(Material.FIREWORK, 1, ChatColor.GREEN + "Power 0",
                Arrays.asList(ChatColor.GRAY + "Click to Select!")), powerAction.apply(0)));
        buttons.add(new MenuButton(12, ItemUtil.create(Material.FIREWORK, 1, ChatColor.GREEN + "Power 1",
                Arrays.asList(ChatColor.GRAY + "Click to Select!")), powerAction.apply(1)));
        buttons.add(new MenuButton(14, ItemUtil.create(Material.FIREWORK, 2, ChatColor.GREEN + "Power 2",
                Arrays.asList(ChatColor.GRAY + "Click to Select!")), powerAction.apply(2)));
        buttons.add(new MenuButton(16, ItemUtil.create(Material.FIREWORK, 3, ChatColor.GREEN + "Power 3",
                Arrays.asList(ChatColor.GRAY + "Click to Select!")), powerAction.apply(3)));
        buttons.add(new MenuButton(22, Creative.getInstance().getMenuUtil().back, ImmutableMap.of(ClickType.LEFT, p -> editAction(p, show, action))));
        new Menu(Bukkit.createInventory(player, 27, ChatColor.BLUE + "Set Power"), player, buttons);
    }

    private ItemStack addGlow(ItemStack itemStack, List<ShowColor> colors) {
        return colors.stream().map(ShowColor::name).map(String::toLowerCase)
                .filter(name -> name.equalsIgnoreCase(ChatColor.stripColor(itemStack.getItemMeta().getDisplayName())))
                .findFirst().map(name -> ItemUtil.addGlow(itemStack)).orElse(itemStack);
    }

    private void selectColors(Player player, Show show, FireworkAction action) {
        List<MenuButton> buttons = new ArrayList<>();
        Function<ShowColor, ImmutableMap<ClickType, Consumer<Player>>> colorAction = color -> ImmutableMap.of(ClickType.LEFT, p -> {
            if (action.getShowData().getColors().size() >= 4) {
                return;
            }

            action.getShowData().getColors().add(color);
            selectColors(p, show, action);
        });
        buttons.add(new MenuButton(0, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 14, ChatColor.DARK_RED + "Red",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getColors()), colorAction.apply(ShowColor.RED)));
        buttons.add(new MenuButton(1, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 1, ChatColor.GOLD + "Orange",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getColors()), colorAction.apply(ShowColor.ORANGE)));
        buttons.add(new MenuButton(2, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 4, ChatColor.YELLOW + "Yellow",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getColors()), colorAction.apply(ShowColor.YELLOW)));
        buttons.add(new MenuButton(3, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 5, ChatColor.GREEN + "Lime",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getColors()), colorAction.apply(ShowColor.LIME)));
        buttons.add(new MenuButton(4, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 13, ChatColor.DARK_GREEN + "Green",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getColors()), colorAction.apply(ShowColor.GREEN)));
        buttons.add(new MenuButton(5, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 3, ChatColor.AQUA + "Aqua",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getColors()), colorAction.apply(ShowColor.AQUA)));
        buttons.add(new MenuButton(6, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 9, ChatColor.DARK_AQUA + "Cyan",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getColors()), colorAction.apply(ShowColor.CYAN)));
        buttons.add(new MenuButton(7, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 11, ChatColor.BLUE + "Blue",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getColors()), colorAction.apply(ShowColor.BLUE)));
        buttons.add(new MenuButton(8, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 10, ChatColor.DARK_PURPLE + "Purple",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getColors()), colorAction.apply(ShowColor.PURPLE)));
        buttons.add(new MenuButton(10, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 2, ChatColor.LIGHT_PURPLE + "Magenta",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getColors()), colorAction.apply(ShowColor.MAGENTA)));
        buttons.add(new MenuButton(11, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 6, ChatColor.RED + "Pink",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getColors()), colorAction.apply(ShowColor.PINK)));
        buttons.add(new MenuButton(12, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 0, ChatColor.WHITE + "White",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getColors()), colorAction.apply(ShowColor.WHITE)));
        buttons.add(new MenuButton(13, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 8, ChatColor.GRAY + "Silver",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getColors()), colorAction.apply(ShowColor.SILVER)));
        buttons.add(new MenuButton(14, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 7, ChatColor.DARK_GRAY + "Gray",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getColors()), colorAction.apply(ShowColor.GRAY)));
        buttons.add(new MenuButton(15, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 15, ChatColor.DARK_GRAY + "Black",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getColors()), colorAction.apply(ShowColor.BLACK)));
        buttons.add(new MenuButton(16, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 12, ChatColor.DARK_GRAY + "Brown",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getColors()), colorAction.apply(ShowColor.BROWN)));
        buttons.add(new MenuButton(22, Creative.getInstance().getMenuUtil().back, ImmutableMap.of(ClickType.LEFT, p -> editAction(p, show, action))));
        buttons.add(new MenuButton(26, ItemUtil.create(Material.EMERALD_BLOCK, ChatColor.GREEN + "Confirm Colors"), ImmutableMap.of(ClickType.LEFT, p -> {
            show.saveFile();
            editAction(p, show, action);
        })));
        new Menu(Bukkit.createInventory(player, 27, ChatColor.BLUE + "Select Colors"), player, buttons);
    }

    private void selectFadeColors(Player player, Show show, FireworkAction action) {
        List<MenuButton> buttons = new ArrayList<>();
        Function<ShowColor, ImmutableMap<ClickType, Consumer<Player>>> colorAction = color -> ImmutableMap.of(ClickType.LEFT, p -> {
            if (action.getShowData().getFade().size() >= 4) {
                return;
            }

            action.getShowData().getFade().add(color);
            selectFadeColors(p, show, action);
        });
        buttons.add(new MenuButton(0, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 14, ChatColor.DARK_RED + "Red",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getFade()), colorAction.apply(ShowColor.RED)));
        buttons.add(new MenuButton(1, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 1, ChatColor.GOLD + "Orange",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getFade()), colorAction.apply(ShowColor.ORANGE)));
        buttons.add(new MenuButton(2, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 4, ChatColor.YELLOW + "Yellow",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getFade()), colorAction.apply(ShowColor.YELLOW)));
        buttons.add(new MenuButton(3, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 5, ChatColor.GREEN + "Lime",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getFade()), colorAction.apply(ShowColor.LIME)));
        buttons.add(new MenuButton(4, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 13, ChatColor.DARK_GREEN + "Green",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getFade()), colorAction.apply(ShowColor.GREEN)));
        buttons.add(new MenuButton(5, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 3, ChatColor.AQUA + "Aqua",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getFade()), colorAction.apply(ShowColor.AQUA)));
        buttons.add(new MenuButton(6, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 9, ChatColor.DARK_AQUA + "Cyan",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getFade()), colorAction.apply(ShowColor.CYAN)));
        buttons.add(new MenuButton(7, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 11, ChatColor.BLUE + "Blue",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getFade()), colorAction.apply(ShowColor.BLUE)));
        buttons.add(new MenuButton(8, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 10, ChatColor.DARK_PURPLE + "Purple",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getFade()), colorAction.apply(ShowColor.PURPLE)));
        buttons.add(new MenuButton(10, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 2, ChatColor.LIGHT_PURPLE + "Magenta",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getFade()), colorAction.apply(ShowColor.MAGENTA)));
        buttons.add(new MenuButton(11, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 6, ChatColor.RED + "Pink",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getFade()), colorAction.apply(ShowColor.PINK)));
        buttons.add(new MenuButton(12, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 0, ChatColor.WHITE + "White",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getFade()), colorAction.apply(ShowColor.WHITE)));
        buttons.add(new MenuButton(13, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 8, ChatColor.GRAY + "Silver",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getFade()), colorAction.apply(ShowColor.SILVER)));
        buttons.add(new MenuButton(14, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 7, ChatColor.DARK_GRAY + "Gray",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getFade()), colorAction.apply(ShowColor.GRAY)));
        buttons.add(new MenuButton(15, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 15, ChatColor.DARK_GRAY + "Black",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getFade()), colorAction.apply(ShowColor.BLACK)));
        buttons.add(new MenuButton(16, addGlow(ItemUtil.create(Material.WOOL, 1, (byte) 12, ChatColor.DARK_GRAY + "Brown",
                Arrays.asList(ChatColor.GRAY + "Click to Select/Deselect!")), action.getShowData().getFade()), colorAction.apply(ShowColor.BROWN)));
        buttons.add(new MenuButton(22, Creative.getInstance().getMenuUtil().back, ImmutableMap.of(ClickType.LEFT, p -> editAction(p, show, action))));
        buttons.add(new MenuButton(26, ItemUtil.create(Material.EMERALD_BLOCK, ChatColor.GREEN + "Confirm Colors"), ImmutableMap.of(ClickType.LEFT, p -> {
            show.saveFile();
            editAction(p, show, action);
        })));
        new Menu(Bukkit.createInventory(player, 27, ChatColor.BLUE + "Select Fade Colors"), player, buttons);
    }

    private void selectType(Player player, Show show, FireworkAction action) {
        List<MenuButton> buttons = new ArrayList<>();
        Function<Type, ImmutableMap<ClickType, Consumer<Player>>> typeAction = type -> ImmutableMap.of(ClickType.LEFT, p -> {
            action.setType(type);
            show.saveFile();
            editAction(p, show, action);
        });
        buttons.add(new MenuButton(9, ItemUtil.create(Material.CLAY_BALL, ChatColor.GREEN + "Ball",
                Arrays.asList(ChatColor.GRAY + "Click to Select!")), typeAction.apply(Type.BALL)));
        buttons.add(new MenuButton(11, ItemUtil.create(Material.SNOW_BALL, ChatColor.GREEN + "Large Ball",
                Arrays.asList(ChatColor.GRAY + "Click to Select!")), typeAction.apply(Type.BALL_LARGE)));
        buttons.add(new MenuButton(13, ItemUtil.create(Material.NETHER_STAR, ChatColor.GREEN + "Star",
                Arrays.asList(ChatColor.GRAY + "Click to Select!")), typeAction.apply(Type.STAR)));
        buttons.add(new MenuButton(15, ItemUtil.create(Material.CLAY_BALL, ChatColor.GREEN + "Burst",
                Arrays.asList(ChatColor.GRAY + "Click to Select!")), typeAction.apply(Type.BURST)));
        buttons.add(new MenuButton(17, ItemUtil.create(Material.SKULL_ITEM, 1, (byte) 4, ChatColor.GREEN + "Creeper",
                Arrays.asList(ChatColor.GRAY + "Click to Select!")), typeAction.apply(Type.CREEPER)));
        buttons.add(new MenuButton(22, Creative.getInstance().getMenuUtil().back, ImmutableMap.of(ClickType.LEFT, p -> editAction(p, show, action))));
        new Menu(Bukkit.createInventory(player, 27, ChatColor.BLUE + "Select Type"), player, buttons);
    }

    private void setParticle(Player player, Show show, ParticleAction action) {
        List<MenuButton> buttons = new ArrayList<>();
        ChatColor c = ChatColor.GREEN;
        Function<Particle, ImmutableMap<ClickType, Consumer<Player>>> particleAction = particle -> ImmutableMap.of(ClickType.LEFT, p -> {
            action.setParticle(particle);
            show.saveFile();
            editAction(p, show, action);
        });
        buttons.add(new MenuButton(9, ItemUtil.create(Material.POTION, 1, (byte) 16419, c + "Heart", new ArrayList<>()), particleAction.apply(Particle.HEART)));
        buttons.add(new MenuButton(10, ItemUtil.create(Material.SNOW_BALL, 1, c + "Snow Shovel", new ArrayList<>()), particleAction.apply(Particle.SNOW_SHOVEL)));
        buttons.add(new MenuButton(11, ItemUtil.create(Material.TNT, 1, c + "Explode", new ArrayList<>()), particleAction.apply(Particle.EXPLOSION_NORMAL)));
        buttons.add(new MenuButton(12, ItemUtil.create(Material.NOTE_BLOCK, 1, c + "Note", new ArrayList<>()), particleAction.apply(Particle.NOTE)));
        buttons.add(new MenuButton(13, ItemUtil.create(Material.SNOW, 1, c + "Cloud", new ArrayList<>()), particleAction.apply(Particle.CLOUD)));
        buttons.add(new MenuButton(14, ItemUtil.create(Material.FLINT_AND_STEEL, 1, c + "Flame", new ArrayList<>()), particleAction.apply(Particle.FLAME)));
        buttons.add(new MenuButton(15, ItemUtil.create(Material.REDSTONE, 1, c + "Red Dust", new ArrayList<>()), particleAction.apply(Particle.REDSTONE)));
        buttons.add(new MenuButton(16, ItemUtil.create(Material.LAVA_BUCKET, 1, c + "Lava", new ArrayList<>()), particleAction.apply(Particle.LAVA)));
        buttons.add(new MenuButton(17, ItemUtil.create(Material.FIREWORK, 1, (byte) 0, c + "Fireworks Spark", new ArrayList<>()), particleAction.apply(Particle.FIREWORKS_SPARK)));
        buttons.add(new MenuButton(22, Creative.getInstance().getMenuUtil().back, ImmutableMap.of(ClickType.LEFT, p -> editAction(p, show, action))));
        new Menu(Bukkit.createInventory(player, 27, ChatColor.BLUE + "Select Particle"), player, buttons);
    }

    private void openAddAction(Player player, Show show) {
        List<MenuButton> buttons = new ArrayList<>();
        ItemStack text = ItemUtil.create(Material.SIGN, ChatColor.GREEN + "Text Action");
        ItemStack music = ItemUtil.create(Material.RECORD_4, ChatColor.GREEN + "Set Music");
        ItemStack particle = ItemUtil.create(Material.NETHER_STAR, ChatColor.GREEN + "Particle Action");
        ItemStack fw = ItemUtil.create(Material.FIREWORK, ChatColor.GREEN + "Firework Action");
        buttons.add(new MenuButton(10, text, ImmutableMap.of(ClickType.LEFT, p -> {
            TextAction action = new TextAction(show, null, null);
            show.actions.add(action);
            editAction(p, show, action);
        })));
        buttons.add(new MenuButton(12, music, ImmutableMap.of(ClickType.LEFT, p -> selectTrack(p, 1, show))));
        buttons.add(new MenuButton(14, particle, ImmutableMap.of(ClickType.LEFT, p -> {
            ParticleAction action = new ParticleAction(show, null, null, player.getLocation(),
                    .75f, .5f, .75f, 0, 20);
            show.actions.add(action);
            editAction(p, show, action);
        })));
        buttons.add(new MenuButton(16, ItemUtil.create(Material.FIREWORK, ChatColor.GREEN + "Firework Action"), ImmutableMap.of(ClickType.LEFT, p -> {
            FireworkAction action = new FireworkAction(show, null, player.getLocation(),
                    new ShowFireworkData(FireworkEffect.Type.BALL, Arrays.asList(ShowColor.BLACK), Arrays.asList(ShowColor.WHITE),
                            false, true), 1);
            show.actions.add(action);
            editAction(p, show, action);
        })));
        buttons.add(new MenuButton(22, Creative.getInstance().getMenuUtil().back, ImmutableMap.of(ClickType.LEFT, p -> editShow(p, 1, show))));
        new Menu(Bukkit.createInventory(player, 27, ChatColor.BLUE + "Add Action"), player, buttons);
    }

    public Map<String, AudioTrack> getAudioTracks() {
        return new HashMap<>(audioTracks);
    }

    public void logout(UUID uuid) {
        Show show = shows.get(uuid);
        if (show != null) {
            stopShow(uuid);
        }
    }

    public void syncMusic(CPlayer player, Player owner) {
        for (Show s : shows.values()) {
            if (s.getOwner().equals(owner.getUniqueId())) {
                s.syncAudioForPlayer(player);
                break;
            }
        }
    }
}
