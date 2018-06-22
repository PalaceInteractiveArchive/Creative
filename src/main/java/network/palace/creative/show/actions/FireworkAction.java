package network.palace.creative.show.actions;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;
import network.palace.core.utils.ItemUtil;
import network.palace.creative.handlers.ShowColor;
import network.palace.creative.handlers.ShowFireworkData;
import network.palace.creative.show.Show;
import org.bukkit.ChatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Firework;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkAction extends ShowAction implements Listener {
    private Show show;
    public Location loc;
    public ShowFireworkData showData;
    public int power;

    public FireworkAction(Integer id, Show show, Long time, Location loc, ShowFireworkData data, int power) {
        super(id, show, time == null ? 0 : time);
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        this.show = show;
        this.loc = new Location(loc.getWorld(), Double.parseDouble(df.format(loc.getX())),
                Double.parseDouble(df.format(loc.getY())), Double.parseDouble(df.format(loc.getZ())));
        this.showData = data;
        this.power = power;
    }

    @Override
    public void play() {
        try {
            playFirework();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playFirework() throws Exception {
        final Firework fw = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta data = fw.getFireworkMeta();
        data.clearEffects();
        // Add effect
        data.addEffect(FireworkEffect.builder().with(showData.getType()).withColor(showData.getColors().stream().map(ShowColor::getColor).collect(Collectors.toList()))
                .withFade(showData.getFade().stream().map(ShowColor::getColor).collect(Collectors.toList())).flicker(showData.isFlicker()).trail(showData.isTrail()).build());
        // Instant
        boolean instaburst;
        if (power == 0) {
            instaburst = true;
        } else {
            instaburst = false;
            data.setPower(Math.min(1, power));
        }
        // Set data
        fw.setFireworkMeta(data);
        if (instaburst) {
            FireworkExplodeAction explode = new FireworkExplodeAction(id + 1, show, time + 50, fw);
            show.actions.add(explode);
        }
    }

    public ShowFireworkData getShowData() {
        return showData;
    }

    public boolean isFlicker() {
        return showData.isFlicker();
    }

    public boolean isTrail() {
        return showData.isTrail();
    }

    @Override
    public ItemStack getItem() {
        return ItemUtil.create(Material.FIREWORK, ChatColor.AQUA + "Firework Action");
    }

    @Override
    public String toString() {
        return time / 1000 + " Firework " + loc.getX() + "," + loc.getY() + "," + loc.getZ() + " " +
                power + " " + showData.toString();
    }

    @Override
    public String getDescription() {
        return ChatColor.GREEN + "Time: " + (time / 1000) + " Type: " + showData.getType().name() +
                "BREAK" + ChatColor.GREEN + "Colors: " + String.join(", ", showData.getColors().stream().map(ShowColor::name).collect(Collectors.toList())) +
                "BREAK" + ChatColor.GREEN + "Fade: " + String.join(", ", showData.getFade().stream().map(ShowColor::name).collect(Collectors.toList())) +
                "BREAK" + ChatColor.GREEN + "Flicker: " + showData.isFlicker() + " Trail: " + showData.isTrail();
    }

    public void setType(FireworkEffect.Type type) {
        this.showData.setType(type);
    }

    public void setColors(List<ShowColor> color) {
        this.showData.setColors(color);
    }

    public void setFade(List<ShowColor> fade) {
        this.showData.setFade(fade);
    }

    public void setPower(int power) {
        this.power = power;
    }
}
