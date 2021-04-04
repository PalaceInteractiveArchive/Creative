package network.palace.creative.utils;

import org.bukkit.Particle;

/**
 * Created by Marc on 2/10/17.
 */
public class ParticleUtil {

    public static Particle getParticle(String name) {
        switch (name.toLowerCase().replace(" ", "").replace("_", "")) {
            case "hugeexplosion":
                return Particle.EXPLOSION_HUGE;
            case "largeexplode":
                return Particle.EXPLOSION_LARGE;
            case "fireworksspark":
                return Particle.FIREWORKS_SPARK;
            case "bubble":
                return Particle.WATER_BUBBLE;
            case "suspend":
                return Particle.SUSPENDED;
            case "depthsuspend":
                return Particle.SUSPENDED_DEPTH;
            case "townaura":
                return Particle.TOWN_AURA;
            case "magiccrit":
                return Particle.CRIT_MAGIC;
            case "smoke":
                return Particle.SMOKE_NORMAL;
            case "mobspell":
                return Particle.SPELL_MOB;
            case "mobspellambient":
                return Particle.SPELL_MOB_AMBIENT;
            case "spell":
                return Particle.SPELL;
            case "instantspell":
                return Particle.SPELL_INSTANT;
            case "witchmagic":
                return Particle.SPELL_WITCH;
            case "note":
                return Particle.NOTE;
            case "portal":
                return Particle.PORTAL;
            case "enchantmenttable":
                return Particle.ENCHANTMENT_TABLE;
            case "explode":
                return Particle.EXPLOSION_NORMAL;
            case "flame":
                return Particle.FLAME;
            case "lava":
                return Particle.LAVA;
            case "splash":
                return Particle.WATER_SPLASH;
            case "wake":
                return Particle.WATER_WAKE;
            case "largesmoke":
                return Particle.SMOKE_LARGE;
            case "cloud":
                return Particle.CLOUD;
            case "reddust":
                return Particle.REDSTONE;
            case "snowballpoof":
                return Particle.SNOWBALL;
            case "dripwater":
                return Particle.DRIP_WATER;
            case "driplava":
                return Particle.DRIP_LAVA;
            case "snowshovel":
                return Particle.SNOW_SHOVEL;
            case "slime":
                return Particle.SLIME;
            case "heart":
                return Particle.HEART;
            case "angryvillager":
                return Particle.VILLAGER_ANGRY;
            case "happyvillager":
                return Particle.VILLAGER_HAPPY;
            default:
                return null;
        }
    }

    public static String getName(Particle particle) {
        switch (particle) {
            case EXPLOSION_NORMAL:
                return "explode";
            case EXPLOSION_LARGE:
                return "largeexplode";
            case EXPLOSION_HUGE:
                return "hugeexplosion";
            case FIREWORKS_SPARK:
                return "fireworksSpark";
            case WATER_BUBBLE:
                return "bubble";
            case WATER_SPLASH:
                return "splash";
            case WATER_WAKE:
                return "wake";
            case SUSPENDED:
                return "suspend";
            case SUSPENDED_DEPTH:
                return "depthsuspend";
            case CRIT_MAGIC:
                return "magiccrit";
            case VILLAGER_ANGRY:
                return "angryvillager";
            case VILLAGER_HAPPY:
                return "happyvillager";
            case NOTE:
                return "note";
            case PORTAL:
                return "portal";
            case ENCHANTMENT_TABLE:
                return "enchantmenttable";
            case FLAME:
                return "flame";
            case LAVA:
                return "lava";
            case HEART:
                return "heart";
            case SNOW_SHOVEL:
                return "snowshovel";
            case CLOUD:
                return "cloud";
            case REDSTONE:
                return "reddust";
        }
        return "unknown";
    }
}
