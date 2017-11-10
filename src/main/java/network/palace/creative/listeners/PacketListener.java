package network.palace.creative.listeners;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import network.palace.core.events.IncomingPacketEvent;
import network.palace.creative.Creative;
import network.palace.creative.packets.PacketID;
import network.palace.creative.packets.PacketMuteChat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PacketListener implements Listener {

    @EventHandler
    public void onIncomingPacket(IncomingPacketEvent event) {
        if (event.getId() != PacketID.Park.MUTECHAT.getID()) return;
        String data = event.getPacket();
        JsonObject object = (JsonObject) new JsonParser().parse(data);
        PacketMuteChat packet = new PacketMuteChat().fromJSON(object);
        Creative.getInstance().getMenuUtil().setChatMuted(packet.isMute());
    }
}
