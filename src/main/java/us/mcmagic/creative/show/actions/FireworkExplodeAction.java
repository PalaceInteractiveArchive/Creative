package us.mcmagic.creative.show.actions;

import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.creative.show.Show;

/**
 * Created by Marc on 7/1/15
 */
public class FireworkExplodeAction extends ShowAction {
    private final Firework fw;

    public FireworkExplodeAction(Integer id, Show show, Long time, Firework fw) {
        super(id, show, time == null ? 0 : time);
        this.fw = fw;
    }

    @Override
    public void play() {
        fw.detonate();
    }

    @Override
    public ItemStack getItem() {
        return null;
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public String getDescription() {
        return null;
    }
}