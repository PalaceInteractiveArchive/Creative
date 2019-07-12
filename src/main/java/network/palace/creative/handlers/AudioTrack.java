package network.palace.creative.handlers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.bukkit.Material;

/**
 * Created by Marc on 6/16/16
 */
public class AudioTrack {
    private String name;
    private String audioPath;
    private Material material;

    public AudioTrack(String name, String audioPath) {
        this.name = name;
        this.audioPath = audioPath;
        this.material = randomBetween();
    }

    public String getName() {
        return name;
    }

    public String getAudioPath() {
        return audioPath;
    }

    private Material randomBetween() {
        List<Material> discs = Arrays.asList(Material.MUSIC_DISC_11, Material.MUSIC_DISC_13, Material.MUSIC_DISC_BLOCKS, Material.MUSIC_DISC_CAT, Material.MUSIC_DISC_CHIRP, Material.MUSIC_DISC_FAR, Material.MUSIC_DISC_MALL, Material.MUSIC_DISC_MELLOHI, Material.MUSIC_DISC_STAL, Material.MUSIC_DISC_STRAD, Material.MUSIC_DISC_WAIT, Material.MUSIC_DISC_WARD);
        return discs.get(new Random().nextInt(discs.size()));
    }

    public Material getItem() {
        return material;
    }
}
