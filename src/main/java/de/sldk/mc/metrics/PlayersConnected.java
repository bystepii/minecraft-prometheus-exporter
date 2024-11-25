package de.sldk.mc.metrics;

import io.prometheus.client.Gauge;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlayersConnected extends Metric {
    private static final Gauge PLAYERS_CONNECTED = Gauge.build()
            .name(prefix("players_connected"))
            .help("Show which players are connected to a specific server")
            .labelNames("player", "server")
            .create();

    private static String serverName;

    public PlayersConnected(Plugin plugin) {
        super(plugin, PLAYERS_CONNECTED);

        serverName = Bukkit.getLocalServerName();
    }

    @Override
    protected void doCollect() {
        PLAYERS_CONNECTED.clear();

        for (Player p: Bukkit.getOnlinePlayers()) {
            String playerName = p.getName();
            String s = p.getExternalServerName();
            if (s == null)
                s = serverName;
            PLAYERS_CONNECTED.labels(playerName, s).set(1);
        }
    }
}
