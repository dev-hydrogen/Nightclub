package com.ilm9001.nightclub.light.pattern;

import lombok.Getter;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class MovementPattern {
    @Getter private final Function<Double, Vector2D> callable;
    
    public MovementPattern(Function<Double, Vector2D> pattern) {
        callable = pattern;
    }
    
    public Vector3D apply(@NotNull Vector3D v, @NotNull Double x, @Nullable Rotation r, @NotNull Double multiplier) {
        Vector2D v2 = callable.apply(x).scalarMultiply(multiplier);
        Plane plane = new Plane(v, 0.1);
        if (r != null) {
            plane = plane.rotate(v, r);
        }
        return plane.getPointAt(v2, 0);
    }
}