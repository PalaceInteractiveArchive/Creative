package network.palace.creative.listeners;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import network.palace.core.events.IncomingPacketEvent;
import network.palace.creative.Creative;
import network.palace.creative.packets.PacketIgnoreList;
import network.palace.creative.packets.PacketMuteChat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PacketListener implements Listener {

    @EventHandler
    public void onIncomingPacket(IncomingPacketEvent event) {
        String data = event.getPacket();
        JsonObject object = (JsonObject) new JsonParser().parse(data);
        switch (event.getId()) {
            case 61: {
                PacketMuteChat packet = new PacketMuteChat().fromJSON(object);
                Creative.getInstance().getMenuUtil().setChatMuted(packet.isMute());
                break;
            }
            case 72: {
                PacketIgnoreList packet = new PacketIgnoreList().fromJSON(object);
                List<UUID> list = new ArrayList<>();
                for (String s : packet.getIgnoreList()) {
                    try {
                        list.add(UUID.fromString(s));
                    } catch (IllegalArgumentException ignored) {
                    }
                }
                Creative.getInstance().getIgnoreUtil().addData(packet.getUniqueId(), list);
                break;
            }
        }
    }
}
