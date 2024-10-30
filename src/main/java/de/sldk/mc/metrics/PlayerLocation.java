package de.sldk.mc.metrics;

import com.github.puregero.multilib.MultiLib;
import io.prometheus.client.Collector;
import io.prometheus.client.Gauge;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PlayerLocation extends Metric {

    private final static Gauge PLAYER_LOCATION = Gauge.build()
            .name(prefix("player_location"))
            .help("Player location by player name")
            .labelNames("name", "uid", "world", "chunk_x", "chunk_z", "chunk_owner")
            .create();

    public PlayerLocation(Plugin plugin) {
        super(plugin, PLAYER_LOCATION);
    }

    @Override
    public final void doCollect() {
        PLAYER_LOCATION.clear();
        for (Player player : MultiLib.getLocalOnlinePlayers()) {
            String serverName = MultiLib.getLocalServerName();
            String playerName = player.getName();
            String uid = getUid(player);
            String world = player.getWorld().getName();
            Location location = player.getLocation();
            Chunk chunk = location.getChunk();
            int chunkX = chunk.getX();
            int chunkZ = chunk.getZ();
            String chunkOwner = MultiLib.isChunkLocal(chunk) ? serverName : "";
            PLAYER_LOCATION.labels(playerName, uid, world, String.valueOf(chunkX), String.valueOf(chunkZ), chunkOwner).set(1);
        }
    }

    protected String getUid(Player player) {
        return player.getUniqueId().toString();
    }

    protected String getNameOrUid(Player player) {
        return player.getName();
    }
}
