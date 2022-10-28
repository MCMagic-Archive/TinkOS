package us.mcmagic.tinkos.listeners;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import us.mcmagic.tinkos.TinkOS;
import us.mcmagic.tinkos.dashboard.packets.dashboard.PacketTabComplete;
import us.mcmagic.tinkos.handlers.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Marc on 9/3/16
 */
public class TabComplete implements Listener {

    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        Player tp = TinkOS.getPlayer(player.getUniqueId());
        if (tp == null) {
            return;
        }
        String[] split = Pattern.compile(" ").split(event.getCursor().substring(1));
        String cmd = split[0].toLowerCase();
        if (!TinkOS.getCommands().contains(cmd)) {
            return;
        }
        event.setCancelled(true);
        List<String> args = Arrays.asList(Arrays.copyOfRange(split, 1, split.length));
        PacketTabComplete packet = new PacketTabComplete(player.getUniqueId(), cmd, args, new ArrayList<String>());
        TinkOS.dashboardConnection.send(packet);
    }
}