package network.palace.creative.handlers;

import org.bukkit.Particle;

import java.util.UUID;

/**
 * Created by Marc on 11/16/15
 */
public class PlayerData {
    private UUID uuid;
    private Particle particle;
    private boolean rptag = false;
    private boolean showCreator = false;
    private int rplimit;
    private boolean creator = false;
    private boolean creatorTag;
    private long onlineTime = 0;
    private long lastAction = 0;
    private boolean isAFK = false;
    private String resourcePack = "";

    public PlayerData(UUID uuid, Particle particle, boolean rptag, boolean showCreator, int rplimit,
                      boolean creator, boolean creatorTag, String resourcePack) {
        this.uuid = uuid;
        this.particle = particle;
        this.rptag = rptag;
        this.showCreator = showCreator;
        this.rplimit = rplimit;
        this.creator = creator;
        this.creatorTag = creatorTag;
        this.resourcePack = resourcePack;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public Particle getParticle() {
        return particle;
    }

    public boolean hasRPTag() {
        return rptag;
    }

    public boolean hasShowCreator() {
        return showCreator;
    }

    public boolean isCreator() {
        return creator;
    }

    public boolean hasCreatorTag() {
        return creatorTag;
    }

    public void setCreator(boolean creator) {
        this.creator = creator;
    }

    public int getRPLimit() {
        return rplimit;
    }

    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    public void setRPLimit(int rplimit) {
        this.rplimit = rplimit;
    }

    public void setHasRPTag(boolean hasRPTag) {
        this.rptag = hasRPTag;
    }

    public void setHasShowCreator(boolean b) {
        this.showCreator = b;
    }

    public void setCreatorTag(boolean creatorTag) {
        this.creatorTag = creatorTag;
    }

    public void addOnlineTime(long amount) {
        this.onlineTime += amount;
    }

    public void setOnlineTime(long onlineTime) {
        this.onlineTime = onlineTime;
    }

    public long getOnlineTime() {
        return onlineTime;
    }

    public boolean isAFK() {
        return isAFK;
    }

    public void addLastAction(int amount) {
        this.lastAction += amount;
    }

    public long getLastAction() {
        return lastAction;
    }

    public void setAFK(boolean b) {
        this.isAFK = b;
    }

    public void resetAction() {
        this.lastAction = 0;
    }

    public String getResourcePack() {
        return resourcePack;
    }

    public void setResourcePack(String resourcePack) {
        this.resourcePack = resourcePack;
    }
}