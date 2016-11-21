package us.mcmagic.creative.commands;

import org.bukkit.ChatColor;
import org.bukkit.WeatherType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.mcmagic.mcmagiccore.player.PlayerUtil;

/**
 * Created by Marc on 2/8/15
 */
public class Commandpweather implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length == 2) {
                Player tp = PlayerUtil.findPlayer(args[1]);
                if (tp == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found!");
                    return true;
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
            return true;
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
            return true;
        }
        if (args.length == 2) {
            Player tp = PlayerUtil.findPlayer(args[1]);
            if (tp == null) {
                player.sendMessage(ChatColor.RED + "Player not found!");
                return true;
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
        return true;
    }
}
