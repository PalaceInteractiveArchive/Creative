package network.palace.creative.show.actions;

import network.palace.core.utils.ItemUtil;
import network.palace.creative.show.Show;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TextAction extends ShowAction {
    public String text;

    public TextAction(Integer id, Show show, Long time, String text) {
        super(id, show, time == null ? 0 : time);
        this.text = text;
    }

    @Override
    public void play() {
        show.displayText(text);
    }

    @Override
    public ItemStack getItem() {
        return ItemUtil.create(Material.SIGN, ChatColor.AQUA + "Text Action");
    }

    @Override
    public String toString() {
        return time / 1000 + " Text " + text;
    }

    @Override
    public String getDescription() {
        return ChatColor.GREEN + "Time: " + (time / 1000) + " Text: \"" + text + ChatColor.GREEN + "\"";
    }

    public void setText(String text) {
        this.text = text;
    }
}