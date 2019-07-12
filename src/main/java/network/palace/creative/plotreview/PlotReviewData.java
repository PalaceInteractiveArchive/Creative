package network.palace.creative.plotreview;

import com.github.intellectualsites.plotsquared.plot.object.PlotId;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.World;

@AllArgsConstructor
public class PlotReviewData {

    private final UUID uuid;
    @Getter
    private final PlotId plotId;
    @Getter
    private final World world;

    public UUID getUUID() {
        return uuid;
    }
}
