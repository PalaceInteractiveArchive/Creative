package network.palace.creative.listeners;

import com.intellectualcrafters.plot.object.PlotPlayer;
import com.plotsquared.bukkit.util.BukkitUtil;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import network.palace.core.utils.ItemUtil;
import network.palace.creative.Creative;
import network.palace.creative.handlers.RolePlay;
import network.palace.creative.utils.TpaUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

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
        Creative.getInstance().login(event.getUniqueId());
        if (!WorldListener.isAllLoaded()) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(ChatColor.RED + "We're still loading all of the worlds!");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final CPlayer player = Core.getPlayerManager().getPlayer(event.getPlayer());
        player.getHeaderFooter().setHeader(ChatColor.GOLD + "Palace Network - A Family of Servers");
        player.getHeaderFooter().setFooter(ChatColor.LIGHT_PURPLE + "You're on the " + ChatColor.GREEN + "Creative " +
                ChatColor.LIGHT_PURPLE + "server");
        Bukkit.getScheduler().runTaskLater(Creative.getInstance(), () -> {
            PlotPlayer tp = BukkitUtil.getPlayer(player.getBukkitPlayer());
            if (player.getRank().getRankId() >= Rank.KNIGHT.getRankId()) {
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
        }, 20L);
        for (PotionEffect e : player.getBukkitPlayer().getActivePotionEffects()) {
            player.getBukkitPlayer().removePotionEffect(e.getType());
        }
        Creative.particleManager.join(player.getBukkitPlayer());
        player.sendMessage(ChatColor.GREEN + "Welcome to " + ChatColor.AQUA + "" + ChatColor.BOLD + "Palace Creative!");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        RolePlay rp = Creative.rolePlayUtil.getRolePlay(player.getUniqueId());
        if (rp != null) {
            if (rp.getOwner().equals(player.getUniqueId())) {
                Creative.rolePlayUtil.close(rp);
            } else {
                rp.leave(Core.getPlayerManager().getPlayer(player));
            }
        }
        Creative.particleManager.logout(player);
        TpaUtil.logout(player);
        Creative.teleportUtil.logout(player);
        Creative.bannerUtil.cancel(player);
        Creative.getInstance().logout(player);
        Creative.showManager.logout(player);
    }
}