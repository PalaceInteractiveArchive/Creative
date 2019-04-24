package network.palace.creative.commands;

import java.util.Collection;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Marc on 5/25/15
 */
@CommandMeta(description = "Enable Night Vision", rank = Rank.DWELLER)
public class NightvisionCommand extends CoreCommand {

    public NightvisionCommand() {
        super("nv");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (player.getRank().getRankId() < Rank.DVCMEMBER.getRankId()) {
            player.sendMessage(ChatColor.RED + "You must be the " + Rank.DVCMEMBER.getFormattedName()
                    + ChatColor.RED + " rank or above to use this!");
            return;
        }
        Collection<PotionEffect> effects = player.getBukkitPlayer().getActivePotionEffects();
        boolean contains = false;
        for (PotionEffect e : effects) {
            if (e.getType().equals(PotionEffectType.NIGHT_VISION)) {
                contains = true;
                break;
            }
        }
        if (contains) {
            player.getBukkitPlayer().removePotionEffect(PotionEffectType.NIGHT_VISION);
            player.sendMessage(ChatColor.GRAY + "You no longer have Night Vision!");
        } else {
            PotionEffect effect = new PotionEffect(PotionEffectType.NIGHT_VISION, 200000, 0, true, false);
            player.getBukkitPlayer().addPotionEffect(effect);
            player.sendMessage(ChatColor.GRAY + "You now have Night Vision!");
        }
    }
}
