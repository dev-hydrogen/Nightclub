package com.ilm9001.nightclub.lights;

public enum Directions {
    NORTH (Math.toRadians(180),0,-1),
    SOUTH (Math.toRadians(0),0,1),
    EAST  (Math.toRadians(270),-1,0),
    WEST  (Math.toRadians(90),1,0);
    
    Double radians;
    Double x;
    Double z;
    Directions(double radians,double x,double z) {
        this.radians = radians;
        this.x = x;
        this.z = z;
    }
    
    public Double getRadians() {
        return radians;
    }
    public Double getX() { return x; }
    public Double getZ() {
        return z;
    }
}
