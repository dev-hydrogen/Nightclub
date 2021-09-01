package com.ilm9001.nightclub.util;

import com.ilm9001.nightclub.Nightclub;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Util {
    public static void safe_sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static World getMainWorld() {
        return Bukkit.getWorlds().get(0);
    }
    public static Location getLocFromConf(String name) {
        List<Double> coords = Nightclub.getInstance().getConfig().getDoubleList(name);
        return new Location(Util.getMainWorld(),coords.get(0),coords.get(1),coords.get(2));
    }
    public static Collection<Chunk> getChunkAround(Chunk origin, int radius) {
        World world = origin.getWorld();
        
        int length = (radius * 2) + 1;
        Set<Chunk> chunks = new HashSet<>(length * length);
        
        int cX = origin.getX();
        int cZ = origin.getZ();
        
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                chunks.add(world.getChunkAt(cX + x, cZ + z));
            }
        }
        return chunks;
    }
}
