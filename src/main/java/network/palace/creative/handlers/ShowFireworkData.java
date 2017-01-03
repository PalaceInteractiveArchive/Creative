package network.palace.creative.handlers;

import org.bukkit.FireworkEffect;

/**
 * Created by Marc on 12/26/15
 */
public class ShowFireworkData {
    private FireworkEffect.Type type;
    private ShowColor color;
    private ShowColor fade;
    private boolean flicker;
    private boolean trail;

    public ShowFireworkData(FireworkEffect.Type type, ShowColor color, ShowColor fade, boolean flicker, boolean trail) {
        this.type = type;
        this.color = color;
        this.fade = fade;
        this.flicker = flicker;
        this.trail = trail;
    }

    public FireworkEffect.Type getType() {
        return type;
    }

    public ShowColor getColor() {
        return color;
    }

    public ShowColor getFade() {
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
        return type == null ? "null" : type.name() + " " + color.toString() + " " + fade.toString() + " " + (flicker ? "true" : "false") + " " +
                (trail ? "true" : "false");
    }

    public void setColor(ShowColor color) {
        this.color = color;
    }

    public void setFade(ShowColor fade) {
        this.fade = fade;
    }

    public void setFlicker(boolean flicker) {
        this.flicker = flicker;
    }

    public void setTrail(boolean trail) {
        this.trail = trail;
    }
}