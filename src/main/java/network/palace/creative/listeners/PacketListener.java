package network.palace.creative.listeners;

import com.google.gson.JsonObject;
import network.palace.core.events.IncomingMessageEvent;
import network.palace.creative.Creative;
import network.palace.creative.packets.ChatMutePacket;
import network.palace.creative.packets.IgnoreListPacket;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PacketListener implements Listener {

    @EventHandler
    public void onIncomingMessage(IncomingMessageEvent event) {
        JsonObject object = event.getPacket();
        if (!object.has("id")) return;
        int id = object.get("id").getAsInt();
        switch (id) {
            case 11: {
                IgnoreListPacket packet = new IgnoreListPacket(object);
                List<UUID> list = new ArrayList<>();
                for (UUID uuid : packet.getPlayers()) {
                    try {
                        list.add(uuid);
                    } catch (IllegalArgumentException ignored) {
                    }
                }
                Creative.getInstance().getIgnoreUtil().addData(packet.getUuid(), list);
                break;
            }
            case 17: {
                ChatMutePacket packet = new ChatMutePacket(object);
                String channel = packet.getChannel();
                if (!channel.equals("Creative")) return;
                Creative.getInstance().getMenuUtil().setChatMuted(packet.isMuted());
                break;
            }
        }
    }
}
