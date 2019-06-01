package network.palace.creative.show.handlers;

import com.github.intellectualsites.plotsquared.plot.PlotSquared;
import com.github.intellectualsites.plotsquared.plot.object.Plot;
import com.github.intellectualsites.plotsquared.plot.object.PlotId;
import com.github.intellectualsites.plotsquared.plot.object.PlotPlayer;
import lombok.Getter;
import network.palace.audio.handlers.AudioArea;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.creative.Creative;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Created by Marc on 6/16/16
 */
@Getter
public class PlotArea extends AudioArea {
    private PlotId plotId;
    private CPlayer owner;

    public PlotArea(PlotId plotId, CPlayer owner, String soundname, World world) {
        super(owner.getUniqueId().toString(), soundname, 750, 1.0, null, true, false, world);
        this.plotId = plotId;
        this.owner = owner;
        for (CPlayer p : Core.getPlayerManager().getOnlinePlayers()) {
            if (p == null || p.getBukkitPlayer() == null)
                continue;
            Plot pl = PlotPlayer.wrap(p.getBukkitPlayer()).getCurrentPlot();
            if (pl == null) {
                continue;
            }
            if (pl.getId().equals(plotId)) {
                addPlayer(p);
            }
        }
    }

    @Override
    public String getRegionName() {
        return "Plot ID " + plotId.toString();
    }

    @Override
    public boolean locIsInArea(Location loc) {
        Plot plot = PlotSquared.get().getPlotAreaAbs(Creative.wrapLocation(loc)).getPlot(Creative.wrapLocation(loc));
        return plot != null && plot.getId().equals(plotId);
    }
}
