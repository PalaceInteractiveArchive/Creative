package network.palace.creative;

import lombok.Getter;
import network.palace.audio.Audio;
import network.palace.audio.handlers.AudioArea;
import network.palace.core.Core;
import network.palace.core.player.Rank;
import network.palace.core.plugin.Plugin;
import network.palace.core.plugin.PluginInfo;
import network.palace.creative.commands.*;
import network.palace.creative.handlers.PlayerData;
import network.palace.creative.handlers.Warp;
import network.palace.creative.itemexploit.ItemExploitHandler;
import network.palace.creative.listeners.*;
import network.palace.creative.particles.ParticleManager;
import network.palace.creative.particles.PlayParticle;
import network.palace.creative.show.ShowManager;
import network.palace.creative.utils.*;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 12/14/14
 */
@PluginInfo(name = "Creative", depend = {"Core", "PlotSquared", "ProtocolLib"}, version = "2.9.0")
public class Creative extends Plugin {
    private Location spawn;
    @Getter private YamlConfiguration config;
    @Getter private TeleportUtil teleportUtil = new TeleportUtil();
    private List<Warp> warps = new ArrayList<>();
    @Getter private RedstoneListener redstoneListener;
    @Getter private BannerUtil bannerUtil;
    @Getter private MenuUtil menuUtil;
    @Getter private ParkLoopUtil parkLoopUtil;
    @Getter private ParticleManager particleManager;
    @Getter private RolePlayUtil rolePlayUtil;
    @Getter private ShowManager showManager;
    @Getter private OnlineUtil onlineUtil;
    @Getter private HeadUtil headUtil;
    @Getter private ResourceUtil resourceUtil;
    @Getter private IgnoreUtil ignoreUtil;
    @Getter private ItemExploitHandler itemExploitHandler;
    @Getter private PlotFloorUtil plotFloorUtil;
    @Getter private PlotWarpUtil plotWarpUtil;
    @Getter private HashMap<UUID, PlayerData> playerData = new HashMap<>();

    @Getter private PlayParticle playParticle;

    @Override
    public void onPluginEnable() {
        loadConfig();
        loadWarps();

        redstoneListener = new RedstoneListener();
        menuUtil = new MenuUtil();
        parkLoopUtil = new ParkLoopUtil();
        bannerUtil = new BannerUtil();
        particleManager = new ParticleManager();
        rolePlayUtil = new RolePlayUtil();
        showManager = new ShowManager();
        onlineUtil = new OnlineUtil();
        headUtil = new HeadUtil();
        resourceUtil = new ResourceUtil();
        ignoreUtil = new IgnoreUtil();
        plotFloorUtil = new PlotFloorUtil();
        plotWarpUtil = new PlotWarpUtil();
        playParticle = new PlayParticle();

        Core.runTaskTimer(playParticle, 0L, 2L);

        registerListeners();
        registerCommands();

        for (AudioArea area : Audio.getInstance().getAudioAreas()) {
            try {
                UUID.fromString(area.getAreaName());
                Audio.getInstance().removeArea(area);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void onPluginDisable() {
        itemExploitHandler.saveCaughtItems();
        parkLoopUtil.serverShutdown();
        showManager.stopAllShows();
    }

    public Location getSpawn() {
        if (spawn == null) {
            return new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
        }
        return new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch());
    }

    public void setSpawn(Location location) {
        spawn = location;

        config.set("spawn.world", location.getWorld().getName());
        config.set("spawn.x", location.getX());
        config.set("spawn.y", location.getY());
        config.set("spawn.z", location.getZ());
        config.set("spawn.yaw", location.getYaw());
        config.set("spawn.pitch", location.getPitch());

        try {
            config.save(new File("plugins/Creative/config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Warp getWarp(String name) {
        for (Warp warp : warps) {
            if (warp.getName().equalsIgnoreCase(name)) {
                return warp;
            }
        }
        return null;
    }

    public List<Warp> getWarps() {
        return new ArrayList<>(warps);
    }

    public void createWarp(Warp warp) {
        Warp w = getWarp(warp.getName());
        if (w != null) {
            warps.remove(w);
        }

        warps.add(warp);

        File file = new File("plugins/Creative/warps.yml");
        Location loc = warp.getLocation();

        YamlConfiguration warpFile = YamlConfiguration.loadConfiguration(file);
        List<String> list = warpFile.getStringList("warps");
        list.add(warp.getName());

        warpFile.set("warps", list);
        warpFile.set("warp." + warp.getName() + ".x", loc.getX());
        warpFile.set("warp." + warp.getName() + ".y", loc.getY());
        warpFile.set("warp." + warp.getName() + ".z", loc.getZ());
        warpFile.set("warp." + warp.getName() + ".yaw", loc.getYaw());
        warpFile.set("warp." + warp.getName() + ".pitch", loc.getPitch());
        warpFile.set("warp." + warp.getName() + ".world", loc.getWorld().getName());
        warpFile.set("warp." + warp.getName() + ".rank", warp.getRank().toString());

        try {
            warpFile.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean removeWarp(String name) {
        Warp w = getWarp(name);
        if (w == null) {
            return false;
        }

        File file = new File("plugins/Creative/warps.yml");
        YamlConfiguration warpFile = YamlConfiguration.loadConfiguration(file);
        List<String> list = warpFile.getStringList("warps");
        list.remove(w.getName());
        warpFile.set("warps", list);

        try {
            warpFile.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        warps.remove(w);
        return true;
    }

    private void registerCommands() {
        registerCommand(new BackCommand());
        registerCommand(new BannedItemCheckCommand());
        registerCommand(new BannerCommand());
        registerCommand(new BroadcastCommand());
        registerCommand(new CreatorCommand());
        registerCommand(new CReloadCommand());
        registerCommand(new DelWarpCommand());
        registerCommand(new DownloadCommand());
        registerCommand(new GiveCommand());
        registerCommand(new HeadCommand());
        registerCommand(new HealCommand());
        registerCommand(new InvseeCommand());
        registerCommand(new LogLagCommand());
        registerCommand(new ManageCommand());
        registerCommand(new MenuCommand());
        registerCommand(new MoreCommand());
        registerCommand(new NightvisionCommand());
        registerCommand(new PackCommand());
        registerCommand(new PlotFloorLogCommand());
        registerCommand(new PlotWarpCommand());
        registerCommand(new PTCommand());
        registerCommand(new PtimeCommand());
        registerCommand(new PweatherCommand());
        registerCommand(new RoleCommand());
        registerCommand(new RulesCommand());
        registerCommand(new SetSpawnCommand());
        registerCommand(new SetWarpCommand());
        registerCommand(new ShopCommand());
        registerCommand(new ShowCommand());
        registerCommand(new SpawnCommand());
        registerCommand(new StarCommand());
        registerCommand(new TpCommand());
        registerCommand(new TpaCommand());
        registerCommand(new TpAcceptCommand());
        registerCommand(new TpDenyCommand());
        registerCommand(new WarpCommand());
    }

    private void registerListeners() {
        registerListener(new BlockEdit());
        registerListener(new EntitySpawn());
        registerListener(new InventoryClick());
        registerListener(new PacketListener());
        registerListener(new PlayerDamage());
        registerListener(new PlayerInteract());
        registerListener(new PlayerJoinAndLeave());
        registerListener(new PlayerMove());
        registerListener(new PlayerPlotListener());
        registerListener(redstoneListener);
        registerListener(new SignChange());
        registerListener(new ResourceListener());
        registerListener(new WorldListener());
        registerListener(showManager);
        registerListener(menuUtil);
        registerListener(itemExploitHandler = new ItemExploitHandler());
    }

    public static Creative getInstance() {
        return Creative.getPlugin(Creative.class);
    }

    public PlayerData login(UUID uuid) {
        Document dataDocument = Core.getMongoHandler().getCreativeData(uuid);
        if (dataDocument == null) return null;
        Particle p;
        try {
            p = ParticleUtil.getParticle(dataDocument.getString("particle"));
        } catch (Exception e) {
            p = null;
        }
        PlayerData data = new PlayerData(uuid, p,
                dataDocument.getBoolean("rptag"), dataDocument.getBoolean("showcreator"),
                dataDocument.getInteger("rplimit"), dataDocument.getBoolean("creator"),
                dataDocument.getBoolean("creatortag"), dataDocument.getString("resourcepack"));
        playerData.remove(uuid);
        playerData.put(uuid, data);
        return data;
    }

    public void logout(UUID uuid) {
        playerData.remove(uuid);
    }

    public PlayerData getPlayerData(UUID uuid) {
        return playerData.get(uuid);
    }

    private void createDataFolder() {
        File dir = getDataFolder();
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    public void loadConfig() {
        createDataFolder();
        File showFolder = new File("plugins/Creative/shows/");
        if (!showFolder.exists()) {
            showFolder.mkdir();
        }

        File cnfg = new File("plugins/Creative/config.yml");
        if (!cnfg.exists()) {
            try {
                cnfg.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            config = YamlConfiguration.loadConfiguration(cnfg);
        } else {
            config = YamlConfiguration.loadConfiguration(cnfg);
            if (config.getString("spawn.world") == null) {
                Core.logMessage("Creative", ChatColor.RED + "No spawn location has been defined!");
                spawn = null;
                return;
            }
            spawn = new Location(Bukkit.getWorld(config.getString("spawn.world")), config.getDouble("spawn.x"),
                    config.getDouble("spawn.y"), config.getDouble("spawn.z"), config.getInt("spawn.yaw"),
                    config.getInt("spawn.pitch"));
        }
    }

    public void loadWarps() {
        createDataFolder();
        File warpFile = new File("plugins/Creative/warps.yml");
        if (!warpFile.exists()) {
            try {
                warpFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            warps.clear();
            YamlConfiguration warpList = YamlConfiguration.loadConfiguration(warpFile);
            List<String> list = warpList.getStringList("warps");
            for (String item : list) {
                Rank rank = Rank.SETTLER;
                if (warpList.contains("warp." + item + ".rank")) {
                    rank = Rank.fromString(warpList.getString("warp." + item + ".rank"));
                }

                Warp w = new Warp(item, warpList.getDouble("warp." + item + ".x"),
                        warpList.getDouble("warp." + item + ".y"), warpList.getDouble("warp." + item + ".z"),
                        (float) warpList.getInt("warp." + item + ".yaw"), (float) warpList.getInt("warp." + item + ".pitch"),
                        warpList.getString("warp." + item + ".world"), rank);
                warps.add(w);
            }
        }
    }
}
