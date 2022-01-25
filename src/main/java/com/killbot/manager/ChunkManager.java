package com.killbot.manager;

import org.bukkit.Chunk;
import org.bukkit.World;

// import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
// import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ChunkPos;

import java.util.concurrent.ConcurrentHashMap;


public class ChunkManager {

    private ConcurrentHashMap<String, ConcurrentHashMap<String, Chunk>> manuallyLoadedChunks;

    public ChunkManager(ServerLevel server) {
        this.manuallyLoadedChunks = new ConcurrentHashMap<String, ConcurrentHashMap<String, Chunk>>();
    }

    private String hashChunk(Chunk chunk) {
        return chunk.getX() + "," + chunk.getZ();
    }

    public void loadChunks(ServerPlayer player) {
        World server = player.getBukkitEntity().getWorld();
        String hash = server.getName(); // dunno if this is best.

        // b = x, c = z
        ChunkPos pos = player.chunkPosition();
        for (int i = pos.x - 1; i <= pos.x + 1; i++) {
            for (int j = pos.z - 1; j <= pos.z + 1; j++) {
                Chunk chunkt = server.getChunkAt(i, j);

                if (!chunkt.isLoaded()) {
                    server.loadChunk(chunkt); // not sure if necessary.
                    chunkt.setForceLoaded(true);
                }

                manuallyLoadedChunks.get(hash).put(hashChunk(chunkt), chunkt);
            }
        }
    };

    public ConcurrentHashMap<String, Chunk> getAllLoadedChunksOfWorld(ServerPlayer player) {
        return getAllLoadedChunksOfWorld(player.getBukkitEntity().getWorld());

    }

    public ConcurrentHashMap<String, Chunk> getAllLoadedChunksOfWorld(World world) {
        ConcurrentHashMap<String, Chunk> hasher = new ConcurrentHashMap<>();
        Chunk[] chunks = world.getLoadedChunks();
        for (Chunk chunk : chunks) {
            hasher.put(hashChunk(chunk), chunk);
        }

        return hasher;
    }

}
