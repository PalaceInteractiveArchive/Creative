package network.palace.creative.show.handlers;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotId;
import lombok.Getter;
import network.palace.audio.handlers.AudioArea;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Collections;

/**
 * Created by Marc on 6/16/16
 */
@Getter
public class PlotArea extends AudioArea {
    private PlotId plotId;
    private CPlayer owner;

    public PlotArea(PlotId plotId, CPlayer owner, String soundname, World world) {
        super(owner.getUniqueId().toString(), soundname, 750, 1.0, Collections.emptyList(), true, false, world);
        this.plotId = plotId;
        this.owner = owner;
        PlotAPI api = new PlotAPI();
        for (CPlayer p : Core.getPlayerManager().getOnlinePlayers()) {
            if (p == null || p.getBukkitPlayer() == null)
                continue;
            Plot pl = api.getPlot(p.getBukkitPlayer());
            if (pl == null) {
                continue;
            }
            if (pl.getId().equals(plotId)) {
                addPlayer(p);
            }
        }
    }

    @Override
    public String getRegionNames() {
        return "Plot ID " + plotId.toString();
    }

    @Override
    public boolean locIsInArea(Location loc) {
        PlotAPI api = new PlotAPI();
        Plot plot = api.getPlot(loc);
        return plot != null && plot.getId().equals(plotId);
    }
}
