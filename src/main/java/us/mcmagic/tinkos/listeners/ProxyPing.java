package us.mcmagic.tinkos.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import us.mcmagic.tinkos.TinkOS;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marc on 8/24/16
 */
public class ProxyPing implements Listener {

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onProxyPing(ProxyPingEvent event) {
        boolean tracker = event.getConnection().getAddress().getAddress().toString().replaceAll("/", "").equals("198.24.160.154");
        List<String> info = new ArrayList<>(TinkOS.getInfo());
        ServerPing.PlayerInfo[] infolist = new ServerPing.PlayerInfo[info.size()];
        for (int i = 0; i < info.size(); i++) {
            infolist[i] = new ServerPing.PlayerInfo(ChatColor.translateAlternateColorCodes('&', info.get(i)), "");
        }
        ServerPing response = event.getResponse();
        if (TinkOS.isMaintenance()) {
            event.setResponse(new ServerPing(response.getVersion(), new ServerPing.Players(0, 0, infolist),
                    ChatColor.translateAlternateColorCodes('&', TinkOS.getMOTDMmaintenance()),
                    response.getFaviconObject()));
        } else {
            event.setResponse(new ServerPing(response.getVersion(), new ServerPing.Players(2000,
                    tracker ? TinkOS.getProxyServer().getOnlineCount() : TinkOS.getOnlineCount(), infolist),
                    ChatColor.translateAlternateColorCodes('&', TinkOS.getMOTD()), response.getFaviconObject()));
        }
    }
}