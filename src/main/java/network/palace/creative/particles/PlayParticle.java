package network.palace.creative.particles;

import com.comphenix.protocol.wrappers.EnumWrappers;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by Marc on 2/15/15
 */
public class PlayParticle implements Runnable {
    private Player player;
    private EnumWrappers.Particle effect;
    private float step = 0.0F;
    private int i = 0;

    public PlayParticle(Player player, EnumWrappers.Particle effect) {
        this.player = player;
        this.effect = effect;
    }

    @Override
    public void run() {
        Location loc = player.getLocation();
        if (i >= 10) {
            i = 0;
            return;
        }
        double radialsPerStep = Math.PI / 5;
        if (effect.equals(EnumWrappers.Particle.ENCHANTMENT_TABLE) || effect.equals(EnumWrappers.Particle.PORTAL)) {
            loc.add(0, 1, 0);
            particle(player, effect, loc, 0.4f, 0.3f, 0.4f, 1, 8);
        } else if (effect.equals(EnumWrappers.Particle.FLAME)) {
            if (i == 0) {
                loc.add(0, 1, 0);
                particle(player, effect, loc, 0.4f, 0.3f, 0.4f, 0, 25);
            } else if (i == 5) {
                loc.add(0, 1, 0);
                particle(player, effect, player.getLocation(), 0.4f, 0.3f, 0.4f, 0, 25);
            }
        } else if (effect.equals(EnumWrappers.Particle.LAVA)) {
            if (i == 0) {
                loc.add(0, 1, 0);
                particle(player, effect, loc, 0.0f, 0.0f, 0.0f, 0, 8);
            } else if (i == 5) {
                loc.add(0, 1, 0);
                particle(player, effect, player.getLocation(), 0.0f, 0.0f, 0.0f, 0, 8);
            }
        } else if (effect.equals(EnumWrappers.Particle.NOTE)) {
            loc.add(0.0D, 2, 0.0D);
            loc.add((Math.cos(radialsPerStep * step) / 2), 0.0D, (Math.sin(radialsPerStep * step) / 2));
            particle(player, effect, loc, 0.0F, 0.0F, 0.0F, 1, 1);
            this.step += 1.0F;
        } else if (effect.equals(EnumWrappers.Particle.REDSTONE)) {
            loc.add(0.0D, 2, 0.0D);
            loc.add((Math.cos(radialsPerStep * step) / 2), 0.0D, (Math.sin(radialsPerStep * step) / 2));
            particle(player, effect, loc, 0.1F, 0.1F, 0.1F, 1, 3);
            this.step += 1.0F;
        } else {
            loc.add(0.0D, 2, 0.0D);
            loc.add((Math.cos(radialsPerStep * step) / 2), 0.0D, (Math.sin(radialsPerStep * step) / 2));
            particle(player, effect, loc, 0.0F, 0.0F, 0.0F, 0, 1);
            this.step += 1.0F;
        }
        i++;
    }

    private void particle(Player player, EnumWrappers.Particle effect, Location loc, float offsetX, float offsetY, float offsetZ, int speed, int count) {
        for (CPlayer tp : Core.getPlayerManager().getOnlinePlayers()) {
            if (tp.canSee(player)) {
                tp.getParticles().send(loc, effect, count, offsetX, offsetY, offsetZ, speed);
            }
        }
    }
}