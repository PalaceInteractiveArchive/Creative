package network.palace.creative;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import network.palace.audio.Audio;
import network.palace.audio.handlers.AudioArea;
import network.palace.core.Core;
import network.palace.core.player.Rank;
import network.palace.core.plugin.Plugin;
import network.palace.core.plugin.PluginInfo;
import network.palace.creative.commands.BackCommand;
import network.palace.creative.commands.BannedItemCheckCommand;
import network.palace.creative.commands.BannerCommand;
import network.palace.creative.commands.BroadcastCommand;
import network.palace.creative.commands.CReloadCommand;
import network.palace.creative.commands.CreatorCommand;
import network.palace.creative.commands.DelWarpCommand;
import network.palace.creative.commands.GiveCommand;
import network.palace.creative.commands.HeadCommand;
import network.palace.creative.commands.HealCommand;
import network.palace.creative.commands.InvseeCommand;
import network.palace.creative.commands.LogLagCommand;
import network.palace.creative.commands.ManageCommand;
import network.palace.creative.commands.MenuCommand;
import network.palace.creative.commands.MoreCommand;
import network.palace.creative.commands.MsgCommand;
import network.palace.creative.commands.NightvisionCommand;
import network.palace.creative.commands.PTCommand;
import network.palace.creative.commands.PackCommand;
import network.palace.creative.commands.PlotFloorLogCommand;
import network.palace.creative.commands.PlotWarpCommand;
import network.palace.creative.commands.PtimeCommand;
import network.palace.creative.commands.PweatherCommand;
import network.palace.creative.commands.ReviewCommand;
import network.palace.creative.commands.RoleCommand;
import network.palace.creative.commands.RulesCommand;
import network.palace.creative.commands.SetSpawnCommand;
import network.palace.creative.commands.SetWarpCommand;
import network.palace.creative.commands.ShopCommand;
import network.palace.creative.commands.SpawnCommand;
import network.palace.creative.commands.StarCommand;
import network.palace.creative.commands.SubmitCommand;
import network.palace.creative.commands.TpAcceptCommand;
import network.palace.creative.commands.TpCommand;
import network.palace.creative.commands.TpDenyCommand;
import network.palace.creative.commands.TpaCommand;
import network.palace.creative.commands.WarpCommand;
import network.palace.creative.handlers.PlayerData;
import network.palace.creative.handlers.Warp;
import network.palace.creative.itemexploit.ItemExploitHandler;
import network.palace.creative.listeners.BlockEdit;
import network.palace.creative.listeners.EntitySpawn;
import network.palace.creative.listeners.InventoryClick;
import network.palace.creative.listeners.PacketListener;
import network.palace.creative.listeners.PlayerDamage;
import network.palace.creative.listeners.PlayerInteract;
import network.palace.creative.listeners.PlayerJoinAndLeave;
import network.palace.creative.listeners.PlayerMove;
import network.palace.creative.listeners.PlayerPlotListener;
import network.palace.creative.listeners.RedstoneListener;
import network.palace.creative.listeners.ResourceListener;
import network.palace.creative.listeners.SignChange;
import network.palace.creative.listeners.WorldListener;
import network.palace.creative.particles.ParticleManager;
import network.palace.creative.particles.PlayParticle;
import network.palace.creative.plotreview.PlotReview;
import network.palace.creative.utils.BannerUtil;
import network.palace.creative.utils.CreativeRank;
import network.palace.creative.utils.HeadUtil;
import network.palace.creative.utils.IgnoreUtil;
import network.palace.creative.utils.MenuUtil;
import network.palace.creative.utils.OnlineUtil;
import network.palace.creative.utils.ParkLoopUtil;
import network.palace.creative.utils.ParticleUtil;
import network.palace.creative.utils.PlotFloorUtil;
import network.palace.creative.utils.PlotWarpUtil;
import network.palace.creative.utils.ResourceUtil;
import network.palace.creative.utils.RolePlayUtil;
import network.palace.creative.utils.TeleportUtil;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Created by Marc on 12/14/14
 */
@PluginInfo(name = "Creative", depend = {"Core", "PlotSquared", "ProtocolLib"}, version = "2.8.6-1.13", apiversion = "1.13.2")
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
    @Getter private OnlineUtil onlineUtil;
    @Getter private HeadUtil headUtil;
    @Getter private ResourceUtil resourceUtil;
    @Getter private IgnoreUtil ignoreUtil;
    @Getter private ItemExploitHandler itemExploitHandler;
    @Getter private PlotFloorUtil plotFloorUtil;
    @Getter private PlotReview plotReview;
    @Getter private PlotWarpUtil plotWarpUtil;
    @Getter private Map<UUID, PlayerData> playerData = new HashMap<>();

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
        onlineUtil = new OnlineUtil();
        headUtil = new HeadUtil();
        resourceUtil = new ResourceUtil();
        ignoreUtil = new IgnoreUtil();
        plotFloorUtil = new PlotFloorUtil();
        plotReview = new PlotReview();
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
        plotReview.save();
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
            config.save(new File(getDataFolder(), "config.yml"));
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

        File file = new File(getDataFolder(), "warps.yml");
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

        File file = new File(getDataFolder(), "warps.yml");
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
        //TODO broken until needed to fix
        //registerCommand(new DownloadCommand());
        registerCommand(new GiveCommand());
        registerCommand(new HeadCommand());
        registerCommand(new HealCommand());
        registerCommand(new InvseeCommand());
        registerCommand(new LogLagCommand());
        registerCommand(new ManageCommand());
        registerCommand(new MenuCommand());
        registerCommand(new MoreCommand());
        registerCommand(new MsgCommand());
        registerCommand(new NightvisionCommand());
        registerCommand(new PackCommand());
        registerCommand(new PlotFloorLogCommand());
        registerCommand(new PlotWarpCommand());
        registerCommand(new PTCommand());
        registerCommand(new PtimeCommand());
        registerCommand(new PweatherCommand());
        registerCommand(new ReviewCommand());
        registerCommand(new RoleCommand());
        registerCommand(new RulesCommand());
        registerCommand(new SetSpawnCommand());
        registerCommand(new SetWarpCommand());
        registerCommand(new ShopCommand());
        registerCommand(new SpawnCommand());
        registerCommand(new StarCommand());
        registerCommand(new SubmitCommand());
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
        registerListener(menuUtil);
        registerListener(itemExploitHandler = new ItemExploitHandler());
    }

    public static Creative getInstance() {
        return Creative.getPlugin(Creative.class);
    }

    public PlayerData login(UUID uuid) {
        Document dataDocument = Core.getMongoHandler().getCreativeData(uuid);
        if (dataDocument == null) return null;
        PlayerData data = new PlayerData(uuid, ParticleUtil.getParticle(dataDocument.getString("particle")),
                dataDocument.getBoolean("rptag"), CreativeRank.valueOf(dataDocument.getString("rank").toUpperCase()),
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
        File cnfg = new File(getDataFolder(), "config.yml");
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
        File warpFile = new File(getDataFolder(), "warps.yml");
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

    public static com.github.intellectualsites.plotsquared.plot.object.Location wrapLocation(Location location) {
        return new com.github.intellectualsites.plotsquared.plot.object.Location(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }
}
