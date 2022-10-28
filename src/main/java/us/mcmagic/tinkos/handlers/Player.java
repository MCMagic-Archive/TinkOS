package us.mcmagic.tinkos.handlers;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import us.mcmagic.tinkos.TinkOS;

import java.util.UUID;

/**
 * Created by Marc on 7/16/16
 */
public class Player {
    private UUID uuid;
    private String username;
    private Rank rank;
    private String address;
    private boolean kicking = false;

    public Player(UUID uuid, String username, Rank rank, String address) {
        this.uuid = uuid;
        this.username = username;
        this.rank = rank;
        this.address = address;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return username;
    }

    public void sendMessage(String message) {
        ProxiedPlayer p = TinkOS.getProxyServer().getPlayer(uuid);
        if (p != null) {
            p.sendMessage(TextComponent.fromLegacyText(message));
        }
    }

    public void sendMessage(TextComponent message) {
        ProxiedPlayer p = TinkOS.getProxyServer().getPlayer(uuid);
        if (p != null) {
            p.sendMessage(message);
        }
    }

    public void sendMessage(BaseComponent[] components) {
        ProxiedPlayer p = TinkOS.getProxyServer().getPlayer(uuid);
        if (p != null) {
            p.sendMessage(components);
        }
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public void kickPlayer(String reason) {
        if (kicking) {
            return;
        }
        kicking = true;
        BaseComponent[] r = new ComponentBuilder("You have been disconnected for: ").color(ChatColor.RED)
                .append(reason).color(ChatColor.AQUA).create();
        TinkOS.getProxyServer().getPlayer(uuid).disconnect(r);
        TinkOS.removePlayer(uuid);
    }

    public void kickPlayer(TextComponent reason) {
        if (kicking) {
            return;
        }
        kicking = true;
        TinkOS.getProxyServer().getPlayer(uuid).disconnect(reason);
        TinkOS.removePlayer(uuid);
    }

    public void kickPlayer(BaseComponent[] reason) {
        if (kicking) {
            return;
        }
        kicking = true;
        TinkOS.getProxyServer().getPlayer(uuid).disconnect(reason);
        TinkOS.removePlayer(uuid);
    }

    public boolean isKicking() {
        return kicking;
    }

    public Server getServer() {
        ProxiedPlayer p = TinkOS.getProxyServer().getPlayer(uuid);
        if (p != null) {
            return p.getServer();
        } else {
            return null;
        }
    }

    public String getAddress() {
        return address;
    }

    public String getServerName() {
        try {
            ServerInfo info = getServer().getInfo();
            return info.getName();
        } catch (Exception e) {
            return "Unknown";
        }
    }

    public void chat(String msg) {
        ProxiedPlayer p = TinkOS.getProxyServer().getPlayer(uuid);
        if (p != null) {
            p.chat(msg);
        }
    }
}