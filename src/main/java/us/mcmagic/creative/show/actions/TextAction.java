package us.mcmagic.creative.show.actions;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import us.mcmagic.creative.show.Show;
import us.mcmagic.mcmagiccore.itemcreator.ItemCreator;

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
        return new ItemCreator(Material.SIGN, ChatColor.AQUA + "Text Action");
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