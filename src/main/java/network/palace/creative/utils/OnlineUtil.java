package network.palace.creative.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import network.palace.core.Core;
import network.palace.creative.Creative;
import network.palace.creative.handlers.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
                    Core.getEconomy().addBalance(p.getUniqueId(), 10, "Creative Online Time");
                    p.sendMessage(ChatColor.GREEN + "You received " + ChatColor.AQUA + "$10 " + ChatColor.GREEN +
                            "for playing on Creative for 30 minutes!");
                    data.setOnlineTime(0);
                } else {
                    data.addOnlineTime(10);
                }
            }
        }, 0L, 200L);
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Creative.getInstance(),
                PacketType.Play.Client.POSITION, PacketType.Play.Client.POSITION_LOOK, PacketType.Play.Client.LOOK) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Creative.getInstance().getPlayerData(event.getPlayer().getUniqueId()).resetAction();
            }
        });
    }
}