package network.palace.creative.particles;

import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.creative.Creative;
import network.palace.creative.handlers.PlayerData;
import network.palace.creative.utils.ParticleUtil;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.UUID;

/**
 * Created by Marc on 9/6/15
 */
public class ParticleManager {

    public void join(CPlayer player) {
        PlayerData playerData = Creative.getInstance().getPlayerData(player.getUuid());
        if (playerData.getParticle() != null) {
            Creative.getInstance().getPlayParticle().getParticles().put(player.getUuid(), playerData.getParticle());
//            taskIds.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(Creative.getInstance(),
//                    new PlayParticle(player, playerData.getParticle()), 0L, 2L).getTaskId());
        }
    }

    public void stop(UUID uuid) {
        Creative.getInstance().getPlayParticle().getParticles().remove(uuid);
    }

    public void clearParticle(final CPlayer player) {
        PlayerData data = Creative.getInstance().getPlayerData(player.getUniqueId());
        data.setParticle(null);
        stop(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Cleared your particle effects!");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 100, 2);
        Core.runTaskAsynchronously(Creative.getInstance(), () -> Core.getMongoHandler().setCreativeValue(player.getUniqueId(),
                "particle", "none"));
    }

    public void setParticle(final CPlayer player, String particle, String displayName) {
        stop(player.getUniqueId());
        PlayerData data = Creative.getInstance().getPlayerData(player.getUniqueId());
        final Particle effect;
        switch (particle) {
            case "notes":
                effect = Particle.NOTE;
                break;
            case "firework spark":
                effect = Particle.FIREWORKS_SPARK;
                break;
            case "mickey head":
                effect = Particle.VILLAGER_ANGRY;
                break;
            case "enchantment":
                effect = Particle.ENCHANTMENT_TABLE;
                break;
            case "flame":
                effect = Particle.FLAME;
                break;
            case "hearts":
                effect = Particle.HEART;
                break;
            case "portal":
                effect = Particle.PORTAL;
                break;
            case "lava":
                effect = Particle.LAVA;
                break;
            case "witch magic":
                effect = Particle.SPELL_WITCH;
                break;
            default:
                return;
        }

        data.setParticle(effect);

        Creative.getInstance().getPlayParticle().getParticles().put(player.getUuid(), effect);
//        taskIds.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(Creative.getInstance(),
//                new PlayParticle(player, effect), 0L, 2L).getTaskId());

        player.sendMessage(ChatColor.GREEN + "You have selected the " + displayName + ChatColor.GREEN + " Particle!");
        player.closeInventory();
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        Core.runTaskAsynchronously(Creative.getInstance(), () -> Core.getMongoHandler().setCreativeValue(player.getUniqueId(),
                "particle", ParticleUtil.getName(effect)));
    }
}
