package us.mcmagic.tinkos.dashboard;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.packet.TabCompleteResponse;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;
import us.mcmagic.tinkos.TinkOS;
import us.mcmagic.tinkos.dashboard.packets.BasePacket;
import us.mcmagic.tinkos.dashboard.packets.dashboard.*;
import us.mcmagic.tinkos.handlers.Player;
import us.mcmagic.tinkos.handlers.Rank;
import us.mcmagic.tinkos.listeners.PlayerJoinAndLeave;
import us.mcmagic.tinkos.utils.DateUtil;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marc on 5/22/16
 */
public class DashboardConnection {
    protected WebSocketClient ws;
    private boolean attempted = false;

    public DashboardConnection() throws URISyntaxException {
        ws = new WebSocketClient(new URI("ws://socket.dashboard.mcmagic.us:7892"), new Draft_10()) {
            @Override
            public void onMessage(String message) {
                JsonObject object = (JsonObject) new JsonParser().parse(message);
                if (!object.has("id")) {
                    return;
                }
                int id = object.get("id").getAsInt();
                System.out.println(object.toString());
                switch (id) {
                    case 20: {
                        PacketStaffListCommand packet = new PacketStaffListCommand().fromJSON(object);
                        UUID uuid = packet.getUniqueId();
                        Player player = TinkOS.getPlayer(uuid);
                        if (player == null) {
                            return;
                        }
                        List<String> owners = packet.getOwners();
                        List<String> mayors = packet.getMayors();
                        List<String> managers = packet.getManagers();
                        List<String> developers = packet.getDevelopers();
                        List<String> coordinators = packet.getCoordinators();
                        List<String> castmembers = packet.getCastmembers();
                        List<String> earningmyears = packet.getEarningmyears();
                        ComponentBuilder o = new ComponentBuilder("Owners: (" + owners.size() + ") ").color(ChatColor.GOLD);
                        ComponentBuilder ma = new ComponentBuilder("Mayors: (" + mayors.size() + ") ").color(ChatColor.GOLD);
                        ComponentBuilder m = new ComponentBuilder("Managers: (" + managers.size() + ") ").color(ChatColor.GOLD);
                        ComponentBuilder d = new ComponentBuilder("Developers: (" + developers.size() + ") ").color(ChatColor.GOLD);
                        ComponentBuilder co = new ComponentBuilder("Coordinators: (" + coordinators.size() + ") ").color(ChatColor.GREEN);
                        ComponentBuilder c = new ComponentBuilder("Cast Members: (" + castmembers.size() + ") ").color(ChatColor.GREEN);
                        ComponentBuilder eme = new ComponentBuilder("Earning My Ears: (" + earningmyears.size() + ") ").color(ChatColor.GREEN);
                        for (int i = 0; i < owners.size(); i++) {
                            String[] list = owners.get(i).split(":");
                            String name = list[0];
                            String server = list[1];
                            o.append(name, ComponentBuilder.FormatRetention.NONE).color(ChatColor.GREEN)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Currently on: ")
                                            .color(ChatColor.GREEN).append(server).color(ChatColor.AQUA).create()));
                            if (i < (owners.size() - 1)) {
                                o.append(", ");
                            }
                        }
                        for (int i = 0; i < mayors.size(); i++) {
                            String[] list = mayors.get(i).split(":");
                            String name = list[0];
                            String server = list[1];
                            ma.append(name, ComponentBuilder.FormatRetention.NONE).color(ChatColor.GREEN)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Currently on: ")
                                            .color(ChatColor.GREEN).append(server).color(ChatColor.AQUA).create()));
                            if (i < (mayors.size() - 1)) {
                                ma.append(", ");
                            }
                        }
                        for (int i = 0; i < managers.size(); i++) {
                            String[] list = managers.get(i).split(":");
                            String name = list[0];
                            String server = list[1];
                            m.append(name, ComponentBuilder.FormatRetention.NONE).color(ChatColor.GREEN)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Currently on: ")
                                            .color(ChatColor.GREEN).append(server).color(ChatColor.AQUA).create()));
                            if (i < (managers.size() - 1)) {
                                m.append(", ");
                            }
                        }
                        for (int i = 0; i < developers.size(); i++) {
                            String[] list = developers.get(i).split(":");
                            String name = list[0];
                            String server = list[1];
                            d.append(name, ComponentBuilder.FormatRetention.NONE).color(ChatColor.GREEN)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Currently on: ")
                                            .color(ChatColor.GREEN).append(server).color(ChatColor.AQUA).create()));
                            if (i < (developers.size() - 1)) {
                                d.append(", ");
                            }
                        }
                        for (int i = 0; i < castmembers.size(); i++) {
                            String[] list = castmembers.get(i).split(":");
                            String name = list[0];
                            String server = list[1];
                            c.append(name, ComponentBuilder.FormatRetention.NONE).color(ChatColor.GREEN)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Currently on: ")
                                            .color(ChatColor.GREEN).append(server).color(ChatColor.AQUA).create()));
                            if (i < (castmembers.size() - 1)) {
                                c.append(", ");
                            }
                        }
                        for (int i = 0; i < coordinators.size(); i++) {
                            String[] list = coordinators.get(i).split(":");
                            String name = list[0];
                            String server = list[1];
                            co.append(name, ComponentBuilder.FormatRetention.NONE).color(ChatColor.GREEN)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Currently on: ")
                                            .color(ChatColor.GREEN).append(server).color(ChatColor.AQUA).create()));
                            if (i < (coordinators.size() - 1)) {
                                co.append(", ");
                            }
                        }
                        for (int i = 0; i < earningmyears.size(); i++) {
                            String[] list = earningmyears.get(i).split(":");
                            String name = list[0];
                            String server = list[1];
                            eme.append(name, ComponentBuilder.FormatRetention.NONE).color(ChatColor.GREEN)
                                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Currently on: ")
                                            .color(ChatColor.GREEN).append(server).color(ChatColor.AQUA).create()));
                            if (i < (earningmyears.size() - 1)) {
                                eme.append(", ");
                            }
                        }
                        player.sendMessage(new ComponentBuilder("Online Staff Members:").color(ChatColor.GREEN).create());
                        if (owners.size() > 0) {
                            player.sendMessage(o.create());
                        }
                        if (mayors.size() > 0) {
                            player.sendMessage(ma.create());
                        }
                        if (managers.size() > 0) {
                            player.sendMessage(m.create());
                        }
                        if (developers.size() > 0) {
                            player.sendMessage(d.create());
                        }
                        if (coordinators.size() > 0) {
                            player.sendMessage(co.create());
                        }
                        if (castmembers.size() > 0) {
                            player.sendMessage(c.create());
                        }
                        if (earningmyears.size() > 0) {
                            player.sendMessage(eme.create());
                        }
                        break;
                    }
                    case 21: {
                        PacketListFriendCommand packet = new PacketListFriendCommand().fromJSON(object);
                        UUID uuid = packet.getUniqueId();
                        int page = packet.getPage();
                        int maxpage = packet.getMaxPage();
                        List<String> friendlist = packet.getFriendlist();
                        Player player = TinkOS.getPlayer(uuid);
                        if (player == null) {
                            return;
                        }
                        List<BaseComponent[]> list = new ArrayList<>();
                        for (String str : friendlist) {
                            String[] l = str.split(":");
                            String s = l[0];
                            if (l.length > 1) {
                                String server = l[1];
                                list.add(new ComponentBuilder("- ").color(ChatColor.AQUA).append(s).color(ChatColor.GREEN)
                                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join ")
                                                .color(ChatColor.GREEN).append(server + "!").color(ChatColor.YELLOW)
                                                .create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                                "/friend tp " + s)).create());
                            } else {
                                list.add(new ComponentBuilder("- ").color(ChatColor.AQUA).append(s).color(ChatColor.RED)
                                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("This player is offline!")
                                                .color(ChatColor.RED).create())).create());
                            }
                        }
                        player.sendMessage(new ComponentBuilder("Friend List ").color(ChatColor.YELLOW).append("[Page " + page + " of "
                                + maxpage + "]").color(ChatColor.GREEN).create());
                        for (BaseComponent[] s : list) {
                            player.sendMessage(s);
                        }
                        if (list.size() > 8) {
                            player.sendMessage(new ComponentBuilder("Scroll up for the full list!").color(ChatColor.GREEN).create());
                        }
                        player.sendMessage(" ");
                        break;
                    }
                    case 24: {
                        PacketPlayerDisconnect packet = new PacketPlayerDisconnect().fromJSON(object);
                        UUID uuid = packet.getUniqueId();
                        String reason = packet.getReason();
                        ProxiedPlayer pl = TinkOS.getProxyServer().getPlayer(uuid);
                        if (pl != null) {
                            pl.disconnect(TextComponent.fromLegacyText(reason));
                        }
                        break;
                    }
                    case 25: {
                        PacketPlayerChat packet = new PacketPlayerChat().fromJSON(object);
                        UUID uuid = packet.getUniqueId();
                        String msg = packet.getMessage();
                        Player tp = TinkOS.getPlayer(uuid);
                        if (tp == null) {
                            return;
                        }
                        tp.chat(msg);
                        break;
                    }
                    case 26: {
                        PacketMessage packet = new PacketMessage().fromJSON(object);
                        UUID uuid = packet.getUniqueId();
                        Player p = TinkOS.getPlayer(uuid);
                        if (p == null) {
                            return;
                        }
                        String msg = packet.getMessage();
                        p.sendMessage(TextComponent.fromLegacyText(msg));
                        break;
                    }
                    case 28: {
                        PacketPlayerRank packet = new PacketPlayerRank().fromJSON(object);
                        UUID uuid = packet.getUniqueId();
                        Player player = TinkOS.getPlayer(uuid);
                        if (player == null) {
                            return;
                        }
                        player.setRank(packet.getRank());
                        break;
                    }
                    case 29: {
                        PacketStartReboot packet = new PacketStartReboot().fromJSON(object);
                        PlayerJoinAndLeave.setRebooting(true);
                        break;
                    }
                    case 30: {
                        PacketListRequestCommand packet = new PacketListRequestCommand().fromJSON(object);
                        UUID uuid = packet.getUniqueId();
                        List<String> requestlist = packet.getRequestlist();
                        Player player = TinkOS.getPlayer(uuid);
                        if (player == null) {
                            return;
                        }
                        player.sendMessage(ChatColor.GREEN + "Request List:");
                        for (String s : requestlist) {
                            player.sendMessage(new ComponentBuilder("- ").color(ChatColor.AQUA).append(s)
                                    .color(ChatColor.YELLOW).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                            new ComponentBuilder("Click to Accept the Request!").color(ChatColor.GREEN).create()))
                                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + s)).create());
                        }
                        player.sendMessage(new ComponentBuilder(" ").create());
                        break;
                    }
                    case 31: {
                        PacketFriendRequest packet = new PacketFriendRequest().fromJSON(object);
                        UUID uuid = packet.getUniqueId();
                        String from = packet.getFrom();
                        Player tp = TinkOS.getPlayer(uuid);
                        if (tp == null) {
                            return;
                        }
                        tp.sendMessage(new ComponentBuilder("\n" + from).color(ChatColor.GREEN)
                                .append(" has sent you a Friend Request!").color(ChatColor.YELLOW).create());
                        tp.sendMessage(new ComponentBuilder("Click to Accept").color(ChatColor.GREEN).bold(true).event(new
                                ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friend accept " + from)).append(" or ",
                                ComponentBuilder.FormatRetention.NONE).color(ChatColor.AQUA).append("Click to Deny\n")
                                .color(ChatColor.RED).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                        "/friend deny " + from)).create());
                        break;
                    }
                    case 32: {
                        PacketSendToServer packet = new PacketSendToServer().fromJSON(object);
                        UUID uuid = packet.getUniqueId();
                        String server = packet.getServer();
                        ProxiedPlayer tp = TinkOS.getProxyServer().getPlayer(uuid);
                        if (tp == null) {
                            return;
                        }
                        try {
                            tp.connect(TinkOS.getProxyServer().getServerInfo(server));
                        } catch (Exception ignored) {
                        }
                        break;
                    }
                    case 33: {
                        PacketUpdateMOTD packet = new PacketUpdateMOTD().fromJSON(object);
                        String motd = packet.getMOTD();
                        String maintenance = packet.getMaintenance();
                        List<String> info = packet.getInfo();
                        TinkOS.setMOTD(motd);
                        TinkOS.setMOTDMaintenance(maintenance);
                        TinkOS.setInfo(info);
                        break;
                    }
                    case 34: {
                        PacketBseenCommand packet = new PacketBseenCommand().fromJSON(object);
                        UUID uuid = packet.getUniqueId();
                        String username = packet.getUsername();
                        String address = packet.getAddress();
                        String server = packet.getServer();
                        boolean online = packet.isOnline();
                        Player tp = TinkOS.getPlayer(uuid);
                        if (tp == null) {
                            return;
                        }
                        String divider = " - ";
                        tp.sendMessage(new ComponentBuilder(address).color(ChatColor.AQUA)
                                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/ipseen " + address))
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new ComponentBuilder("Click to run an IP Search").color(ChatColor.AQUA)
                                                .create())).append(divider).color(ChatColor.DARK_GREEN)
                                .append("Name Check").color(ChatColor.LIGHT_PURPLE)
                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/namecheck " + username))
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new ComponentBuilder("Click to run a Name Check").color(ChatColor.AQUA)
                                                .create())).append(divider).color(ChatColor.DARK_GREEN)
                                .append("Mod Log").color(ChatColor.GREEN)
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new ComponentBuilder("Review Moderation History").color(ChatColor.GREEN)
                                                .create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                        "/modlog " + username)).append("\n" + (online ? "Current" : "Last") + " Server: ")
                                .color(ChatColor.YELLOW).append(server).color(ChatColor.AQUA)
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new ComponentBuilder("Click to join this server!").color(ChatColor.GREEN)
                                                .create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                        "/server " + server)).create());
                        break;
                    }
                    case 35: {
                        PacketServerList packet = new PacketServerList().fromJSON(object);
                        List<String> servers = packet.getServers();
                        BungeeCord bungee = ((BungeeCord) ProxyServer.getInstance());
                        for (String server : servers) {
                            try {
                                String[] list = server.split(":");
                                ServerInfo info = ProxyServer.getInstance().constructServerInfo(list[0],
                                        new InetSocketAddress(list[1], Integer.parseInt(list[2])), "", false);
                                bungee.getServers().put(info.getName(), info);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    }
                    case 36: {
                        PacketRemoveServer packet = new PacketRemoveServer().fromJSON(object);
                        String name = packet.getName();
                        ProxyServer.getInstance().getServers().remove(name);
                        break;
                    }
                    case 37: {
                        PacketAddServer packet = new PacketAddServer().fromJSON(object);
                        String name = packet.getName();
                        String address = packet.getAddress();
                        int port = packet.getPort();
                        ServerInfo server = ProxyServer.getInstance().constructServerInfo(name,
                                new InetSocketAddress(address, port), "", false);
                        ProxyServer.getInstance().getServers().put(name, server);
                        break;
                    }
                    case 38: {
                        PacketTargetLobby packet = new PacketTargetLobby().fromJSON(object);
                        String server = packet.getServer();
                        TinkOS.setTargetLobby(TinkOS.getProxyServer().getServerInfo(server));
                        break;
                    }
                    case 39: {
                        PacketJoinCommand packet = new PacketJoinCommand().fromJSON(object);
                        UUID uuid = packet.getUniqueId();
                        List<String> servers = packet.getServers();
                        Player tp = TinkOS.getPlayer(uuid);
                        if (tp == null) {
                            return;
                        }
                        TextComponent top = new TextComponent(ChatColor.GREEN + "Here is a list of servers you can join: " +
                                ChatColor.GRAY + "(Click to join)");
                        tp.sendMessage(top);
                        for (String server : servers) {
                            if (server.trim().equals("")) {
                                continue;
                            }
                            TextComponent txt = new TextComponent(ChatColor.GREEN + "- " + ChatColor.AQUA +
                                    formatName(server));
                            txt.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    new ComponentBuilder(ChatColor.GREEN + "Click to join the " + ChatColor.AQUA +
                                            formatName(server) + ChatColor.GREEN + " server!").create()));
                            txt.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/join " + server));
                            tp.sendMessage(txt);
                        }
                        break;
                    }
                    case 40: {
                        PacketUptimeCommand packet = new PacketUptimeCommand().fromJSON(object);
                        UUID uuid = packet.getUniqueId();
                        long time = packet.getTime();
                        Player tp = TinkOS.getPlayer(uuid);
                        if (tp == null) {
                            return;
                        }
                        tp.sendMessage(new ComponentBuilder("Dashboard has been online for " +
                                DateUtil.formatDateDiff(time) + "\nThis BungeeCord has been online for " +
                                DateUtil.formatDateDiff(TinkOS.getStartTime())).color(ChatColor.GREEN).create());
                        break;
                    }
                    case 41: {
                        PacketOnlineCount packet = new PacketOnlineCount().fromJSON(object);
                        int count = packet.getCount();
                        TinkOS.setOnlineCount(count);
                        break;
                    }
                    case 42: {
                        PacketAudioCommand packet = new PacketAudioCommand().fromJSON(object);
                        UUID uuid = packet.getUniqueId();
                        int auth = packet.getAuth();
                        Player tp = TinkOS.getPlayer(uuid);
                        if (tp == null) {
                            return;
                        }
                        tp.sendMessage(new ComponentBuilder("\nClick here to connect to our Audio Server!\n")
                                .color(ChatColor.GREEN).underlined(true).bold(true)
                                .event((new ClickEvent(ClickEvent.Action.OPEN_URL, "http://audio.mcmagic.us/?username="
                                        + tp.getName() + "&auth=" + auth))).create());
                        break;
                    }
                    case 43: {
                        PacketTabComplete packet = new PacketTabComplete().fromJSON(object);
                        UUID uuid = packet.getUniqueId();
                        List<String> results = packet.getResults();
                        ProxiedPlayer player = TinkOS.getProxyServer().getPlayer(uuid);
                        if (player == null) {
                            return;
                        }
                        if (results.isEmpty()) {
                            return;
                        }
                        player.unsafe().sendPacket(new TabCompleteResponse(results));
                        break;
                    }
                    case 44: {
                        PacketCommandList packet = new PacketCommandList().fromJSON(object);
                        List<String> commands = packet.getCommands();
                        TinkOS.setCommands(commands);
                        break;
                    }
                    case 45: {
                        PacketIPSeenCommand packet = new PacketIPSeenCommand().fromJSON(object);
                        UUID uuid = packet.getUniqueId();
                        Player tp = TinkOS.getPlayer(uuid);
                        if (tp == null) {
                            return;
                        }
                        List<String> users = packet.getUsernames();
                        String ip = packet.getAddress();
                        ComponentBuilder ulist = new ComponentBuilder("");
                        for (int i = 0; i < users.size(); i++) {
                            String s = users.get(i);
                            if (i == (users.size() - 1)) {
                                ulist.append(s).color(ChatColor.GREEN).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bseen "
                                        + s)).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new ComponentBuilder("Click to search this Player!").color(ChatColor.GREEN).create()));
                                continue;
                            }
                            ulist.append(s).color(ChatColor.GREEN).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bseen "
                                    + s)).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    new ComponentBuilder("Click to search this Player!").color(ChatColor.GREEN).create())).append(", ");
                        }
                        BaseComponent[] msg = new ComponentBuilder("Users on the IP Address " + ip + ":").color(ChatColor.AQUA).create();
                        tp.sendMessage(msg);
                        tp.sendMessage(ulist.create());
                        break;
                    }
                    case 46: {
                        PacketMaintenance packet = new PacketMaintenance().fromJSON(object);
                        boolean maintenance = packet.isMaintenance();
                        TinkOS.setMaintenance(maintenance);
                        break;
                    }
                    case 47: {
                        PacketMaintenanceWhitelist packet = new PacketMaintenanceWhitelist().fromJSON(object);
                        List<UUID> list = packet.getAllowed();
                        TinkOS.setMaintenanceWhitelist(list);
                        break;
                    }
                    case 53: {
                        PacketLink packet = new PacketLink().fromJSON(object);
                        UUID uuid = packet.getUniqueId();
                        String url = packet.getUrl();
                        String name = packet.getName();
                        ChatColor color = packet.getColor();
                        boolean bold = packet.isBold();
                        boolean spacing = packet.isSpacing();
                        Player tp = TinkOS.getPlayer(uuid);
                        if (tp == null) {
                            return;
                        }
                        String s = spacing ? "\n" : "";
                        tp.sendMessage(new ComponentBuilder(s + name + s).color(color).bold(bold)
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to visit " +
                                        url).color(ChatColor.GREEN).create())).event(new ClickEvent(ClickEvent.Action.OPEN_URL,
                                        url)).create());
                        break;
                    }
                    case 56: {
                        PacketWarning packet = new PacketWarning().fromJSON(object);
                        UUID warnid = packet.getWarningID();
                        String username = packet.getUsername();
                        String msg = packet.getMessage();
                        String action = packet.getAction();
                        BaseComponent[] comp = new ComponentBuilder(username).color(ChatColor.AQUA)
                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ":warn-" + warnid))
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new ComponentBuilder("Click to warn this player").color(ChatColor.GREEN)
                                                .create())).append(" " + action + ": ", ComponentBuilder.FormatRetention.ALL)
                                .color(ChatColor.RED).append(msg, ComponentBuilder.FormatRetention.ALL)
                                .color(ChatColor.AQUA).create();
                        for (Player tp : TinkOS.getOnlinePlayers()) {
                            if (tp.getRank().getRankId() >= Rank.EARNINGMYEARS.getRankId()) {
                                tp.sendMessage(comp);
                            }
                        }
                        break;
                    }
                    case 58: {
                        PacketPartyRequest packet = new PacketPartyRequest().fromJSON(object);
                        UUID uuid = packet.getUniqueId();
                        String from = packet.getFrom();
                        Player tp = TinkOS.getPlayer(uuid);
                        if (tp == null) {
                            return;
                        }
                        tp.sendMessage(new ComponentBuilder(from).color(ChatColor.YELLOW)
                                .append(" has invited you to their Party! ").color(ChatColor.GREEN).append("Click here to join the Party.")
                                .color(ChatColor.GOLD).bold(true).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new ComponentBuilder("Click to join this Party!").color(ChatColor.AQUA).create()))
                                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept"))
                                .append(" This invite will expire in 5 minutes.", ComponentBuilder.FormatRetention.NONE)
                                .color(ChatColor.GREEN).create());
                        break;
                    }
                    case 59: {
                        PacketMyMCMagicRegister packet = new PacketMyMCMagicRegister().fromJSON(object);
                        UUID uuid = packet.getUniqueId();
                        int pin = packet.getPin();
                        Player tp = TinkOS.getPlayer(uuid);
                        if (tp == null) {
                            return;
                        }
                        String url = "https://my.mcmagic.us/?r=1&pin=" + pin;
                        tp.sendMessage(new ComponentBuilder("Click here").color(ChatColor.AQUA)
                                .event(new ClickEvent(ClickEvent.Action.OPEN_URL, url))
                                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new ComponentBuilder("Click to visit " + url).color(ChatColor.GREEN).create()))
                                .append(" to register.").color(ChatColor.GREEN).create());
                        break;
                    }
                    case 60: {
                        PacketTitle packet = new PacketTitle().fromJSON(object);
                        UUID uuid = packet.getUniqueId();
                        String title = packet.getTitle();
                        String subtitle = packet.getSubtitle();
                        int fadeIn = packet.getFadeIn();
                        int stay = packet.getStay();
                        int fadeOut = packet.getFadeOut();
                        ProxiedPlayer tp = TinkOS.getProxyServer().getPlayer(uuid);
                        if (tp == null) {
                            return;
                        }
                        Title t = TinkOS.getProxyServer().createTitle();
                        t.title(TextComponent.fromLegacyText(title));
                        t.subTitle(TextComponent.fromLegacyText(subtitle));
                        t.fadeIn(fadeIn);
                        t.stay(stay);
                        t.fadeOut(fadeOut);
                        t.send(tp);
                        break;
                    }
                }
            }

            @Override
            public void onOpen(ServerHandshake handshake) {
                System.out.println("Successfully connected to Dashboard");
                DashboardConnection.this.send(new PacketConnectionType(PacketConnectionType.ConnectionType.BUNGEECORD).getJSON().toString());
                attempted = false;
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println(code + " Disconnected from Dashboard! Shutting BungeeCord...");
                TinkOS.getProxyServer().stop();
            }

            @Override
            public void onError(Exception ex) {
                System.out.println("Error in Dashboard connection");
                ex.printStackTrace();
            }

        };
        ws.connect();
    }

    public void send(String s) {
        ws.send(s);
    }

    public boolean isConnected() {
        return ws.getConnection() != null;
    }

    public void send(BasePacket packet) {
        send(packet.getJSON().toString());
    }

    public void playerChat(UUID uuid, String message) {
        PacketPlayerChat packet = new PacketPlayerChat(uuid, message);
        send(packet);
    }

    private String formatName(String s) {
        String ns = "";
        if (s.length() < 4) {
            for (char c : s.toCharArray()) {
                ns += Character.toString(Character.toUpperCase(c));
            }
            return ns;
        }
        Character last = null;
        for (char c : s.toCharArray()) {
            if (last == null) {
                last = c;
                ns += Character.toString(Character.toUpperCase(c));
                continue;
            }
            if (Character.toString(last).equals(" ")) {
                ns += Character.toString(Character.toUpperCase(c));
            } else {
                ns += Character.toString(c);
            }
            last = c;
        }
        return ns;
    }
}