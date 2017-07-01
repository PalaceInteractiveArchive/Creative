package network.palace.creative.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import network.palace.core.Core;
import network.palace.core.player.CPlayer;
import network.palace.creative.Creative;
import network.palace.creative.handlers.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

/**
 * Created by Marc on 6/16/16
 */
public class OnlineUtil {

    public OnlineUtil() {
        Bukkit.getScheduler().runTaskTimer(Creative.getInstance(), () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                PlayerData data = Creative.getInstance().getPlayerData(p.getUniqueId());
                data.addLastAction(1);
                if (data.getLastAction() >= 600 && !data.isAFK()) {
                    data.setAFK(true);
                } else if (data.isAFK()) {
                    data.setAFK(false);
                }
            }
        }, 0L, 20L);
        // $10 every 30 minutes
        Bukkit.getScheduler().runTaskTimer(Creative.getInstance(), () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                PlayerData data = Creative.getInstance().getPlayerData(p.getUniqueId());
                if (data.isAFK()) {
                    continue;
                }
                if (data.getOnlineTime() >= 1800) {
                    Core.getEconomy().addBalance(p.getUniqueId(), 5, "Creative Online Time");
                    p.sendMessage(ChatColor.GREEN + "You received " + ChatColor.AQUA + "$5 and 2 Honor " + ChatColor.GREEN +
                            "for playing on Creative for 30 minutes!");
                    data.setOnlineTime(0);
                    CPlayer cp = Core.getPlayerManager().getPlayer(p);
                    if (cp == null) {
                        Bukkit.getLogger().severe("Player is null: " + p.getName() + " " + p.getUniqueId());
                    } else {
                        Core.runTaskAsynchronously(() -> cp.giveHonor(2));
                    }
                } else {
                    data.addOnlineTime(10);
                }
            }
        }, 0L, 200L);
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Creative.getInstance(),
                PacketType.Play.Client.POSITION, PacketType.Play.Client.POSITION_LOOK, PacketType.Play.Client.LOOK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                PacketContainer p = event.getPacket();
                boolean moved = false;
                if (p.getType().equals(PacketType.Play.Client.POSITION_LOOK) || p.getType().equals(PacketType.Play.Client.LOOK)) {
                    StructureModifier<Float> floats = p.getFloat();
                    float yaw = 360;
                    float pitch = 360;
                    Field yawF = floats.getField(0);
                    Field pitchF = floats.getField(1);
                    yawF.setAccessible(true);
                    pitchF.setAccessible(true);
                    try {
                        yaw = (float) yawF.get(floats.getTarget());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    try {
                        pitch = (float) pitchF.get(floats.getTarget());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    if (yaw != 360 && pitch != 360) {
                        Location l = player.getLocation();
                        if (yaw != l.getYaw() || pitch != l.getPitch()) {
                            moved = true;
                        }
                    }
                }
                if (!moved && !p.getType().equals(PacketType.Play.Client.LOOK)) {
                    StructureModifier<Double> doubles = p.getDoubles();
                    double x = -10;
                    double y = -10;
                    double z = -10;
                    Field xF = doubles.getField(0);
                    Field yF = doubles.getField(1);
                    Field zF = doubles.getField(2);
                    xF.setAccessible(true);
                    yF.setAccessible(true);
                    zF.setAccessible(true);
                    try {
                        x = (double) xF.get(doubles.getTarget());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    try {
                        y = (double) yF.get(doubles.getTarget());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    try {
                        z = (double) zF.get(doubles.getTarget());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    if (x != -10 && y != -10 && z != -10) {
                        Location l = player.getLocation();
                        if (x != l.getX() || y != l.getY() || z != l.getZ()) {
                            moved = true;
                        }
                    }
                }
                if (moved) {
                    PlayerData data = Creative.getInstance().getPlayerData(player.getUniqueId());
                    if (data != null) {
                        data.resetAction();
                    }
                }
            }
        });
    }
}