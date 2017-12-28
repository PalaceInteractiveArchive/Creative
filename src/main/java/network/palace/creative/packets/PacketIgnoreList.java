package network.palace.creative.packets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import network.palace.core.dashboard.packets.BasePacket;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 8/22/16
 */
public class PacketIgnoreList extends BasePacket {
    private UUID uuid;
    @Getter private List<String> ignoreList = new ArrayList<>();

    public PacketIgnoreList() {
        this(null, new ArrayList<>());
    }

    public PacketIgnoreList(UUID uuid, List<String> ignoreList) {
        super(PacketID.Dashboard.LISTFRIENDCOMMAND.getID());
        this.uuid = uuid;
        this.ignoreList = ignoreList;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public PacketIgnoreList fromJSON(JsonObject obj) {
        try {
            this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        } catch (Exception e) {
            this.uuid = null;
        }
        JsonArray list = obj.get("ignoreList").getAsJsonArray();
        for (JsonElement e : list) {
            this.ignoreList.add(e.getAsString());
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("uuid", this.uuid.toString());
            Gson gson = new Gson();
            obj.add("ignoreList", gson.toJsonTree(this.ignoreList).getAsJsonArray());
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}