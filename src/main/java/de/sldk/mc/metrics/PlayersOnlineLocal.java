package de.sldk.mc.metrics;

import com.github.puregero.multilib.MultiLib;
import io.prometheus.client.Gauge;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class PlayersOnlineLocal extends WorldMetric {

    private static final Gauge PLAYERS_ONLINE = Gauge.build()
            .name(prefix("players_online_local"))
            .help("Local players currently online per world")
            .labelNames("world")
            .create();

    public PlayersOnlineLocal(Plugin plugin) {
        super(plugin, PLAYERS_ONLINE);
    }

    @Override
    protected void clear() {
    }

    @Override
    protected void collect(World world) {
        PLAYERS_ONLINE.labels(world.getName()).set(world.getPlayers().stream().filter(MultiLib::isLocalPlayer).count());
    }

    @Override
    public boolean isFoliaCapable() {
        return true;
    }

    @Override
    public boolean isAsyncCapable() {
        return true;
    }
}
