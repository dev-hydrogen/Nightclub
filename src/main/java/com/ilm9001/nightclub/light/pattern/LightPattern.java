package com.ilm9001.nightclub.light.pattern;

import com.ilm9001.nightclub.util.Util;
import lombok.Getter;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public enum LightPattern {
    LINE(new MovementPattern((x) -> new Vector3D(0,Math.sin(Math.toRadians(Util.getDegreesFromPercentage(x)))).normalize(),100))
    ,CIRCLE(new MovementPattern((x) -> new Vector3D(0,Math.toRadians(Util.getDegreesFromPercentage(x))).normalize(),100))
    ,DOUBLE_CIRCLE(new MovementPattern((x) -> {
        Vector3D y = new Vector3D(0,Math.toRadians(Util.getDegreesFromPercentage(x))).normalize();
        Vector3D z = new Vector3D(0,Math.toRadians(Util.getDegreesFromPercentage(-x))).normalize();
        return y.add(z);
    },100))
    ,STILL(new MovementPattern((x) -> {return new Vector3D(0.0,0.0).normalize().scalarMultiply(0.0);},100));
    
    @Getter private final MovementPattern pattern;
    LightPattern(MovementPattern pattern) {
        this.pattern = pattern;
    }
}
