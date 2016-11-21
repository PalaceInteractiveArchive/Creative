package us.mcmagic.creative.show.handlers;

import org.bukkit.Material;

import java.util.Random;

/**
 * Created by Marc on 6/16/16
 */
public class AudioTrack {
    private String name;
    private String audioPath;
    private int itemID;

    public AudioTrack(String name, String audioPath) {
        this.name = name;
        this.audioPath = audioPath;
        this.itemID = randomBetween(2256, 2267);
    }

    public String getName() {
        return name;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public int getItemID() {
        return itemID;
    }

    private static int randomBetween(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    @SuppressWarnings("deprecation")
    public Material getItem() {
        return Material.getMaterial(itemID);
    }
}