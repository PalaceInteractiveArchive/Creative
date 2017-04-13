package network.palace.creative.commands;

import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CommandPermission;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WeatherType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Marc on 2/8/15
 */
@CommandMeta(description = "Set player weather")
@CommandPermission(rank = Rank.KNIGHT)
public class Commandpweather extends CoreCommand {

    public Commandpweather() {
        super("pweather");
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            if (args.length == 2) {
                Player tp = Bukkit.getPlayer(args[1]);
                if (tp == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    return;
                }
                args[0] = args[0].toLowerCase();
                switch (args[0]) {
                    case "sun":
                        tp.setPlayerWeather(WeatherType.CLEAR);
                        sender.sendMessage(ChatColor.DARK_AQUA + tp.getName()
                                + "'s " + ChatColor.GREEN
                                + "weather has been set to " + ChatColor.DARK_AQUA
                                + "Clear" + ChatColor.GREEN + "!");
                        break;
                    case "rain":
                        tp.setPlayerWeather(WeatherType.DOWNFALL);
                        sender.sendMessage(ChatColor.DARK_AQUA + tp.getName()
                                + "'s " + ChatColor.GREEN
                                + "weather has been set to " + ChatColor.DARK_AQUA
                                + "Storm" + ChatColor.GREEN + "!");
                        break;
                    case "reset":
                        tp.resetPlayerWeather();
                        sender.sendMessage(ChatColor.DARK_AQUA + tp.getName()
                                + "'s " + ChatColor.GREEN
                                + "weather now matches the server.");
                    default:
                        sender.sendMessage(ChatColor.RED
                                + "/pweather [rain/sun/reset] [Username]");
                        break;
                }
            }
            sender.sendMessage(ChatColor.RED
                    + "/pweather [rain/sun/reset] [Username]");
            return;
        }
        Player player = (Player) sender;
        if (args.length == 1) {
            args[0] = args[0].toLowerCase();
            switch (args[0]) {
                case "sun":
                    player.setPlayerWeather(WeatherType.CLEAR);
                    player.sendMessage(ChatColor.GREEN
                            + "Your weather has been set to " + ChatColor.DARK_AQUA
                            + "Clear" + ChatColor.GREEN + "!");
                    break;
                case "rain":
                    player.setPlayerWeather(WeatherType.DOWNFALL);
                    player.sendMessage(ChatColor.GREEN
                            + "Your weather has been set to " + ChatColor.DARK_AQUA
                            + "Storm" + ChatColor.GREEN + "!");
                    break;
                case "reset":
                    player.resetPlayerWeather();
                    player.sendMessage(ChatColor.GREEN
                            + "Your weather now matches the server");
                default:
                    player.sendMessage(ChatColor.RED
                            + "/pweather [rain/sun/reset] [Username]");
                    break;
            }
            return;
        }
        if (args.length == 2) {
            Player tp = Bukkit.getPlayer(args[1]);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
            args[0] = args[0].toLowerCase();
            switch (args[0]) {
                case "sun":
                    tp.setPlayerWeather(WeatherType.CLEAR);
                    player.sendMessage(ChatColor.DARK_AQUA + tp.getName() + "'s "
                            + ChatColor.GREEN + "weather has been set to "
                            + ChatColor.DARK_AQUA + "Clear" + ChatColor.GREEN + "!");
                    break;
                case "rain":
                    tp.setPlayerWeather(WeatherType.DOWNFALL);
                    player.sendMessage(ChatColor.DARK_AQUA + tp.getName() + "'s "
                            + ChatColor.GREEN + "weather has been set to "
                            + ChatColor.DARK_AQUA + "Storm" + ChatColor.GREEN + "!");
                    break;
                case "reset":
                    tp.resetPlayerWeather();
                    player.sendMessage(ChatColor.DARK_AQUA + tp.getName() + "'s "
                            + ChatColor.GREEN + "weather now matches the server.");
                default:
                    player.sendMessage(ChatColor.RED
                            + "/pweather [rain/sun/reset] [Username]");
                    break;
            }
        }
        player.sendMessage(ChatColor.RED + "/pweather [rain/sun/reset] [Username]");
    }
}
