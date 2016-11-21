package us.mcmagic.creative.listeners;

import com.intellectualcrafters.plot.object.PlotPlayer;
import com.plotsquared.bukkit.util.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import us.mcmagic.creative.Creative;
import us.mcmagic.creative.handlers.RolePlay;
import us.mcmagic.creative.utils.TpaUtil;
import us.mcmagic.creative.utils.VanishUtil;
import us.mcmagic.mcmagiccore.MCMagicCore;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.mcmagiccore.permissions.Rank;
import us.mcmagic.mcmagiccore.player.User;
import us.mcmagic.mcmagiccore.title.TitleObject;

/**
 * Created by Marc on 12/14/14
 */
public class PlayerJoinAndLeave implements Listener {
    private TitleObject rules = new TitleObject(ChatColor.YELLOW + "Read the rules!", ChatColor.GREEN +
            "Type /rules for a Link").setFadeIn(5).setStay(200).setFadeOut(5);
    private TitleObject starTitle = new TitleObject(ChatColor.YELLOW + "You removed the Menu!", ChatColor.GREEN +
            "To get the Creative Menu back, type " + ChatColor.AQUA + "/star").setFadeIn(5).setStay(100).setFadeOut(5);
    public static ItemStack star = new ItemCreator(Material.NETHER_STAR, ChatColor.AQUA + "Creative Menu");

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        if (!event.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
            return;
        }
        Creative.getInstance().login(event.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final User user = MCMagicCore.getUser(player.getUniqueId());
        Bukkit.getScheduler().runTaskLater(Creative.getInstance(), () -> {
            PlotPlayer tp = BukkitUtil.getPlayer(player);
            if (user.getRank().getRankId() >= Rank.KNIGHT.getRankId()) {
                tp.setAttribute("worldedit");
            }
            if (!player.hasPlayedBefore()) {
                rules.send(player);
                player.performCommand("spawn");
                player.getInventory().setItem(8, star);
            } else {
                if (!player.getInventory().contains(star)) {
                    starTitle.send(player);
                }
                if (!user.hasAchievement(9) && !tp.getPlots("plotworld").isEmpty()) {
                    user.giveAchievement(9);
                }
            }
        }, 20L);
        if (user.getRank().getRankId() >= Rank.EMPEROR.getRankId()) {
            Creative.getPlayerData(player.getUniqueId()).setCreator(false);
        }
        for (PotionEffect e : player.getActivePotionEffects()) {
            player.removePotionEffect(e.getType());
        }
        if (user.getRank().getRankId() < Rank.SPECIALGUEST.getRankId()) {
            VanishUtil.hideVanishedPlayers(player);
        }
        if (user.getRank().getRankId() > Rank.KNIGHT.getRankId()) {
            VanishUtil.setVanished(player);
        }
        Creative.particleManager.join(player);
        player.sendMessage(ChatColor.GREEN + "Welcome to " + ChatColor.AQUA + "" + ChatColor.BOLD + "MCMagic Creative!");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        RolePlay rp = Creative.rolePlayUtil.getRolePlay(player.getUniqueId());
        if (rp != null) {
            if (rp.getOwner().equals(player.getUniqueId())) {
                Creative.rolePlayUtil.close(rp);
            } else {
                rp.leave(player);
            }
        }
        Creative.particleManager.logout(player);
        TpaUtil.logout(player);
        VanishUtil.logout(player);
        Creative.teleportUtil.logout(player);
        Creative.bannerUtil.cancel(player);
        Creative.getInstance().logout(player);
        Creative.showManager.logout(player);
    }
}