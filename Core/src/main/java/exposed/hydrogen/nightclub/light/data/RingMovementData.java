package exposed.hydrogen.nightclub.light.data;

import exposed.hydrogen.nightclub.util.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

@Data
@AllArgsConstructor
@Builder
public class RingMovementData {
    private Location pitchYaw;
    private double distance;
    /**
     * How long the ring movement should take, in milliseconds.
     */
    private double duration;

    public RingMovementData() {
        this(new Location(), 0, 0);
    }

    /**
     * Calculates movement based on duration and distance, uses set Location as the pitch and yaw to move to
     *
     * @param time current time, 0-1
     * @return Location to move to
     */
    public Vector3D calculateMovement(double time) {
        return new Vector3D(Math.toRadians(pitchYaw.getYaw()), Math.toRadians(pitchYaw.getPitch())).normalize().scalarMultiply(distance * time);
    }
}
