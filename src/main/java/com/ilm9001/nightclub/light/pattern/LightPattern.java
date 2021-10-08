package com.ilm9001.nightclub.light.pattern;

import com.ilm9001.nightclub.util.Util;
import lombok.Getter;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public enum LightPattern {
    LINE(new MovementPattern((x) -> new Vector2D(
            Math.sin(Math.toRadians(Util.getDegreesFromPercentage(x))),
            0.0), 100)),
    CIRCLE(new MovementPattern((x) -> new Vector2D(
            Math.sin(Math.toRadians(Util.getDegreesFromPercentage(x))),
            Math.cos(Math.toRadians(Util.getDegreesFromPercentage(x)))), 100)),
    DOUBLE_CIRCLE(new MovementPattern((x) ->
            CIRCLE.getPattern().getCallable().apply(x).add(CIRCLE.getPattern().getCallable().apply(-x)),
            100)),
    STILL(new MovementPattern((x) -> new Vector2D(0.0, 0.0).scalarMultiply(0.0), 100));
    
    @Getter
    private final MovementPattern pattern;
    
    LightPattern(MovementPattern pattern) {
        this.pattern = pattern;
    }
}

