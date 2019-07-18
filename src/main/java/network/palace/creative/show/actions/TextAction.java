package network.palace.creative.show.actions;

import java.util.Arrays;
import java.util.Collections;
import network.palace.core.utils.ItemUtil;
import network.palace.creative.show.Show;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TextAction extends ShowAction {
    public String text;

    public TextAction(Show show, Long time, String text) {
        super(show, time == null ? 0 : time);
        this.text = text;
    }

    @Override
    public void play() {
        show.displayText(text);
    }

    @Override
    public ItemStack getItem() {
        return ItemUtil.create(Material.SIGN, ChatColor.AQUA + "Text Action", Collections.singletonList(ChatColor.GREEN + "Time: " + (time / 1000) + " Text: \"" + text + ChatColor.GREEN + "\""));
    }

    @Override
    public String toString() {
        return time / 1000 + " Text " + text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
