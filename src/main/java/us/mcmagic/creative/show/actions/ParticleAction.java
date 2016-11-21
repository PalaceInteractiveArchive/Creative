package us.mcmagic.creative.show.actions;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.creative.show.Show;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;
import us.mcmagic.mcmagiccore.particles.ParticleEffect;
import us.mcmagic.mcmagiccore.particles.ParticleUtil;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by Marc on 1/10/15
 */
public class ParticleAction extends ShowAction {
    private Show show;
    public ParticleEffect particle;
    public Location loc;
    public double offsetX;
    public double offsetY;
    public double offsetZ;
    public float speed;
    public int amount;

    public ParticleAction(Integer id, Show show, Long time, ParticleEffect particle, Location loc, double offsetX,
                          double offsetY, double offsetZ, float speed, int amount) {
        super(id, show, time == null ? 0 : time);
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        this.show = show;
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
            ParticleUtil.spawnParticle(particle, loc, (float) offsetX, (float) offsetY, (float) offsetZ, speed, amount);
        }
    }

    @Override
    public ItemStack getItem() {
        return new ItemCreator(Material.NETHER_STAR, ChatColor.AQUA + "Particle Action");
    }

    @Override
    public String toString() {
        return time / 1000 + " Particle " + (particle == null ? "null" : particle.getName()) + " " + loc.getX() + "," +
                loc.getY() + "," + loc.getZ() + " " + offsetX + " " + offsetY + " " + offsetZ + " " + speed + " " + amount;
    }

    @Override
    public String getDescription() {
        return ChatColor.GREEN + "Time: " + (time / 1000) + " Particle: " + caps(particle == null ? "none" :
                particle.getName()) + " Loc: " + strLoc(loc);
    }

    public void setParticle(ParticleEffect particle) {
        this.particle = particle;
    }
}