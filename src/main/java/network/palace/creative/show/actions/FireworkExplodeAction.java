package network.palace.creative.show.actions;

import network.palace.creative.show.Show;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Marc on 7/1/15
 */
public class FireworkExplodeAction extends ShowAction {
    private final Firework fw;

    public FireworkExplodeAction(Show show, Long time, Firework fw) {
        super(show, time == null ? 0 : time);
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
}
