package com.ilm9001.nightclub.util;

import lombok.Data;
import org.bukkit.Bukkit;

// yet another preparation for minestom support, no real reason to use otherwise
@Data
public class Location {
    private double x;
    private double y;
    private double z;
    private double pitch;
    private double yaw;
    
    public Location(Number x, Number y, Number z, Number pitch, Number yaw) {
        this.x = x.doubleValue();
        this.y = y.doubleValue();
        this.z = z.doubleValue();
        this.pitch = pitch.doubleValue();
        this.yaw = yaw.doubleValue();
    }
    
    public static Location getFromBukkitLocation(org.bukkit.Location loc) {
        return new Location(loc.getX(), loc.getY(), loc.getZ(), Location.translateMinecraftsStupidFuckingPitch(loc.getPitch()), Location.translateMinecraftsStupidFuckingYaw(loc.getYaw()));
    }
    
    private static double translateMinecraftsStupidFuckingPitch(float pitch) {
        return -pitch;
    }
    
    private static double translateMinecraftsStupidFuckingYaw(float yaw) {
        return yaw + 270 % 360;
    }
    
    public org.bukkit.Location getBukkitLocation() {
        return new org.bukkit.Location(Bukkit.getWorlds().get(0), x, y, z, (float) -pitch, (float) yaw + 90 % 360);
        //TODO: fix hardcoded world
    }
}
