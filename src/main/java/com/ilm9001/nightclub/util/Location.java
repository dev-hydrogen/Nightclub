package com.ilm9001.nightclub.util;

import lombok.Data;

// yet another preparation for minestom support, no real reason to use otherwise
@Data
public class Location {
    private double x;
    private double y;
    private double z;
    private double pitch;
    private double yaw;
    
    public Location(Number x,Number y,Number z,Number pitch,Number yaw) {
        this.x=x.doubleValue();
        this.y=y.doubleValue();
        this.z=z.doubleValue();
        this.pitch=pitch.doubleValue();
        this.yaw=yaw.doubleValue();
    }
}
