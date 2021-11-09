package com.ilm9001.nightclub.util;

import lombok.Data;
import org.bukkit.Bukkit;

// yet another preparation for minestom support, not much reason to use otherwise, but this does also translate
// MINECRAFTS stupid FUCKING pitch AND yaw systems that DO NOT MAKE ANY SENSE!!!!

/**
 * A kind of Wrapper class for Minecrafts Location class which has non-standard pitch and yaw
 */
@Data
public class Location implements Cloneable {
    private double x;
    private double y;
    private double z;
    private double pitch;
    private double yaw;
    private double rotation;
    
    public Location() {
        this(0, 0, 0, 0, 0, 0);
    }
    public Location(Number x, Number y, Number z, Number pitch, Number yaw) {
        this(x, y, z, pitch, yaw, 0);
    }
    public Location(Number x, Number y, Number z, Number pitch, Number yaw, Number rotation) {
        this.x = x.doubleValue();
        this.y = y.doubleValue();
        this.z = z.doubleValue();
        this.pitch = pitch.doubleValue() % 360;
        this.yaw = yaw.doubleValue() % 360;
        this.rotation = rotation.doubleValue();
    }
    
    public static Location getFromBukkitLocation(org.bukkit.Location loc) {
        return new Location(loc.getX(), loc.getY(), loc.getZ(), Location.translateMinecraftsStupidFuckingPitch(loc.getPitch()), Location.translateMinecraftsStupidFuckingYaw(loc.getYaw()));
    }
    
    private static double translateMinecraftsStupidFuckingPitch(float pitch) {
        return -pitch;
    }
    
    private static double translateMinecraftsStupidFuckingYaw(float yaw) {
        return yaw - 270;
    }
    
    public org.bukkit.Location getBukkitLocation() {
        return new org.bukkit.Location(Bukkit.getWorlds().get(0), x, y, z, (float) -pitch, (float) yaw + 270);
        //TODO: fix hardcoded world
    }
    
    public Location clone() {
        try {
            return (Location) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Location add(Number x, Number y, Number z) {
        this.x += x.doubleValue();
        this.y += y.doubleValue();
        this.z += z.doubleValue();
        return this;
    }
}
