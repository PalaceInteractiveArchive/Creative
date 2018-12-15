package network.palace.creative.show.actions;

import java.util.Arrays;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.core.utils.ItemUtil;
import network.palace.creative.show.Show;
import network.palace.creative.utils.ParticleUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by Marc on 1/10/15
 */
public class ParticleAction extends ShowAction {
    public Particle particle;
    public Location loc;
    public double offsetX;
    public double offsetY;
    public double offsetZ;
    public float speed;
    public int amount;

    public ParticleAction(Show show, Long time, Particle particle, Location loc, double offsetX, double offsetY, double offsetZ, float speed, int amount) {
        super(show, time == null ? 0 : time);
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        this.particle = particle;
        this.loc = new Location(loc.getWorld(), Double.parseDouble(df.format(loc.getX())),
                Double.parseDouble(df.format(loc.getY())), Double.parseDouble(df.format(loc.getZ())));
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.speed = speed;
        this.amount = amount;
    }

    @Override
    public void play() {
        if (particle != null) {
            for (CPlayer p : Core.getPlayerManager().getOnlinePlayers()) {
                if (p == null)
                    continue;
                p.getParticles().send(loc, particle, amount, (float) offsetX, (float) offsetY, (float) offsetZ, speed);
            }
        }
    }

    @Override
    public ItemStack getItem() {
        return ItemUtil.create(Material.NETHER_STAR, ChatColor.AQUA + "Particle Action", Arrays.asList(ChatColor.GREEN + "Time: " + (time / 1000) + " Particle: " + caps(particle == null ? "none" : ParticleUtil.getName(particle)) + " Loc: " + strLoc(loc)));
    }

    @Override
    public String toString() {
        return time / 1000 + " Particle " + (particle == null ? "null" : ParticleUtil.getName(particle)) + " " + loc.getX() + "," +
                loc.getY() + "," + loc.getZ() + " " + offsetX + " " + offsetY + " " + offsetZ + " " + speed + " " + amount;
    }

    public void setParticle(Particle particle) {
        this.particle = particle;
    }
}
