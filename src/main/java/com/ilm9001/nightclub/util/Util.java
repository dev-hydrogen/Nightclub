package com.ilm9001.nightclub.util;

import com.ilm9001.nightclub.Nightclub;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

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
}
