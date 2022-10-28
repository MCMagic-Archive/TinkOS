package us.mcmagic.tinkos;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import us.mcmagic.tinkos.dashboard.DashboardConnection;
import us.mcmagic.tinkos.dashboard.packets.dashboard.PacketPlayerDisconnect;
import us.mcmagic.tinkos.dashboard.packets.dashboard.PacketPlayerList;
import us.mcmagic.tinkos.handlers.Player;
import us.mcmagic.tinkos.handlers.TinkReconnectHandler;
import us.mcmagic.tinkos.listeners.*;
import us.mcmagic.tinkos.utils.SqlUtil;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TinkOS extends Plugin {
    private static TinkOS instance;
    private static Configuration config;
    private static ProxyServer server;
    private static HashMap<UUID, Player> players = new HashMap<>();
    private static long startTime = System.currentTimeMillis();
    public static boolean canJoin = false;
    public static DashboardConnection dashboardConnection;
    public static SqlUtil sqlUtil;
    private static boolean maintenance = false;
    private static List<UUID> maintenanceWhitelist = new ArrayList<>();
    private static String motd;
    private static String motdmaintenance;
    private static List<String> info = new ArrayList<>();
    private static ServerInfo targetLobby = null;
    private static int onlineCount = 0;
    private static List<String> commands = new ArrayList<>();

    @Override
    public void onEnable() {
        this.instance = this;
        try {
            File file = new File("plugins/TinkOS/config.yml");
            if (!file.exists()) {
                file.createNewFile();
            }
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        server = getProxy();
        sqlUtil = new SqlUtil();
        try {
            dashboardConnection = new DashboardConnection();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            getLogger().severe("Error connecting to Dashboard!");
            return;
        }
        registerListeners();
        ProxyServer.getInstance().setReconnectHandler(new TinkReconnectHandler());
        getProxyServer().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                if (!dashboardConnection.isConnected()) {
                    getLogger().severe("Error connecting to Dashboard! Stopping BungeeCord...");
                    getProxyServer().stop();
                }
            }
        }, 3, TimeUnit.SECONDS);
        getProxyServer().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                canJoin = true;
            }
        }, 2, TimeUnit.SECONDS);
        getProxyServer().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                for (Player tp : players.values()) {
                    UUID uuid = tp.getUniqueId();
                    if (getProxyServer().getPlayer(uuid) == null) {
                        PacketPlayerDisconnect packet = new PacketPlayerDisconnect(uuid, "");
                        TinkOS.dashboardConnection.send(packet);
                        removePlayer(tp.getUniqueId());
                    }
                }
                List<UUID> uuids = new ArrayList<>();
                for (ProxiedPlayer tp : getProxyServer().getPlayers()) {
                    uuids.add(tp.getUniqueId());
                }
                PacketPlayerList packet = new PacketPlayerList(uuids);
                dashboardConnection.send(packet);
            }
        }, 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public void onDisable() {
        sqlUtil.stop();
    }

    public static TinkOS getInstance() {
        return instance;
    }

    public static boolean canJoin() {
        return !BungeeCord.getInstance().getServers().isEmpty();
    }

    public static Configuration getConfig() {
        return config;
    }

    public static ProxyServer getProxyServer() {
        return server;
    }

    public static Player getPlayer(UUID uuid) {
        return players.get(uuid);
    }

    public static List<Player> getOnlinePlayers() {
        return new ArrayList<>(players.values());
    }

    public static void addPlayer(Player player) {
        players.put(player.getUniqueId(), player);
    }

    public static void removePlayer(UUID uuid) {
        players.remove(uuid);
    }

    public static boolean isMaintenance() {
        return maintenance;
    }

    public static void setMaintenance(boolean maintenance) {
        TinkOS.maintenance = maintenance;
    }

    public static List<UUID> getMaintenanceWhitelist() {
        return maintenanceWhitelist;
    }

    public static void setMaintenanceWhitelist(List<UUID> list) {
        TinkOS.maintenanceWhitelist = list;
    }

    public static String getMOTD() {
        return motd;
    }

    public static String getMOTDMmaintenance() {
        return motdmaintenance;
    }

    public static List<String> getInfo() {
        return info;
    }

    public static void setMOTD(String motd) {
        TinkOS.motd = motd;
    }

    public static void setMOTDMaintenance(String motd) {
        TinkOS.motdmaintenance = motd;
    }

    public static void setInfo(List<String> info) {
        TinkOS.info = info;
    }

    public static void setTargetLobby(ServerInfo server) {
        targetLobby = server;
    }

    public static ServerInfo getTargetLobby() {
        return targetLobby;
    }

    public static long getStartTime() {
        return startTime;
    }

    public static int getOnlineCount() {
        return onlineCount;
    }

    public static void setOnlineCount(int onlineCount) {
        TinkOS.onlineCount = onlineCount;
    }

    public static List<String> getCommands() {
        return commands;
    }

    public static void setCommands(List<String> commands) {
        TinkOS.commands = commands;
    }

    private void registerListeners() {
        PluginManager pm = getProxyServer().getPluginManager();
        pm.registerListener(this, new PlayerChat());
        pm.registerListener(this, new PlayerJoinAndLeave());
        pm.registerListener(this, new ProxyPing());
        pm.registerListener(this, new ServerKick());
        pm.registerListener(this, new ServerSwitch());
        pm.registerListener(this, new TabComplete());
        ProxyServer.getInstance().getServers().clear();
    }
}