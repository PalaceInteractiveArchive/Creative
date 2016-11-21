package us.mcmagic.creative.particles;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;
import us.mcmagic.mcmagiccore.particles.ParticleUtil;

/**
 * Created by Marc on 2/15/15
 */
public class PlayParticle implements Runnable {
    private Player player;
    private ParticleEffect effect;
    private float step = 0.0F;
    private int i = 0;

    public PlayParticle(Player player, ParticleEffect effect) {
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
        if (effect.equals(ParticleEffect.ENCHANTMENT_TABLE) || effect.equals(ParticleEffect.PORTAL)) {
            loc.add(0, 1, 0);
            ParticleUtil.spawnParticleArcade(player, effect, loc, 0.4f, 0.3f, 0.4f, 1, 8);
        } else if (effect.equals(ParticleEffect.FLAME)) {
            if (i == 0) {
                loc.add(0, 1, 0);
                ParticleUtil.spawnParticleArcade(player, effect, loc, 0.4f, 0.3f, 0.4f, 0, 25);
            } else if (i == 5) {
                loc.add(0, 1, 0);
                ParticleUtil.spawnParticleArcade(player, effect, player.getLocation(), 0.4f, 0.3f, 0.4f, 0, 25);
            }
        } else if (effect.equals(ParticleEffect.LAVA)) {
            if (i == 0) {
                loc.add(0, 1, 0);
                ParticleUtil.spawnParticleArcade(player, effect, loc, 0.0f, 0.0f, 0.0f, 0, 8);
            } else if (i == 5) {
                loc.add(0, 1, 0);
                ParticleUtil.spawnParticleArcade(player, effect, player.getLocation(), 0.0f, 0.0f, 0.0f, 0, 8);
            }
        } else if (effect.equals(ParticleEffect.NOTE)) {
            loc.add(0.0D, 2, 0.0D);
            loc.add((Math.cos(radialsPerStep * step) / 2), 0.0D, (Math.sin(radialsPerStep * step) / 2));
            ParticleUtil.spawnParticleArcade(player, effect, loc, 0.0F, 0.0F, 0.0F, 1, 1);
            this.step += 1.0F;
        } else if (effect.equals(ParticleEffect.RED_DUST)) {
            loc.add(0.0D, 2, 0.0D);
            loc.add((Math.cos(radialsPerStep * step) / 2), 0.0D, (Math.sin(radialsPerStep * step) / 2));
            ParticleUtil.spawnParticleArcade(player, effect, loc, 0.1F, 0.1F, 0.1F, 1, 3);
            this.step += 1.0F;
        } else {
            loc.add(0.0D, 2, 0.0D);
            loc.add((Math.cos(radialsPerStep * step) / 2), 0.0D, (Math.sin(radialsPerStep * step) / 2));
            ParticleUtil.spawnParticleArcade(player, effect, loc, 0.0F, 0.0F, 0.0F, 0, 1);
            this.step += 1.0F;
        }
        i++;
    }
}