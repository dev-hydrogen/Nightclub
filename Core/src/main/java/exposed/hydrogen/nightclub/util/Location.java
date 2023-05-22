package exposed.hydrogen.nightclub.util;

import com.google.gson.JsonArray;
import lombok.Data;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

// yet another preparation for minestom support, not much reason to use otherwise, but this does also translate
// MINECRAFTS stupid FUCKING pitch AND yaw systems that DO NOT MAKE ANY SENSE!!!!

/**
 * A kind of Wrapper class for Minecrafts Location class which has non-standard pitch and yaw
 */
@Data
public class Location implements Cloneable {
    private double x;
    private double y;
    private double z;
    private double pitch;
    private double yaw;

    /**
     * No-args constructor, all values are set to 0
     */
    public Location() {
        this(0, 0, 0, 0, 0);
    }
    public Location(Number x, Number y, Number z) {
        this(x,y,z,0,0);
    }
    public Location(Number x, Number y, Number z, Number pitch, Number yaw) {
        this.x = x.doubleValue();
        this.y = y.doubleValue();
        this.z = z.doubleValue();
        this.pitch = pitch.doubleValue();
        this.yaw = yaw.doubleValue() % 360;
    }

    public static Location of() {
        return new Location();
    }
    public static Location of(Number x, Number y, Number z) {
        return new Location(x,y,z);
    }
    public static Location of(Number x, Number y, Number z, Number pitch, Number yaw) {
        return new Location(x,y,z,pitch,yaw);
    }

    public Location clone() {
        try {
            return (Location) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Location add(Number x, Number y, Number z) {
        this.x += x.doubleValue();
        this.y += y.doubleValue();
        this.z += z.doubleValue();
        return this;
    }

    public double distanceSquared(Location loc) {
        return Math.pow(loc.getX() - x, 2) + Math.pow(loc.getY() - y, 2) + Math.pow(loc.getZ() - z, 2);
    }

    public static Location fromVector3D(Vector3D vector) {
        return new Location(vector.getX(), vector.getZ(), vector.getY(), 0, 0);
    }

    public static Location fromJsonArray(JsonArray array) {
        if(array == null) return null;
        return new Location(array.get(0).getAsDouble(),array.get(1).getAsDouble(),array.get(2).getAsDouble());
    }

    public Vector3D toVector3D() {
        return new Vector3D(x, z, y);
    }

    public Location add(Location loc) {
        return add(loc.getX(), loc.getY(), loc.getZ());
    }
}
