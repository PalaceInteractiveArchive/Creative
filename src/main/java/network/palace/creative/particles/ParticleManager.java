package network.palace.creative.particles;

import com.comphenix.protocol.wrappers.EnumWrappers;
import network.palace.core.Core;
import network.palace.creative.Creative;
import network.palace.creative.handlers.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

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

    private void stop(Player player) {
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
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 100, 2);
        Bukkit.getScheduler().runTaskAsynchronously(Creative.getInstance(), () -> {
            try (Connection connection = Core.getSqlUtil().getConnection()) {
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
        final EnumWrappers.Particle effect;
        switch (particle) {
            case "notes":
                effect = EnumWrappers.Particle.NOTE;
                break;
            case "firework spark":
                effect = EnumWrappers.Particle.FIREWORKS_SPARK;
                break;
            case "mickey head":
                effect = EnumWrappers.Particle.VILLAGER_ANGRY;
                break;
            case "enchantment":
                effect = EnumWrappers.Particle.ENCHANTMENT_TABLE;
                break;
            case "flame":
                effect = EnumWrappers.Particle.FLAME;
                break;
            case "hearts":
                effect = EnumWrappers.Particle.HEART;
                break;
            case "portal":
                effect = EnumWrappers.Particle.PORTAL;
                break;
            case "lava":
                effect = EnumWrappers.Particle.LAVA;
                break;
            case "witch magic":
                effect = EnumWrappers.Particle.SPELL_WITCH;
                break;
            default:
                return;
        }
        data.setParticle(effect);
        taskIds.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(Creative.getInstance(),
                new PlayParticle(player, effect), 0L, 2L).getTaskId());
        player.sendMessage(ChatColor.GREEN + "You have selected the " + displayName + ChatColor.GREEN + " Particle!");
        player.closeInventory();
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        Bukkit.getScheduler().runTaskAsynchronously(Creative.getInstance(), () -> {
            try (Connection connection = Core.getSqlUtil().getConnection()) {
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