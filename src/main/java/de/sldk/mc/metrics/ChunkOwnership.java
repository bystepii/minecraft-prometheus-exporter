package de.sldk.mc.metrics;

import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import com.github.puregero.multilib.MultiLib;

import de.sldk.mc.collectors.ChunkOwnershipCollector;
import io.prometheus.client.Gauge;

public class ChunkOwnership extends WorldMetric {
    private static final Gauge CHUNK_OWNERSHIP = Gauge.build()
            .name(prefix("chunk_ownership"))
            .help("Chunk ownership per world")
            .labelNames("world", "server", "chunk_x", "chunk_z")
            .create();

    private final ChunkOwnershipCollector chunkOwnershipCollector = new ChunkOwnershipCollector();

    public ChunkOwnership(Plugin plugin) {
        super(plugin, CHUNK_OWNERSHIP);
    }

    @Override
    public void enable() {
        super.enable();
        getPlugin().getServer().getPluginManager().registerEvents(chunkOwnershipCollector, getPlugin());
    }

    @Override
    public void disable() {
        super.disable();
        HandlerList.unregisterAll(chunkOwnershipCollector);
    }

    @Override
    public void clear() {
        CHUNK_OWNERSHIP.clear();
    }


    @Override
    protected void collect(World world) {
        String w = world.getName();
        String s = MultiLib.getLocalServerName();
        Set<Chunk> chunks = chunkOwnershipCollector.getChunks(w);

        for (Chunk chunk : chunks) {
            int x = chunk.getX();
            int z = chunk.getZ();
            CHUNK_OWNERSHIP.labels(w, s, String.valueOf(x), String.valueOf(z)).set(1);
        }
    }
}
