package de.sldk.mc.metrics;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import io.prometheus.client.Gauge;

import java.util.logging.Logger;

public class ChunkOwnership extends WorldMetric {

    private static final Gauge CHUNK_OWNERSHIP = Gauge.build()
            .name(prefix("chunk_ownership"))
            .help("Chunk ownership per world")
            .labelNames("world", "owner", "chunk_x", "chunk_z")
            .create();

    private static Logger logger;
    private static String serverName;

    public ChunkOwnership(Plugin plugin) {
        super(plugin, CHUNK_OWNERSHIP);

        serverName = Bukkit.getLocalServerName();
        logger = plugin.getLogger();
    }

    @Override
    public void clear() {
        CHUNK_OWNERSHIP.clear();
    }

    @Override
    protected void collect(World world) {
        String w = world.getName();
        Chunk[] chunks = world.getLoadedChunks();

        // logger.info("World: " + w + " Server: " + s + " Chunks: " + chunks.length);

        for (Chunk chunk : chunks) {
            int x = chunk.getX();
            int z = chunk.getZ();
            if (chunk.isExternalChunk())
                continue;
            if (!chunk.isLocalChunk())
                continue;
            CHUNK_OWNERSHIP.labels(w, serverName, String.valueOf(x), String.valueOf(z)).set(1);
        }
    }
}
