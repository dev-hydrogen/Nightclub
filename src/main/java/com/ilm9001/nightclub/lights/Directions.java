package com.ilm9001.nightclub.lights;

public enum Directions {
    NORTH ((3.0 * Math.PI)/2),
    SOUTH (2.0 * Math.PI),
    EAST  (Math.PI),
    WEST  (0);
    
    Double divisible;
    Directions(double facing) {
        divisible = facing;
    }
    
    public Double getValue() {
        return divisible;
    }
}
