package com.ilm9001.nightclub.light.pattern;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.function.Function;

public class MovementPattern {
    @Getter
    private final Function<Double, Vector2D> callable;
    @Getter
    @Setter
    private int delay;
    
    public MovementPattern(Function<Double, Vector2D> pattern, int delay) {
        callable = pattern;
        this.delay = delay;
    }
    
    public Vector3D apply(Vector3D v, double x, double multiplier) {
        Vector2D v2 = callable.apply(x).scalarMultiply(multiplier);
        Plane plane = new Plane(v, 0.1);
        return plane.getPointAt(v2, 5);
    }
}