package exposed.hydrogen.nightclub.light;

import exposed.hydrogen.nightclub.util.Util;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public enum LightPattern {
    LINE((x) -> new Vector2D(
            Math.sin(Math.toRadians(Util.getDegreesFromPercentage(x))),
            0.0)),
    CIRCLE((x) -> new Vector2D(
            Math.sin(Math.toRadians(Util.getDegreesFromPercentage(x))),
            Math.cos(Math.toRadians(Util.getDegreesFromPercentage(x))))),
    STILL((x) -> new Vector2D(0.0, 0.0).scalarMultiply(0.0));

    Function<Double, Vector2D> pattern;

    LightPattern(Function<Double, Vector2D> pattern) {
        this.pattern = pattern;
    }

    public Vector3D apply(@NotNull Vector3D v, @NotNull Double x, @Nullable Rotation r, @NotNull Double multiplier) {
        Vector2D v2 = pattern.apply(x).scalarMultiply(multiplier);
        Plane plane = new Plane(v, 0.1);
        if (r != null) {
            plane = plane.rotate(v, r);
        }
        return plane.getPointAt(v2, 0);
    }
}

