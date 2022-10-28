package us.mcmagic.tinkos.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import us.mcmagic.tinkos.TinkOS;
import us.mcmagic.tinkos.dashboard.packets.dashboard.PacketPlayerDisconnect;
import us.mcmagic.tinkos.dashboard.packets.dashboard.PacketPlayerJoin;
import us.mcmagic.tinkos.handlers.AddressBan;
import us.mcmagic.tinkos.handlers.Ban;
import us.mcmagic.tinkos.handlers.Player;
import us.mcmagic.tinkos.handlers.Rank;
import us.mcmagic.tinkos.utils.DateUtil;

/**
 * Created by Marc on 7/16/16
 */
public class PlayerJoinAndLeave implements Listener {
    private static boolean rebooting = false;

    @EventHandler
    public void onLogin(LoginEvent event) {
        PendingConnection connection = event.getConnection();
        if (!TinkOS.canJoin) {
            event.setCancelled(true);
            event.setCancelReason(ChatColor.AQUA + "Players can not join right now. Try again in a few seconds!");
            return;
        }
        if (rebooting) {
            event.setCancelled(true);
            event.setCancelReason(ChatColor.AQUA + "We're restarting our servers right now! Check back in a few moments.");
            return;
        }
        if (TinkOS.isMaintenance() && !TinkOS.getMaintenanceWhitelist().contains(connection.getUniqueId())) {
            event.setCancelled(true);
            event.setCancelReason(ChatColor.AQUA + "We are currently performing maintenance on our servers!\nFollow " +
                    ChatColor.BLUE + "@MCMagicDev " + ChatColor.AQUA + "on Twitter for updates.");
            return;
        }
        TinkOS.removePlayer(connection.getUniqueId());
        String address = connection.getAddress().getAddress().toString().replaceAll("/", "");
        AddressBan addressBan = TinkOS.sqlUtil.getAddressBan(address);
        if (addressBan != null) {
            event.setCancelled(true);
            event.setCancelReason(ChatColor.RED + "Your IP Address (" + addressBan.getAddress() +
                    ") has been banned from this server!\n Appeal at " + ChatColor.AQUA +
                    "https://mcmagic.us/appeal Reason: " + ChatColor.AQUA + addressBan.getReason());
            TinkOS.removePlayer(connection.getUniqueId());
            return;
        }
        String[] list = address.split("\\.");
        String range = list[0] + "." + list[1] + "." + list[2] + ".*";
        AddressBan rangeBan = TinkOS.sqlUtil.getAddressBan(range);
        if (rangeBan != null) {
            event.setCancelled(true);
            event.setCancelReason(ChatColor.RED + "Your IP Range (" + range + ") has been banned from this server!\nAppeal at "
                    + ChatColor.AQUA + "https://mcmagic.us/appeal Reason: " + ChatColor.AQUA + rangeBan.getReason());
            TinkOS.removePlayer(connection.getUniqueId());
            return;
        }
        Ban ban = TinkOS.sqlUtil.getBan(connection.getUniqueId(), connection.getName());
        if (ban != null) {
            event.setCancelled(true);
            if (ban.isPermanent()) {
                event.setCancelReason(ChatColor.RED + "You are banned from this server!\n Appeal at " +
                        ChatColor.AQUA + "https://mcmagic.us/appeal Reason: " + ChatColor.AQUA + ban.getReason());
                TinkOS.removePlayer(connection.getUniqueId());
                return;
            } else {
                if (ban.getRelease() > System.currentTimeMillis()) {
                    event.setCancelReason(ChatColor.RED + "You are temporarily banned from this server!\n Reason: "
                            + ChatColor.AQUA + ban.getReason() + ChatColor.RED + " Release: " + ChatColor.AQUA +
                            DateUtil.formatDateDiff(ban.getRelease()));
                    TinkOS.removePlayer(connection.getUniqueId());
                    return;
                }
                TinkOS.sqlUtil.unbanPlayer(connection.getUniqueId());
                event.setCancelled(false);
            }
        }
        TinkOS.addPlayer(new Player(connection.getUniqueId(), connection.getName(), Rank.GUEST, address));
        PacketPlayerJoin join = new PacketPlayerJoin(connection.getUniqueId(), connection.getName(), "unknown", address);
        TinkOS.dashboardConnection.send(join);
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer pl = event.getPlayer();
        Player player = TinkOS.getPlayer(pl.getUniqueId());
        if (player == null) {
            pl.disconnect(TextComponent.fromLegacyText(ChatColor.RED +
                    "We are currently experiencing some server-side issues. Please check back soon!"));
        }
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer pl = event.getPlayer();
        Player player = TinkOS.getPlayer(pl.getUniqueId());
        PacketPlayerDisconnect packet = new PacketPlayerDisconnect(pl.getUniqueId(), "");
        TinkOS.dashboardConnection.send(packet);
        if (player != null) {
            TinkOS.removePlayer(player.getUniqueId());
        }
    }

    public static boolean isRebooting() {
        return rebooting;
    }

    public static void setRebooting(boolean rebooting) {
        PlayerJoinAndLeave.rebooting = rebooting;
    }
}