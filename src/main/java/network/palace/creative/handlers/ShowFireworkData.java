package network.palace.creative.handlers;

import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;

/**
 * Created by Marc on 12/26/15
 */
public class ShowFireworkData {
    private FireworkEffect.Type type;
    private List<ShowColor> colors;
    private List<ShowColor> fade;
    private boolean flicker;
    private boolean trail;

    public ShowFireworkData(Type type, List<ShowColor> colors, List<ShowColor> fade, boolean flicker, boolean trail) {
        this.type = type;
        this.colors = colors;
        this.fade = fade;
        this.flicker = flicker;
        this.trail = trail;
    }

    public FireworkEffect.Type getType() {
        return type;
    }

    public List<ShowColor> getColors() {
        return colors;
    }

    public List<ShowColor> getFade() {
        return fade;
    }

    public boolean isFlicker() {
        return flicker;
    }

    public boolean isTrail() {
        return trail;
    }

    public void setType(FireworkEffect.Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type == null ? "null" : type.name() + " " +
                String.join(",", colors.stream().map(ShowColor::name).collect(Collectors.toList())) + " " +
                String.join(",", fade.stream().map(ShowColor::name).collect(Collectors.toList())) + " "
                + (flicker ? "true" : "false") + " " + (trail ? "true" : "false");
    }

    public void setColors(List<ShowColor> color) {
        this.colors = color;
    }

    public void setFade(List<ShowColor> fade) {
        this.fade = fade;
    }

    public void setFlicker(boolean flicker) {
        this.flicker = flicker;
    }

    public void setTrail(boolean trail) {
        this.trail = trail;
    }
}
