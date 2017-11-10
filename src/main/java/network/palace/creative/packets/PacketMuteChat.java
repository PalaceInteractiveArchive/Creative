package network.palace.creative.packets;

import com.google.gson.JsonObject;
import network.palace.core.dashboard.packets.BasePacket;

/**
 * Created by Marc on 9/18/16
 */
public class PacketMuteChat extends BasePacket {
    private String server;
    private boolean mute;
    private String source;

    public PacketMuteChat() {
        this("", false, "");
    }

    public PacketMuteChat(String server, boolean mute, String source) {
        super(PacketID.Park.MUTECHAT.getID());
        this.server = server;
        this.mute = mute;
        this.source = source;
    }

    public String getServer() {
        return server;
    }

    public boolean isMute() {
        return mute;
    }

    public String getSource() {
        return source;
    }

    public PacketMuteChat fromJSON(JsonObject obj) {
        this.id = obj.get("id").getAsInt();
        this.server = obj.get("server").getAsString();
        this.mute = obj.get("mute").getAsBoolean();
        this.source = obj.get("source").getAsString();
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", this.id);
        obj.addProperty("server", this.server);
        obj.addProperty("mute", this.mute);
        obj.addProperty("source", this.source);
        return obj;
    }
}