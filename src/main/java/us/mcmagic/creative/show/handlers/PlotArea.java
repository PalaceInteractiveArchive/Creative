package us.mcmagic.creative.show.handlers;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotId;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import us.mcmagic.creative.Creative;
import us.mcmagic.mcmagiccore.audioserver.AudioArea;

/**
 * Created by Marc on 6/16/16
 */
public class PlotArea extends AudioArea {
    private PlotId plotId;
    private Player owner;

    @SuppressWarnings("deprecation")
    public PlotArea(PlotId plotId, Player owner, String soundname, World world) {
        super(owner.getName(), soundname, 750, 1.0, "default", null, true, false, 0, false, world);
        this.plotId = plotId;
        this.owner = owner;
        PlotAPI api = new PlotAPI(Creative.getInstance());
        for (Player p : Bukkit.getOnlinePlayers()) {
            Plot pl = api.getPlot(p);
            if (pl == null) {
                continue;
            }
            if (pl.getId().equals(plotId)) {
                addPlayer(p);
            }
        }
    }

    public Player getOwner() {
        return owner;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean locIsInArea(Location loc) {
        PlotAPI api = new PlotAPI(Creative.getInstance());
        Plot plot = api.getPlot(loc);
        return plot != null && plot.getId().equals(plotId);
    }
}
