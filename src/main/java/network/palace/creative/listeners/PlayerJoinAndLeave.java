package network.palace.creative.listeners;

import com.github.intellectualsites.plotsquared.plot.object.PlotPlayer;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.ItemUtil;
import network.palace.creative.Creative;
import network.palace.creative.handlers.RolePlay;
import network.palace.creative.utils.TpaUtil;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import java.util.UUID;

/**
 * Created by Marc on 12/14/14
 */
public class PlayerJoinAndLeave implements Listener {
    public static ItemStack star = ItemUtil.create(Material.NETHER_STAR, ChatColor.AQUA + "Creative Menu");

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        if (!event.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
            return;
        }
        if (Creative.getInstance().login(event.getUniqueId()) == null) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(ChatColor.RED + "There was an error loading your Player Data!");
        }
        if (!WorldListener.isAllLoaded()) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(ChatColor.RED + "We're still loading all of the worlds!");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        if (player == null) return;

        player.getHeaderFooter().setHeader(ChatColor.GOLD + "Palace Network - A Family of Servers");
        player.getHeaderFooter().setFooter(ChatColor.LIGHT_PURPLE + "You're on the " + ChatColor.GREEN + "Creative " +
                ChatColor.LIGHT_PURPLE + "server");
        Core.runTaskLater(() -> {
            PlotPlayer tp = PlotPlayer.wrap(player.getBukkitPlayer());
            if (player.getRank().getRankId() >= Rank.MOD.getRankId()) {
                tp.setAttribute("worldedit");
            }
            if (!player.getBukkitPlayer().hasPlayedBefore()) {
                player.getTitle().show(ChatColor.YELLOW + "Read the rules!", ChatColor.GREEN +
                        "Type /rules for a Link", 5, 200, 5);
                player.getBukkitPlayer().performCommand("spawn");
                player.setGamemode(GameMode.ADVENTURE);
                player.getInventory().setItem(8, star);
            } else {
                if (!player.getInventory().contains(star)) {
                    player.getTitle().show(ChatColor.YELLOW + "You removed the Menu!", ChatColor.GREEN +
                            "To get the Creative Menu back, type " + ChatColor.AQUA + "/star", 5, 100, 5);
                }
            }
            if (player.getLocation().getWorld().getName().equalsIgnoreCase("spawn")) {
                PlayerInventory inv = player.getInventory();
                inv.remove(Material.ELYTRA);
                if (inv.getChestplate() != null && inv.getChestplate().getType() != null &&
                        inv.getChestplate().getType().equals(Material.ELYTRA)) {
                    inv.setChestplate(new ItemStack(Material.AIR));
                }
            }
            if (tp.getPlots() != null && !tp.getPlots().isEmpty()) player.giveAchievement(9);
        }, 20L);
        for (PotionEffect e : player.getBukkitPlayer().getActivePotionEffects()) {
            player.getBukkitPlayer().removePotionEffect(e.getType());
        }
        Creative.getInstance().getParticleManager().join(player);
        player.sendMessage(ChatColor.GREEN + "Welcome to " + ChatColor.AQUA + "" + ChatColor.BOLD + "Palace Creative!");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        Creative creative = Creative.getInstance();
        RolePlay rp = creative.getRolePlayUtil().getRolePlay(uuid);
        if (rp != null) {
            if (rp.getOwner().equals(uuid)) {
                creative.getRolePlayUtil().close(rp);
            } else {
                rp.leave(player);
            }
        }
        TpaUtil.logout(player);
        creative.getIgnoreUtil().logout(uuid);
        creative.getParticleManager().stop(uuid);
        creative.getTeleportUtil().logout(uuid);
        creative.getBannerUtil().cancel(uuid);
        creative.getShowManager().logout(uuid);
        creative.logout(uuid);
    }
}
