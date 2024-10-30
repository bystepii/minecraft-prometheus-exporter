package de.sldk.mc.collectors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import com.github.puregero.multilib.MultiLib;

public class ChunkOwnershipCollector implements Listener {

    private final Map<String, Set<Chunk>> chunkOwnershipMap = new HashMap<>();

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        if (MultiLib.isChunkExternal(chunk))
            return;

        String worldName = chunk.getWorld().getName();
        Set<Chunk> chunks = chunkOwnershipMap.get(worldName);
        if (chunks == null) {
            chunks = new HashSet<>();
            chunkOwnershipMap.put(worldName, chunks);
        }
        chunks.add(chunk);
    }

    @EventHandler
    public void onChunkUnload(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        if (MultiLib.isChunkExternal(chunk))
            return;

        String worldName = chunk.getWorld().getName();
        Set<Chunk> chunks = chunkOwnershipMap.get(worldName);
        if (chunks != null) {
            chunks.remove(chunk);
        }
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        String worldName = event.getWorld().getName();
        chunkOwnershipMap.remove(worldName);
    }

    public Set<Chunk> getChunks(String worldName) {
        return chunkOwnershipMap.computeIfAbsent(worldName, k -> new HashSet<>());
    }
}
