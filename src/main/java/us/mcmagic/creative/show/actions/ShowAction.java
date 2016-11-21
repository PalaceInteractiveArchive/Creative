package us.mcmagic.creative.show.actions;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.creative.show.Show;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public abstract class ShowAction {
    public Integer id;
    public Show show;
    public long time;

    public ShowAction(Integer id, Show show, Long time) {
        this.id = id;
        this.show = show;
        this.time = time;
    }

    public Integer getId() {
        return id;
    }

    public abstract void play();

    public abstract ItemStack getItem();

    @Override
    public abstract String toString();

    public void setTime(double time) {
        this.time = (long) (time * 1000);
    }

    String strLoc(Location loc) {
        return "x: " + round(loc.getX()) + ", y: " + round(loc.getY()) + ", z: " + round(loc.getZ());
    }

    String caps(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    protected String round(double x) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        return df.format(x);
    }

    public abstract String getDescription();
}