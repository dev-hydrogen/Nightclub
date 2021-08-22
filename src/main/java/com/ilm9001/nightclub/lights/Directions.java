package com.ilm9001.nightclub.lights;

public enum Directions {
    NORTH ((3.0 * Math.PI)/2,0,-1),
    SOUTH (2.0 * Math.PI,0,1),
    EAST  (Math.PI,1,0),
    WEST  (0,-1,0);
    
    Double divisible;
    Double x;
    Double z;
    Directions(double facing,double x,double z) {
        divisible = facing;
        this.x = x;
        this.z = z;
    }
    
    public Double getValue() {
        return divisible;
    }
    public Double getX() { return x; }
    public Double getZ() {
        return z;
    }
}
