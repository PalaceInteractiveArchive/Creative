package network.palace.creative.commands;

import network.palace.core.Core;
import network.palace.core.command.CommandException;
import network.palace.core.command.CommandMeta;
import network.palace.core.command.CoreCommand;
import network.palace.core.player.CPlayer;
import network.palace.core.player.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandMeta(description = "Set the movement speed of a player", rank = Rank.TRAINEEBUILD)
public class SpeedCommand extends CoreCommand {

    public SpeedCommand() {
        super("speed");
    }

    @Override
    protected void handleCommand(CPlayer player, String[] args) throws CommandException {
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/speed [speed]" + (player.getRank().getRankId() >= Rank.CM.getRankId() ? " <player>" : ""));
            return;
        }
        CPlayer target;
        if (args.length > 1 && player.getRank().getRankId() >= Rank.CM.getRankId()) {
            target = Core.getPlayerManager().getPlayer(args[1]);
        } else {
            target = player;
        }
        setSpeed(player.getBukkitPlayer(), target, args[0]);
    }

    @Override
    protected void handleCommandUnspecific(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "/speed [speed] [player]");
            return;
        }
        setSpeed(sender, Core.getPlayerManager().getPlayer(args[1]), args[0]);
    }

    private void setSpeed(CommandSender sender, CPlayer target, String speedString) {
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return;
        }
        boolean isFlying = target.getRank().getRankId() >= Rank.VIP.getRankId() && target.isFlying();
        float speed = getMoveSpeed(speedString);
        if (isFlying) {
            target.setFlySpeed(getRealMoveSpeed(speed, true));
        } else {
            target.setWalkSpeed(getRealMoveSpeed(speed, false));
        }
        sender.sendMessage(ChatColor.GREEN + "Set " +
                (((sender instanceof Player) && ((Player) sender).getUniqueId().equals(target.getUniqueId())) ? "your" : (target.getName() + "'s"))
                + " " + (isFlying ? "flying" : "walking") + " speed to " + speed);
    }

    /**
     * Convert string speed to floating point value
     *
     * @param moveSpeed the speed in string format
     * @return a float representing the movement speed
     */
    private float getMoveSpeed(final String moveSpeed) {
        float userSpeed;
        try {
            userSpeed = Float.parseFloat(moveSpeed);
            if (userSpeed > 10f) {
                userSpeed = 10f;
            } else if (userSpeed < 0f) {
                userSpeed = 0f;
            }
        } catch (NumberFormatException e) {
            return 1;
        }
        return userSpeed;
    }

    /**
     * Convert a 0.0-10.0 float to the Minecraft-scale for walk/fly speed
     *
     * @param userSpeed the 0.0-10.0 float
     * @param isFly     whether this is flight speed
     * @return movement speed scaled for Minecraft
     */
    private float getRealMoveSpeed(float userSpeed, boolean isFly) {
        final float defaultSpeed = isFly ? 0.1f : 0.2f;
        float maxSpeed = 1f;

        if (userSpeed < 1f) {
            return defaultSpeed * userSpeed;
        } else {
            float ratio = ((userSpeed - 1) / 9) * (maxSpeed - defaultSpeed);
            return ratio + defaultSpeed;
        }
    }
}
