package network.palace.creative.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

/**
 * Created by Marc on 3/27/15
 */
@CommandMeta(description = "Heal a player")
@CommandPermission(rank = Rank.SQUIRE)
public class Commandheal extends CoreCommand {

    public Commandheal() {
        super("heal");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            if (args.length == 1) {
                if (args[0].equals("**")) {
                    for (Player tp : Bukkit.getOnlinePlayers()) {
                        healPlayer(tp);
                        tp.sendMessage(ChatColor.GRAY + "You have been healed.");
                    }
                    return;
                }
                Player tp = Bukkit.getPlayer(args[0]);
                if (tp == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    return;
                }
                healPlayer(tp);
                tp.sendMessage(ChatColor.GRAY + "You have been healed.");
            }
            return;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("**")) {
                for (Player tp : Bukkit.getOnlinePlayers()) {
                    healPlayer(tp);
                    tp.sendMessage(ChatColor.GRAY + "You have been healed.");
                }
                return;
            }
            Player tp = Bukkit.getPlayer(args[0]);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            healPlayer(tp);
            player.sendMessage(ChatColor.GRAY + "You healed " + tp.getName());
            tp.sendMessage(ChatColor.GRAY + "You have been healed.");
            return;
        }
        healPlayer(player);
        player.sendMessage(ChatColor.GRAY + "You have been healed.");
    }

    public static void healPlayer(Player player) {
        player.setHealth(player.getHealthScale());
        player.setFoodLevel(20);
        player.setFireTicks(0);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }
}