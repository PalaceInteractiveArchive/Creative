package network.palace.creative.show.actions;

import lombok.Getter;
import lombok.Setter;
import network.palace.creative.show.Show;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public abstract class ShowAction {
    public Show show;
    public long time;
    @Getter @Setter protected boolean needsLocationUpdate = false;

    public ShowAction(Show show, Long time) {
        this.show = show;
        this.time = time;
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
}
