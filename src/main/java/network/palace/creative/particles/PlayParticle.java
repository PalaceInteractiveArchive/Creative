package network.palace.creative.particles;

import lombok.Getter;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Marc on 2/15/15
 */
public class PlayParticle implements Runnable {
    private float step = 0.0F;
    private int i = 0;

    @Getter public HashMap<UUID, Particle> particles = new HashMap<>();

    public Particle getParticle(CPlayer player) {
        return particles.get(player.getUuid());
    }

    @Override
    public void run() {
        particles.forEach((uuid, particle) -> {
            CPlayer player = Core.getPlayerManager().getPlayer(uuid);
            Location loc = player.getLocation();

            if (i >= 10) {
                i = 0;
                return;
            }
            double radialsPerStep = Math.PI / 5;
            if (particle.equals(Particle.ENCHANTMENT_TABLE) || particle.equals(Particle.PORTAL)) {
                loc.add(0, 1, 0);
                particle(player, particle, loc, 0.4f, 0.3f, 0.4f, 1, 8);
            } else if (particle.equals(Particle.FLAME)) {
                if (i == 0) {
                    loc.add(0, 1, 0);
                    particle(player, particle, loc, 0.4f, 0.3f, 0.4f, 0, 25);
                } else if (i == 5) {
                    loc.add(0, 1, 0);
                    particle(player, particle, player.getLocation(), 0.4f, 0.3f, 0.4f, 0, 25);
                }
            } else if (particle.equals(Particle.LAVA)) {
                if (i == 0) {
                    loc.add(0, 1, 0);
                    particle(player, particle, loc, 0.0f, 0.0f, 0.0f, 0, 8);
                } else if (i == 5) {
                    loc.add(0, 1, 0);
                    particle(player, particle, player.getLocation(), 0.0f, 0.0f, 0.0f, 0, 8);
                }
            } else if (particle.equals(Particle.NOTE)) {
                loc.add(0.0D, 2, 0.0D);
                loc.add((Math.cos(radialsPerStep * step) / 2), 0.0D, (Math.sin(radialsPerStep * step) / 2));
                particle(player, particle, loc, 0.0F, 0.0F, 0.0F, 1, 1);

                this.step += 1.0F;
            } else if (particle.equals(Particle.REDSTONE)) {
                loc.add(0.0D, 2, 0.0D);
                loc.add((Math.cos(radialsPerStep * step) / 2), 0.0D, (Math.sin(radialsPerStep * step) / 2));
                particle(player, particle, loc, 0.1F, 0.1F, 0.1F, 1, 3);

                this.step += 1.0F;
            } else {
                loc.add(0.0D, 2, 0.0D);
                loc.add((Math.cos(radialsPerStep * step) / 2), 0.0D, (Math.sin(radialsPerStep * step) / 2));
                particle(player, particle, loc, 0.0F, 0.0F, 0.0F, 0, 1);

                this.step += 1.0F;
            }
            i++;
        });
    }

    private void particle(CPlayer player, Particle particle, Location location, float offsetX, float offsetY, float offsetZ, int speed, int count) {
        for (CPlayer onlinePlayer : Core.getPlayerManager().getOnlinePlayers()) {
            if (onlinePlayer.canSee(player) || onlinePlayer.getUniqueId().equals(player.getUniqueId())) {
                onlinePlayer.getParticles().send(location, particle, count, offsetX, offsetY, offsetZ, speed);
            }
        }
    }
}