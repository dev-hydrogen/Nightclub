package exposed.hydrogen.nightclub.light.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.LinkedList;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class RingData implements Cloneable{
    private RingMovementData ringMovementData;
    private String linkedRingUUID;
    private int ringCount;
    private double ringSize;
    private double ringOffset;
    private double ringSpacing;
    private double ringRotation;

    public RingData() {
        this(new RingMovementData(),"",0,0,0,0,0);
    }

    /**
     * Distributes points evenly across a circle, creating a ring. For example if ringCount is defined as 4, then this function
     * will return the edge points of a square. If ringcount is 3, then it will return the edge points of a triangle.
     * @param center The center of the ring
     * @param rotation The rotation of the ring
     * @return A list of the edge points of the ring
     */
    public List<Vector3D> calculateRingEdgePoints(Vector3D center, double rotation) {
        List<Vector3D> ringEdgePoints = new LinkedList<>();
        for (int i = 0; i < ringCount; i++) {
            Vector2D ringEdgePoint = LightPattern.CIRCLE.getPattern().apply(i*(100.0/ringCount)).scalarMultiply(ringSize);
            Plane plane = new Plane(center, 0.1);
            plane = plane.rotate(center,new Rotation(center,rotation, RotationConvention.FRAME_TRANSFORM));
            ringEdgePoints.add(plane.getPointAt(ringEdgePoint,0.0));
        }
        return ringEdgePoints;
    }

    @Override
    public RingData clone() {
        try {
            return (RingData) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
