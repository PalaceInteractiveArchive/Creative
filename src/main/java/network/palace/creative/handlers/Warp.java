package network.palace.creative.handlers;

import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Warp {
    private String name;
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    private Rank rank;
    private String world;

    public Warp(String name, double x, double y, double z, float yaw, float pitch, String world, Rank rank) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.world = world;
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public Rank getRank() {
        return rank;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public World getWorld() {
        return Bukkit.getWorld(world);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public Location getLocation() {
        return new Location(getWorld(), x, y, z, yaw, pitch);
    }
}
