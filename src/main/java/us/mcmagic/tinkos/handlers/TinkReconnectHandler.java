package us.mcmagic.tinkos.handlers;

import net.md_5.bungee.api.ReconnectHandler;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import us.mcmagic.tinkos.TinkOS;

/**
 * Created by Marc on 8/25/16
 */
public class TinkReconnectHandler implements ReconnectHandler {

    @Override
    public ServerInfo getServer(ProxiedPlayer player) {
        return TinkOS.getTargetLobby();
    }

    @Override
    public void setServer(ProxiedPlayer proxiedPlayer) {
    }

    @Override
    public void save() {
    }

    @Override
    public void close() {
    }
}