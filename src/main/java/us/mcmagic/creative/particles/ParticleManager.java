package us.mcmagic.creative.particles;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import us.mcmagic.creative.Creative;
import us.mcmagic.creative.handlers.PlayerData;
import us.mcmagic.creative.utils.SqlUtil;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Marc on 9/6/15
 */
public class ParticleManager {
    private HashMap<UUID, Integer> taskIds = new HashMap<>();

    public void join(Player player) {
        PlayerData data = Creative.getPlayerData(player.getUniqueId());
        if (data.getParticle() != null) {
            taskIds.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(Creative.getInstance(),
                    new PlayParticle(player, data.getParticle()), 0L, 2L).getTaskId());
        }
    }

    public void logout(Player player) {
        stop(player);
    }

    public void stop(Player player) {
        Integer taskID = taskIds.remove(player.getUniqueId());
        if (taskID != null) {
            Bukkit.getScheduler().cancelTask(taskID);
        }
    }

    public void clearParticle(final Player player) {
        PlayerData data = Creative.getPlayerData(player.getUniqueId());
        data.setParticle(null);
        stop(player);
        player.sendMessage(ChatColor.GREEN + "You cleared your Particle!");
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 100, 2);
        Bukkit.getScheduler().runTaskAsynchronously(Creative.getInstance(), () -> {
            try (Connection connection = SqlUtil.getConnection()) {
                PreparedStatement sql = connection.prepareStatement("UPDATE creative SET particle=? WHERE uuid=?");
                sql.setString(1, "none");
                sql.setString(2, player.getUniqueId().toString());
                sql.execute();
                sql.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void setParticle(final Player player, String particle, String displayName) {
        stop(player);
        PlayerData data = Creative.getPlayerData(player.getUniqueId());
        final ParticleEffect effect;
        switch (particle) {
            case "notes":
                effect = ParticleEffect.NOTE;
                break;
            case "firework spark":
                effect = ParticleEffect.FIREWORKS_SPARK;
                break;
            case "mickey head":
                effect = ParticleEffect.ANGRY_VILLAGER;
                break;
            case "enchantment":
                effect = ParticleEffect.ENCHANTMENT_TABLE;
                break;
            case "flame":
                effect = ParticleEffect.FLAME;
                break;
            case "hearts":
                effect = ParticleEffect.HEART;
                break;
            case "portal":
                effect = ParticleEffect.PORTAL;
                break;
            case "lava":
                effect = ParticleEffect.LAVA;
                break;
            case "witch magic":
                effect = ParticleEffect.WITCH_MAGIC;
                break;
            default:
                return;
        }
        data.setParticle(effect);
        taskIds.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(Creative.getInstance(),
                new PlayParticle(player, effect), 0L, 2L).getTaskId());
        player.sendMessage(ChatColor.GREEN + "You have selected the " + displayName + ChatColor.GREEN + " Particle!");
        player.closeInventory();
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        Bukkit.getScheduler().runTaskAsynchronously(Creative.getInstance(), () -> {
            try (Connection connection = SqlUtil.getConnection()) {
                PreparedStatement sql = connection.prepareStatement("UPDATE creative SET particle=? WHERE uuid=?");
                sql.setString(1, effect.getName());
                sql.setString(2, player.getUniqueId().toString());
                sql.execute();
                sql.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}