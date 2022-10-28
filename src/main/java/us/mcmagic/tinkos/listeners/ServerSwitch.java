package us.mcmagic.tinkos.listeners;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import us.mcmagic.tinkos.TinkOS;
import us.mcmagic.tinkos.dashboard.packets.dashboard.PacketServerSwitch;
import us.mcmagic.tinkos.handlers.Player;

/**
 * Created by Marc on 8/19/16
 */
public class ServerSwitch implements Listener {

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        ProxiedPlayer proxied = event.getPlayer();
        proxied.sendMessage(new TextComponent(" "));
        Player tp = TinkOS.getPlayer(proxied.getUniqueId());
        if (tp == null) {
            return;
        }
        PacketServerSwitch packet = new PacketServerSwitch(tp.getUniqueId(),
                proxied.getServer() != null ? proxied.getServer().getInfo().getName() : "");
        TinkOS.dashboardConnection.send(packet);
    }
}