package de.sldk.mc.metrics;

import com.github.puregero.multilib.MultiLib;
import io.prometheus.client.Gauge;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class PlayersOnlineLocal extends WorldMetric {

    private static final Gauge PLAYERS_ONLINE = Gauge.build()
            .name(prefix("players_online_local"))
            .help("Local players currently online per world")
            .labelNames("world")
            .create();

    private static Logger logger;

    public PlayersOnlineLocal(Plugin plugin) {
        super(plugin, PLAYERS_ONLINE);

        logger = plugin.getLogger();
    }

    @Override
    protected void clear() {
    }

    @Override
    protected void collect(World world) {
        String worldName = world.getName();
        long localPlayers = world.getPlayers().stream().filter(MultiLib::isLocalPlayer).count();
        // logger.info("Local players online in world " + worldName + ": " + localPlayers);
        PLAYERS_ONLINE.labels(worldName).set(localPlayers);
    }
}
