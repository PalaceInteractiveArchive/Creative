package network.palace.creative.loop;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotId;
import com.intellectualcrafters.plot.object.RegionWrapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import network.palace.audio.Audio;
import network.palace.audio.PacketHelper;
import network.palace.audio.handlers.AudioArea;
import network.palace.core.Core;
import network.palace.core.dashboard.packets.audio.PacketAreaStop;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import network.palace.creative.Creative;
import network.palace.creative.handlers.CreativeInventoryType;
import network.palace.creative.show.handlers.AudioTrack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class ParkLoopUtil {

    private final Map<String, AudioTrack> loops = new HashMap<>();
    private final Map<PlotId, String> registeredAudioAreas = new HashMap<>();

    public ParkLoopUtil() {
        loadLoops();
        Bukkit.getScheduler().scheduleSyncDelayedTask(Creative.getInstance(), this::loadLoopRegions, 20);
    }

    private void loadLoops() {
        File file = new File("plugins/Creative/parkloops.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            }
            catch (IOException e) {
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
                    PlotAPI plotAPI = new PlotAPI(Creative.getInstance());
                    String[] keySplit = key.split("-");
                    plotAPI.getPlotAreas(Bukkit.getWorld("plotworld")).stream().map(plotArea -> plotArea.getPlot(new PlotId(Integer.parseInt(keySplit[0]), Integer.parseInt(keySplit[1])))).filter(Objects::nonNull).forEach(plot -> {
                        Optional<AudioTrack> audioTrack = loops.values().stream().filter(at -> at.getAudioPath().equals(yaml.getString(key))).findFirst();
                        if (!audioTrack.isPresent()) {
                            Creative.getInstance().getLogger().warning("Failed to create audio region for plot " + plot.getId().toString());
                            return;
                        }

                        createRegion(audioTrack.get(), plot, Bukkit.getWorld("plotworld"));
                    });
                });
            });
        }

        Creative.getInstance().getLogger().info("Plot audio loops created.");
    }

    public void open(Player player) {
        int page = 1;
        if (player.hasMetadata("page")) {
            page = player.getMetadata("page").get(0).asInt();
        }

        Inventory inv = Bukkit.createInventory(player, 36, ChatColor.BLUE + "Select Park Loop");
        List<AudioTrack> loops = new ArrayList<>(this.loops.values());
        for (int x = 0; x < 27; x++) {
            try {
                AudioTrack track = loops.get(x + (page - 1) * 27);
                inv.setItem(x, ItemUtil.create(track.getItem(), ChatColor.GREEN + track.getName()));
            }
            catch (IndexOutOfBoundsException ignored) {

            }
        }

        inv.setItem(27, Creative.getInstance().getMenuUtil().last);
        inv.setItem(31, Creative.getInstance().getMenuUtil().back);
        inv.setItem(35, Creative.getInstance().getMenuUtil().next);
        player.openInventory(inv);
    }

    public void handle(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || meta.getDisplayName() == null) {
            return;
        }

        String invname = ChatColor.stripColor(event.getInventory().getName());
        String name = ChatColor.stripColor(meta.getDisplayName());
        boolean isBack = item.getType() == Material.ARROW && name.equalsIgnoreCase("Back");
        event.setCancelled(true);
        switch (invname) {
            case "Select Park Loop": {
                if (isBack) {
                    Creative.getInstance().getMenuUtil().openMenu(player, CreativeInventoryType.PLOT_SETTINGS);
                    return;
                }

                int maxPages = new Double(Math.ceil(loops.size() / 27D)).intValue();
                int page = 1;
                if (player.hasMetadata("page")) {
                    page = player.getMetadata("page").get(0).asInt();
                }

                switch (name) {
                    case "Next Page": {
                        if (page + 1 <= maxPages) {
                            player.setMetadata("page", new FixedMetadataValue(Creative.getInstance(), page + 1));
                            open(player);
                        }

                        break;
                    }
                    case "Last Page": {
                        if (page - 1 > 0) {
                            player.setMetadata("page", new FixedMetadataValue(Creative.getInstance(), page - 1));
                            open(player);
                        }

                        break;
                    }
                    default: {
                        player.removeMetadata("page", Creative.getInstance());
                        PlotAPI api = new PlotAPI(Creative.getInstance());
                        Plot plot = api.getPlot(player);
                        loops.values().stream().filter(audioTrack -> name.equals(audioTrack.getName())).findFirst().ifPresent(audioTrack -> {
                            String oldAudioName = registeredAudioAreas.get(plot.getId());
                            Audio audio = Audio.getInstance();
                            AudioArea audioArea = audio.getByName(oldAudioName);
                            CPlayer cPlayer = Core.getPlayerManager().getPlayer(player);
                            World world = player.getWorld();
                            if (audioArea != null) {
                                world = audioArea.getWorld();
                                audioArea.removeAllPlayers(true);
                                registeredAudioAreas.remove(plot.getId());
                                audio.removeArea(audioArea);
                                PacketHelper.sendToPlayer(new PacketAreaStop(audioArea.getAudioid(), 1000), cPlayer);
                            }

                            AudioArea newAudioArea = createRegion(audioTrack, plot, world);
                            newAudioArea.addPlayerIfInside(cPlayer);
                            player.sendMessage(ChatColor.GREEN + "Plot audio loop updated to " + name);
                            player.closeInventory();
                        });
                    }
                }

                break;
            }
        }
    }

    private AudioArea createRegion(AudioTrack audioTrack, Plot plot, World world) {
        RegionWrapper region = plot.getLargestRegion();
        Location minLoc = new Location(world, region.minX, region.minY, region.minZ);
        Location maxLoc = new Location(world, region.maxX, region.maxY, region.maxZ);
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
            AudioArea audioArea = audio.getByName(track);
            if (audioArea == null) {
                return;
            }

            audioArea.removeAllPlayers(true);
            audio.removeArea(audioArea);
            PlotAPI plotAPI = new PlotAPI();
            plotAPI.getPlotAreas(Bukkit.getWorld("plotworld")).stream().map(plot -> plot.getPlot(plotId)).filter(Objects::nonNull).findFirst().ifPresent(plot -> {
                loops.values().stream().filter(audioTrack -> audioTrack.getAudioPath().equals(audioArea.getPath())).map(AudioTrack::getAudioPath).findFirst().ifPresent(path -> {
                    audioTable.put(plot.getOwners().iterator().next(), plotId, path);
                });
            });
        });
        audioTable.rowKeySet().forEach(uuid -> {
            audioTable.columnKeySet().forEach(plotId -> {
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
                    yaml.set(plotId.toString().replace(";", "-"), track);
                    yaml.save(file);
                }
                catch (IOException e) {
                    Creative.getInstance().getLogger().warning("Failed to save plot audio for " + uuid.toString() + " at plot " + plotId.toString());
                    return;
                }
            });
        });
    }
}
