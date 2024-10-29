package de.sldk.mc.metrics;

import io.prometheus.client.Gauge;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import com.github.puregero.multilib.MultiLib;

public class PlayersConnected extends Metric {
    private static final Gauge PLAYERS_CONNECTED = Gauge.build()
            .name(prefix("players_connected"))
            .help("Show which players are connected to a specific server")
            .labelNames("player", "server")
            .create();

    public PlayersConnected(Plugin plugin) {
        super(plugin, PLAYERS_CONNECTED);
    }

    @Override
    protected void doCollect() {
        PLAYERS_CONNECTED.clear();

        for (Player p: Bukkit.getOnlinePlayers()) {
            String playerName = p.getName();
            String serverName = MultiLib.getExternalServerName(p);
            if (serverName == null)
                serverName = MultiLib.getLocalServerName();
            PLAYERS_CONNECTED.labels(playerName, serverName).set(1);
        }
    }
}
