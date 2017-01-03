package network.palace.creative;

import network.palace.core.plugin.Plugin;
import network.palace.creative.commands.*;
import network.palace.creative.handlers.PlayerData;
import network.palace.creative.handlers.Warp;
import network.palace.creative.listeners.*;
import network.palace.creative.particles.ParticleManager;
import network.palace.creative.show.Show;
import network.palace.creative.show.ShowManager;
import network.palace.creative.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 12/14/14
 */
public class Creative extends Plugin {
    public static Creative inst;
    private static Location spawn;
    public static YamlConfiguration config;
    public static TeleportUtil teleportUtil = new TeleportUtil();
    private static List<Warp> warps = new ArrayList<>();
    public static RedstoneListener redstoneListener;
    public static BannerUtil bannerUtil;
    public static MenuUtil menuUtil;
    public static ParticleManager particleManager;
    public static SqlUtil sqlUtil;
    public static RolePlayUtil rolePlayUtil;
    public static ShowManager showManager;
    public static OnlineUtil onlineUtil;
    public static HeadUtil headUtil;
    private static HashMap<UUID, PlayerData> playerData = new HashMap<>();

    @Override
    public void onPluginEnable() {
        inst = this;
        sqlUtil = new SqlUtil();
        redstoneListener = new RedstoneListener();
        menuUtil = new MenuUtil();
        bannerUtil = new BannerUtil();
        particleManager = new ParticleManager();
        rolePlayUtil = new RolePlayUtil();
        showManager = new ShowManager();
        onlineUtil = new OnlineUtil();
        headUtil = new HeadUtil();
        getLogger().info("Let's build!");
        for (World w : Bukkit.getWorlds()) {
            w.setTime(2000);
        }
        registerListeners();
        registerCommands();
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
        }
        File warpFile = new File("plugins/Creative/warps.yml");
        if (!warpFile.exists()) {
            try {
                warpFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration warpList = YamlConfiguration.loadConfiguration(warpFile);
        List<String> list = warpList.getStringList("warps");
        for (String item : list) {
            Warp w = new Warp(item, warpList.getDouble("warp." + item + ".x"),
                    warpList.getDouble("warp." + item + ".y"), warpList.getDouble("warp." + item + ".z"),
                    (float) warpList.getInt("warp." + item + ".yaw"), (float) warpList.getInt("warp." + item + ".pitch"),
                    warpList.getString("warp." + item + ".world"));
            warps.add(w);
        }
        config = YamlConfiguration.loadConfiguration(cnfg);
        Bukkit.getScheduler().runTaskLater(this, () -> spawn = new Location(Bukkit.getWorld(config.getString("spawn.world")),
                config.getDouble("spawn.x"), config.getDouble("spawn.y"), config.getDouble("spawn.z"), config.getInt("spawn.yaw"),
                config.getInt("spawn.pitch")), 20L);
    }

    @Override
    public void onPluginDisable() {
        for (Show show : showManager.shows.values()) {
            showManager.stopShow(Bukkit.getPlayer(show.getOwner()));
        }
    }

    public static Creative getInstance() {
        return inst;
    }

    public static Location getSpawn() {
        return new Location(spawn.getWorld(), spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch());
    }

    public static void setSpawn(Location loc) {
        spawn = loc;
        config.set("spawn.world", loc.getWorld().getName());
        config.set("spawn.x", loc.getX());
        config.set("spawn.y", loc.getY());
        config.set("spawn.z", loc.getZ());
        config.set("spawn.yaw", loc.getYaw());
        config.set("spawn.pitch", loc.getPitch());
        try {
            config.save(new File("plugins/Creative/config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Warp getWarp(String name) {
        for (Warp warp : warps) {
            if (warp.getName().equalsIgnoreCase(name)) {
                return warp;
            }
        }
        return null;
    }

    public static List<Warp> getWarps() {
        return new ArrayList<>(warps);
    }

    public static void createWarp(Warp warp) {
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
        try {
            warpFile.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean removeWarp(String name) {
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

    public void registerCommands() {
        registerCommand(new Commandback());
        registerCommand(new Commandbanner());
        registerCommand(new Commandbc());
        registerCommand(new Commandcreator());
        registerCommand(new Commanddelwarp());
        registerCommand(new Commandgive());
        registerCommand(new Commandhead());
        registerCommand(new Commandheal());
        registerCommand(new Commandinvsee());
        registerCommand(new Commandloglag());
        registerCommand(new Commandmanage());
        registerCommand(new Commandmenu());
        registerCommand(new Commandmore());
        registerCommand(new Commandmsg());
        registerCommand(new Commandnv());
        registerCommand(new Commandpt());
        registerCommand(new Commandptime());
        registerCommand(new Commandpweather());
        registerCommand(new Commandrole());
        registerCommand(new Commandrules());
        registerCommand(new Commandsetspawn());
        registerCommand(new Commandsetwarp());
        registerCommand(new Commandshop());
        registerCommand(new Commandshow());
        registerCommand(new Commandspawn());
        registerCommand(new Commandstar());
        registerCommand(new Commandtp());
        registerCommand(new Commandtpa());
        registerCommand(new Commandtpaccept());
        registerCommand(new Commandtpdeny());
        registerCommand(new Commandwarp());
    }

    public void registerListeners() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(new BlockEdit(), this);
        pm.registerEvents(new InventoryClick(), this);
        pm.registerEvents(new PlayerInteract(), this);
        pm.registerEvents(new PlayerJoinAndLeave(), this);
        pm.registerEvents(redstoneListener, this);
        pm.registerEvents(new SignChange(), this);
        pm.registerEvents(new ResourceListener(), this);
        pm.registerEvents(showManager, this);
        pm.registerEvents(menuUtil, this);
    }

    public PlayerData login(UUID uuid) {
        PlayerData data = sqlUtil.login(uuid);
        playerData.remove(uuid);
        playerData.put(uuid, data);
        return data;
    }

    public void logout(Player player) {
        playerData.remove(player.getUniqueId());
    }

    public static PlayerData getPlayerData(UUID uuid) {
        return playerData.get(uuid);
    }
}