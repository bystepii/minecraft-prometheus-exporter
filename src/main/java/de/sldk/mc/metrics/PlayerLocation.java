package de.sldk.mc.metrics;

import com.github.puregero.multilib.MultiLib;
import io.papermc.paper.chunk.system.scheduling.NewChunkHolder;
import io.prometheus.client.Gauge;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.UUID;

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
            NewChunkHolder newChunkHolder = getChunkHolder(world, chunkX, chunkZ);
            String chunkOwner = "";
            if (MultiLib.isChunkLocal(chunk)) {
                chunkOwner = MultiLib.getLocalServerName();
            }
            else {
                try {
                    Field externalOwnerField = NewChunkHolder.class.getDeclaredField("externalOwner");
                    externalOwnerField.setAccessible(true);
                    Field nameField = externalOwnerField.getType().getDeclaredField("name");
                    nameField.setAccessible(true);
                    chunkOwner = (String) nameField.get(externalOwnerField.get(newChunkHolder));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            PLAYER_LOCATION.labels(playerName, uid, world, String.valueOf(chunkX), String.valueOf(chunkZ), chunkOwner).set(1);
        }
    }

    protected String getUid(Player player) {
        return player.getUniqueId().toString();
    }

    protected String getNameOrUid(Player player) {
        return player.getName();
    }

    public static NewChunkHolder getChunkHolder(String world, BlockPos pos) {
        return getChunkHolder(world, pos.getX() >> 4, pos.getZ() >> 4);
    }

    public static NewChunkHolder getChunkHolder(UUID world, BlockPos pos) {
        return getChunkHolder(world, pos.getX() >> 4, pos.getZ() >> 4);
    }

    public static NewChunkHolder getChunkHolder(ServerLevel level, BlockPos pos) {
        return getChunkHolder(level, pos.getX() >> 4, pos.getZ() >> 4);
    }

    public static NewChunkHolder getChunkHolder(String world, int x, int z) {
        CraftWorld craftWorld = ((CraftWorld) Bukkit.getWorld(world));
        return craftWorld != null ? getChunkHolder(craftWorld.getHandle(), x, z) : null;
    }

    public static NewChunkHolder getChunkHolder(UUID world, int x, int z) {
        CraftWorld craftWorld = ((CraftWorld) Bukkit.getWorld(world));
        return craftWorld != null ? getChunkHolder(craftWorld.getHandle(), x, z) : null;
    }

    public static NewChunkHolder getChunkHolder(ServerLevel level, int x, int z) {
        return level.chunkTaskScheduler.chunkHolderManager.getChunkHolder(x, z);
    }

    public static NewChunkHolder getChunkHolder(Entity entity) {
        return getChunkHolder((ServerLevel) entity.level(), entity.chunkPosition().x, entity.chunkPosition().z);
    }
}
