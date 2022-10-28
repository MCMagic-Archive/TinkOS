package us.mcmagic.tinkos.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import us.mcmagic.tinkos.TinkOS;
import us.mcmagic.tinkos.handlers.Player;

/**
 * Created by Marc on 9/17/16
 */
public class ServerKick implements Listener {

    @EventHandler
    public void onServerKick(ServerKickEvent event) {
        Player player = TinkOS.getPlayer(event.getPlayer().getUniqueId());
        ServerInfo server = event.getKickedFrom();
        String name = server.getName();
        if (event.getState().equals(ServerKickEvent.State.CONNECTED)) {
            event.setCancelled(true);
            player.sendMessage(new ComponentBuilder("The server you were previously on (").color(ChatColor.RED)
                    .append(server.getName()).color(ChatColor.AQUA).append(") has disconnected you with the reason (\"")
                    .color(ChatColor.RED).append(event.getKickReasonComponent()[0].toPlainText()).color(ChatColor.AQUA)
                    .append("\")").color(ChatColor.RED).create());
            event.setCancelServer(getFallbackServer(server.getName().toLowerCase()));
        }
    }

    private ServerInfo getFallbackServer(String name) {
        if (name.startsWith("ttc")) {
            return TinkOS.getProxyServer().getServerInfo("Arcade");
        }
        return TinkOS.getProxyServer().getServerInfo("TTC1");
    }
}