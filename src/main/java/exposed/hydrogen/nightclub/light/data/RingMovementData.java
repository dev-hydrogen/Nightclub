package exposed.hydrogen.nightclub.light.data;

import exposed.hydrogen.nightclub.util.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

@Data
@AllArgsConstructor
public class RingMovementData {
    private Location pitchYaw;
    private double distance;
    /**
     * How long the ring movement should take, in milliseconds.
     */
    private double duration;

    /**
     * Calculates movement based on duration and distance, uses set Location as the pitch and yaw to move to
     *
     * @param time current time, 0-1
     * @return Location to move to
     */
    public Location calculateMovement(double time) {
        Vector3D v = new Vector3D(Math.toRadians(pitchYaw.getYaw()), Math.toRadians(pitchYaw.getPitch())).normalize().scalarMultiply(distance * time);
        return Location.fromVector3D(v);
    }
}
