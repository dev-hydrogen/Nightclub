package com.ilm9001.nightclub.light.pattern;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.concurrent.Callable;
import java.util.function.Function;

public class MovementPattern {
    @Getter @Setter private int delay;
    @Getter private final Function<Double,Vector3D> callable;
    
    public MovementPattern(Function<Double,Vector3D> pattern, int delay) {
        callable = pattern;
        this.delay = delay;
    }
}
