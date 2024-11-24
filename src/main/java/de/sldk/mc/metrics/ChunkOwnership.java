package de.sldk.mc.metrics;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import com.github.puregero.multilib.MultiLib;

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

        serverName = MultiLib.getLocalServerName();
        logger = plugin.getLogger();
    }

    @Override
    public void clear() {
        logger.info("Clearing chunk ownership metrics");
        CHUNK_OWNERSHIP.clear();
        logger.info("Cleared chunk ownership metrics");
    }

    @Override
    protected void collect(World world) {
        logger.info("Collecting chunk ownership metrics");
        String w = world.getName();
        logger.info("World: " + w);
        Chunk[] chunks = world.getLoadedChunks();
        logger.info("World: " + w + " Chunks: " + chunks.length);

        // logger.info("World: " + w + " Server: " + s + " Chunks: " + chunks.length);

        for (Chunk chunk : chunks) {
            int x = chunk.getX();
            int z = chunk.getZ();
            logger.info("World: " + w + " Chunk: " + x + " " + z);
            if (MultiLib.isChunkExternal(chunk)) {
                logger.info("World: " + w + " Chunk: " + x + " " + z + " is external");
                continue;
            }
            if (!MultiLib.isChunkLocal(chunk)) {
                logger.info("World: " + w + " Chunk: " + x + " " + z + " is not local");
                continue;
            }
            logger.info("World: " + w + " Chunk: " + x + " " + z + " is local");
            CHUNK_OWNERSHIP.labels(w, serverName, String.valueOf(x), String.valueOf(z)).set(1);
            logger.info("World: " + w + " Chunk: " + x + " " + z + " set");
        }
    }
}
