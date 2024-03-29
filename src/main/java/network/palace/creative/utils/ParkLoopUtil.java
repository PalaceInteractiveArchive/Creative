package network.palace.creative.utils;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Table;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotId;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import network.palace.audio.Audio;
import network.palace.audio.PacketHelper;
import network.palace.audio.handlers.AudioArea;
import network.palace.audio.packets.PacketAreaStop;
import network.palace.core.Core;
import network.palace.core.menu.Menu;
import network.palace.core.menu.MenuButton;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import network.palace.creative.Creative;
import network.palace.creative.show.handlers.AudioTrack;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.ClickType;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class ParkLoopUtil {

    private final Map<String, AudioTrack> loops = new HashMap<>();
    private final Map<PlotId, String> registeredAudioAreas = new HashMap<>();
    private final Map<UUID, PlotId> disabledLoops = new HashMap<>();

    public ParkLoopUtil() {
        loadLoops();
        Bukkit.getScheduler().scheduleSyncDelayedTask(Creative.getInstance(), this::loadLoopRegions, 20);
    }

    public void reloadLoops() {
        serverShutdown();
        loadLoops();
        loadLoopRegions();
    }

    public void disableRegion(Plot plot) {
        UUID owner = plot.getOwners().iterator().next();
        PlotId plotId = plot.getId();
        String audioAreaName = registeredAudioAreas.get(plotId);
        if (audioAreaName == null) return;

        AudioArea audioArea = Audio.getInstance().getByName(audioAreaName);
        if (audioArea == null) return;

        audioArea.setEnabled(false);
        audioArea.removeAllPlayers(true);
        disabledLoops.put(owner, plotId);
    }

    public void enableRegion(UUID owner) {
        PlotId plotId = disabledLoops.get(owner);
        if (plotId == null) {
            return;
        }

        String audioAreaName = registeredAudioAreas.get(plotId);
        if (audioAreaName == null) {
            return;
        }

        AudioArea audioArea = Audio.getInstance().getByName(audioAreaName);
        audioArea.setEnabled(true);
        Core.getPlayerManager().getOnlinePlayers().forEach(audioArea::addPlayerIfInside);
    }

    private void loadLoops() {
        loops.clear();
        File file = new File("plugins/Creative/parkloops.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (config.getConfigurationSection("loops") == null) {
            Core.logMessage("Creative", ChatColor.RED + "No audio loops have been added!");
            return;
        }

        List<String> loops = new ArrayList<>(config.getConfigurationSection("loops").getKeys(false));
        Collections.sort(loops);
        for (String s : loops) {
            AudioTrack audioTrack = new AudioTrack(config.getString("loops." + s + ".name"), config.getString("loops." + s + ".path"));
            this.loops.put(s, audioTrack);
        }
    }

    private void loadLoopRegions() {
        File loopRegions = new File(Creative.getInstance().getDataFolder(), "parkloop_regions");
        loopRegions.mkdirs();
        File[] files = loopRegions.listFiles();
        if (files != null) {
            Stream.of(files).forEach(file -> {
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
                yaml.getKeys(false).forEach(key -> {
                    PlotSquared plotSquared = PlotSquared.get();
                    String[] keySplit = key.split(";");
                    plotSquared.getPlotAreas("plotworld").stream().map(plotArea -> plotArea.getPlot(new PlotId(Integer.parseInt(keySplit[0]), Integer.parseInt(keySplit[1])))).filter(Objects::nonNull).forEach(plot -> {
                        Optional<AudioTrack> audioTrack = loops.values().stream().filter(at -> at.getAudioPath().equals(yaml.getString(key))).findFirst();
                        if (audioTrack.isEmpty()) return;

                        createRegion(audioTrack.get(), plot, Bukkit.getWorld("plotworld"));
                    });
                });
            });
        }

        Creative.getInstance().getLogger().info("Plot audio loops created.");
    }

    public void open(CPlayer player, int page) {
        List<MenuButton> buttons = new ArrayList<>();
        List<AudioTrack> loops = new ArrayList<>(this.loops.values());
        loops.sort(Comparator.comparing(AudioTrack::getName));
        for (int x = 0; x < 27; x++) {
            try {
                AudioTrack track = loops.get(x + (page - 1) * 27);
                buttons.add(new MenuButton(x, ItemUtil.create(track.getItem(), ChatColor.GREEN + track.getName()), ImmutableMap.of(ClickType.LEFT, p -> {
                    Plot plot = PlotPlayer.wrap(player.getBukkitPlayer()).getCurrentPlot();
                    String oldAudioName = registeredAudioAreas.get(plot.getId());
                    Audio audio = Audio.getInstance();
                    AudioArea audioArea = audio.getByName(oldAudioName);
                    World world = player.getWorld();
                    if (audioArea != null) {
                        world = audioArea.getWorld();
                        audioArea.removeAllPlayers(true);
                        registeredAudioAreas.remove(plot.getId());
                        audio.removeArea(audioArea);
                        PacketHelper.sendToPlayer(new PacketAreaStop(audioArea.getAudioid(), 1000), player);
                    }

                    AudioArea newAudioArea = createRegion(track, plot, world);
                    newAudioArea.addPlayerIfInside(player);
                    player.sendMessage(ChatColor.GREEN + "Plot audio loop updated to " + track.getName());
                    player.closeInventory();
                })));
            } catch (IndexOutOfBoundsException ignored) {

            }
        }

        if (page - 1 > 0) {
            buttons.add(new MenuButton(27, Creative.getInstance().getMenuUtil().last, ImmutableMap.of(ClickType.LEFT, p -> open(p, page - 1))));
        }

        buttons.add(new MenuButton(30, ItemUtil.create(Material.BARRIER, ChatColor.GREEN + "None"), ImmutableMap.of(ClickType.LEFT, p -> {
            Plot plot = PlotPlayer.wrap(p.getBukkitPlayer()).getCurrentPlot();
            if (!plot.getOwners().contains(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You don't have permission to change this plot's audio.");
                player.closeInventory();
                return;
            }

            String audioAreaName = registeredAudioAreas.get(plot.getId());
            Audio audio = Audio.getInstance();
            AudioArea audioArea = audio.getByName(audioAreaName);
            if (audioArea != null) {
                audioArea.removeAllPlayers(true);
                registeredAudioAreas.remove(plot.getId());
                audio.removeArea(audioArea);
                Core.getPlayerManager().getOnlinePlayers().forEach(cp -> PacketHelper.sendToPlayer(new PacketAreaStop(audioArea.getAudioid(), 1000), cp));
            }

            player.sendMessage(ChatColor.GREEN + "Plot audio has been removed.");
            player.closeInventory();
        })));
        buttons.add(new MenuButton(32, Creative.getInstance().getMenuUtil().back, ImmutableMap.of(ClickType.LEFT, Creative.getInstance().getMenuUtil()::openMenu)));
        if (page + 1 <= Double.valueOf(Math.ceil(loops.size() / 27D)).intValue()) {
            buttons.add(new MenuButton(35, Creative.getInstance().getMenuUtil().next, ImmutableMap.of(ClickType.LEFT, p -> open(p, page + 1))));
        }
        new Menu(36, ChatColor.BLUE + "Select Park Loop", player, buttons).open();
    }

    private AudioArea createRegion(AudioTrack audioTrack, Plot plot, World world) {
        CuboidRegion region = plot.getLargestRegion();
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();
        Location minLoc = new Location(world, min.getX(), min.getY(), min.getZ());
        Location maxLoc = new Location(world, max.getX(), max.getY(), max.getZ());
        String audioId = getAudioAreaID(plot, audioTrack.getName());
        AudioArea audioArea = new AudioArea(audioId, audioTrack.getAudioPath(), 1000, 1D, minLoc, maxLoc, true, true, world);
        registeredAudioAreas.put(plot.getId(), audioId);
        Audio.getInstance().addArea(audioArea);
        return audioArea;
    }

    private String getAudioAreaID(Plot plot, String loop) {
        return plot.getId().toString().replace(";", ",") + "-" + loop.replace(" ", "_");
    }

    public void serverShutdown() {
        Audio audio = Audio.getInstance();
        Table<UUID, PlotId, String> audioTable = HashBasedTable.create();
        registeredAudioAreas.forEach((plotId, track) -> {
            if (plotId == null || track == null) return;
            AudioArea audioArea = audio.getByName(track);
            if (audioArea == null) return;

            audioArea.removeAllPlayers(true);
            audio.removeArea(audioArea);
            PlotSquared plotSquared = PlotSquared.get();
            plotSquared.getPlotAreas("plotworld").stream().map(plot -> plot.getPlot(plotId)).filter(Objects::nonNull)
                    .findFirst().ifPresent(plot -> loops.values().stream().map(AudioTrack::getAudioPath)
                    .filter(audioPath -> audioPath.equals(audioArea.getPath())).findFirst()
                    .ifPresent(path -> audioTable.put(plot.getOwners().iterator().next(), plotId, path)));
        });
        audioTable.rowKeySet().forEach(uuid -> audioTable.columnKeySet().forEach(plotId -> {
            String track = audioTable.get(uuid, plotId);
            if (track == null) {
                return;
            }

            File loopRegions = new File(Creative.getInstance().getDataFolder(), "parkloop_regions");
            loopRegions.mkdirs();
            File file = new File(loopRegions, uuid.toString() + ".yml");
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }

                YamlConfiguration yaml = new YamlConfiguration();
                yaml.set(plotId.toString(), track);
                yaml.save(file);
            } catch (IOException e) {
                Creative.getInstance().getLogger().warning("Failed to save plot audio for " + uuid.toString() + " at plot " + plotId.toString());
            }
        }));
    }
}
