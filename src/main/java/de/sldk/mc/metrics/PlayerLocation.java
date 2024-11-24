package de.sldk.mc.metrics;

import com.github.puregero.multilib.MultiLib;
import io.prometheus.client.Gauge;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class PlayerLocation extends Metric {

    private final static Gauge PLAYER_LOCATION = Gauge.build()
            .name(prefix("player_location"))
            .help("Player location by player name")
            .labelNames("name", "uid", "world", "chunk_x", "chunk_z", "chunk_owner")
            .create();

    private static Logger logger;
    private static Method getChunkHolderMethod;
    private static String serverName;

    public PlayerLocation(Plugin plugin) {
        super(plugin, PLAYER_LOCATION);

        serverName = MultiLib.getLocalServerName();
        logger = plugin.getLogger();

        try {
            Class<?> multiPaperClass = Class.forName("puregero.multipaper.MultiPaper");
            getChunkHolderMethod = multiPaperClass.getDeclaredMethod("getChunkHolder", String.class, int.class, int.class);
            getChunkHolderMethod.setAccessible(true);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public final void doCollect() {
        PLAYER_LOCATION.clear();
        for (Player player : MultiLib.getLocalOnlinePlayers()) {
            String playerName = player.getName();
            String uid = getUid(player);
            String world = player.getWorld().getName();
            Location location = player.getLocation();
            Chunk chunk = location.getChunk();
            int chunkX = chunk.getX();
            int chunkZ = chunk.getZ();
            String chunkOwner = "";
            if (MultiLib.isChunkLocal(chunk)) {
                chunkOwner = serverName;
            }
            else {
                try {
                    Object newChunkHolder = getChunkHolderMethod.invoke(null, world, chunkX, chunkZ);
                    Field externalOwnerField = newChunkHolder.getClass().getDeclaredField("externalOwner");
                    externalOwnerField.setAccessible(true);
                    Field nameField = externalOwnerField.getType().getDeclaredField("name");
                    nameField.setAccessible(true);
                    chunkOwner = (String) nameField.get(externalOwnerField.get(newChunkHolder));
                } catch (NoSuchFieldException | IllegalAccessException  | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            logger.info("Player: " + playerName + " World: " + world + " Chunk: " + chunkX + " " + chunkZ + " Owner: " + chunkOwner);
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
