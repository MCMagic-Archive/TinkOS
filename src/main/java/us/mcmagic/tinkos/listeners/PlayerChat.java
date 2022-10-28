package us.mcmagic.tinkos.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import us.mcmagic.tinkos.TinkOS;

public class PlayerChat implements Listener {

    @EventHandler
    public void onPlayerChat(ChatEvent event) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        TinkOS.dashboardConnection.playerChat(player.getUniqueId(), event.getMessage());
        event.setCancelled(true);
    }
}