package de.sldk.mc.metrics;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import com.github.puregero.multilib.MultiLib;

import io.prometheus.client.Gauge;

public class ChunkOwnership extends WorldMetric {

    private static final Gauge CHUNK_OWNERSHIP = Gauge.build()
            .name(prefix("chunk_ownership"))
            .help("Chunk ownership per world")
            .labelNames("world", "owner", "chunk_x", "chunk_z")
            .create();

    public ChunkOwnership(Plugin plugin) {
        super(plugin, CHUNK_OWNERSHIP);
    }

    @Override
    public void clear() {
        CHUNK_OWNERSHIP.clear();
    }

    @Override
    protected void collect(World world) {
        String w = world.getName();
        String s = MultiLib.getLocalServerName();
        Chunk[] chunks = world.getLoadedChunks();

        for (Chunk chunk : chunks) {
            int x = chunk.getX();
            int z = chunk.getZ();
            if (MultiLib.isChunkExternal(chunk))
                continue;
            CHUNK_OWNERSHIP.labels(w, s, String.valueOf(x), String.valueOf(z)).set(1);
        }
    }
}
