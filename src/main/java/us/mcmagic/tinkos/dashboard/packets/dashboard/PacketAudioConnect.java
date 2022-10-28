package us.mcmagic.tinkos.dashboard.packets.dashboard;

import com.google.gson.JsonObject;
import us.mcmagic.tinkos.dashboard.packets.BasePacket;
import us.mcmagic.tinkos.dashboard.packets.PacketID;

import java.util.UUID;

/**
 * Created by Marc on 9/17/16
 */
public class PacketAudioConnect extends BasePacket {
    private UUID uuid;

    public PacketAudioConnect() {
        this(null);
    }

    public PacketAudioConnect(UUID uuid) {
        this.id = PacketID.Dashboard.AUDIOCONNECT.getID();
        this.uuid = uuid;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public PacketAudioConnect fromJSON(JsonObject obj) {
        try {
            this.uuid = UUID.fromString(obj.get("uuid").getAsString());
        } catch (Exception e) {
            this.uuid = null;
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("uuid", this.uuid.toString());
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}
