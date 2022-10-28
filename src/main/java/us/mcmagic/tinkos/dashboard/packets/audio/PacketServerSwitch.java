package us.mcmagic.tinkos.dashboard.packets.audio;

import com.google.gson.JsonObject;
import us.mcmagic.tinkos.dashboard.packets.BasePacket;
import us.mcmagic.tinkos.dashboard.packets.PacketID;

/**
 * Created by Marc on 6/15/15
 */
public class PacketServerSwitch extends BasePacket {
    private String servername = "";

    public PacketServerSwitch(String servername) {
        this.id = PacketID.SERVER_SWITCH.getID();

        this.servername = servername;
    }

    public String getServer() {
        return this.servername;
    }

    public PacketServerSwitch fromJSON(JsonObject obj) {
        try {
            this.servername = obj.get("servername").getAsString();
        } catch (Exception e) {
            return null;
        }
        return this;
    }

    public JsonObject getJSON() {
        JsonObject obj = new JsonObject();
        try {
            obj.addProperty("id", this.id);
            obj.addProperty("servername", this.servername);
        } catch (Exception e) {
            return null;
        }
        return obj;
    }
}