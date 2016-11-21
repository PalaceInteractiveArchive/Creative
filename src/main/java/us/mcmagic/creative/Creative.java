package us.mcmagic.creative;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import us.mcmagic.creative.commands.*;
import us.mcmagic.creative.handlers.PlayerData;
import us.mcmagic.creative.handlers.Warp;
import us.mcmagic.creative.listeners.*;
import us.mcmagic.creative.particles.ParticleManager;
import us.mcmagic.creative.show.Show;
import us.mcmagic.creative.show.ShowManager;
import us.mcmagic.creative.utils.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Marc on 12/14/14
 */
public class Creative extends JavaPlugin {
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
    public void onEnable() {
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
    public void onDisable() {
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
        getCommand("back").setExecutor(new Commandback());
        getCommand("banner").setExecutor(new Commandbanner());
        getCommand("bc").setExecutor(new Commandbc());
        getCommand("creator").setExecutor(new Commandcreator());
        getCommand("delwarp").setExecutor(new Commanddelwarp());
        getCommand("give").setExecutor(new Commandgive());
        getCommand("give").setAliases(Arrays.asList("item", "i"));
        getCommand("head").setExecutor(new Commandhead());
        getCommand("heal").setExecutor(new Commandheal());
        getCommand("helpop").setExecutor(new Commandhelpop());
        getCommand("helpop").setAliases(Collections.singletonList("ac"));
        getCommand("invsee").setExecutor(new Commandinvsee());
        getCommand("loglag").setExecutor(new Commandloglag());
        getCommand("manage").setExecutor(new Commandmanage());
        getCommand("menu").setExecutor(new Commandmenu());
        getCommand("msg").setExecutor(new Commandmsg());
        getCommand("msg").setAliases(Arrays.asList("tell", "whisper"));
        getCommand("nv").setExecutor(new Commandnv());
        getCommand("pt").setExecutor(new Commandpt());
        getCommand("ptime").setExecutor(new Commandptime());
        getCommand("pweather").setExecutor(new Commandpweather());
        getCommand("role").setExecutor(new Commandrole());
        getCommand("role").setAliases(Arrays.asList("roleplay"));
        getCommand("rules").setExecutor(new Commandrules());
        getCommand("setspawn").setExecutor(new Commandsetspawn());
        getCommand("setwarp").setExecutor(new Commandsetwarp());
        getCommand("shop").setExecutor(new Commandshop());
        getCommand("show").setExecutor(new Commandshow());
        getCommand("spawn").setExecutor(new Commandspawn());
        getCommand("star").setExecutor(new Commandstar());
        getCommand("tp").setExecutor(new Commandtp());
        getCommand("tpa").setExecutor(new Commandtpa());
        getCommand("tpaccept").setExecutor(new Commandtpaccept());
        getCommand("tpdeny").setExecutor(new Commandtpdeny());
        getCommand("vanish").setExecutor(new Commandvanish());
        getCommand("vanish").setAliases(Collections.singletonList("v"));
        getCommand("warp").setExecutor(new Commandwarp());
        getCommand("more").setExecutor(new Commandmore());
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