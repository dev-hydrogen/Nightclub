package exposed.hydrogen.nightclub.light;

import exposed.hydrogen.nightclub.Nightclub;
import exposed.hydrogen.nightclub.light.data.LightType;
import exposed.hydrogen.nightclub.light.data.RingData;
import exposed.hydrogen.nightclub.util.Location;
import exposed.hydrogen.nightclub.wrapper.LaserWrapper;
import lombok.*;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@ToString
@EqualsAndHashCode
public class Ring {
    private transient ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private static final transient int DELAY = 100; // run every x ms

    @Getter private final UUID uniqueId;
    @Getter @Setter private String name;
    @Getter @Setter private Location location; // only changes when manually set
    @Getter @Setter private RingData ringData;
    @Getter @Setter private LightType lightType;

    private final transient List<LaserWrapper> lasers = new LinkedList<>();
    private transient double zoomTime = 0; // 0 to 1, current zoom time. does nothing if <=0
    private transient double rotationTime = 0; // current rotation time
    @Getter @Setter private transient double rotation = 0; // 0 to 360, degrees rotation
    private transient boolean isZoomed = false;
    private final transient Runnable run;
    private final transient Random random = new Random();

    private Ring() {
        this(UUID.randomUUID(),"",new Location(),new RingData(),LightType.GUARDIAN_BEAM);
    }

    @Builder
    public Ring(UUID uniqueId, String name, Location location, RingData ringData, LightType lightType) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.location = location;
        this.ringData = ringData;
        this.lightType = lightType;
        this.run = () -> {
            // Do nothing if no need to rotate
            if (rotationTime > 0) {
                double duration = getRingData().getRingRotation();
                zoomTime = zoomTime - duration/10;
            }
            if (isZoomed ? zoomTime > 0 : zoomTime < 1) {
                double duration = getRingData().getRingMovementData().getDuration();
                zoomTime = isZoomed ?
                        zoomTime - duration >= 0 ? duration / 1000 / (DELAY / 10.0) : 1
                        : zoomTime + duration <= 1 ? duration / 1000 / (DELAY / 10.0) : 1;
            }
            rotation = rotation + rotationTime;
            rotation = rotation % 360;
            List<Vector3D> ringEdgePoints = getRingData().calculateRingEdgePoints(this.location.toVector3D(), Math.toRadians(rotation));
            for (int i = 0; i < lasers.size(); i++) {
                LaserWrapper laser = lasers.get(i);
                Vector3D ringedgePoint = ringEdgePoints.get(i);
                Vector3D nextRingEdgePoint = ringEdgePoints.get((i + 1) % ringEdgePoints.size());
                Vector3D v1 = getRingData().getRingMovementData().calculateMovement(zoomTime);
                laser.setStart(this.location.clone().add(Location.fromVector3D(ringedgePoint.add(v1))));
                laser.setEnd(this.location.clone().add(Location.fromVector3D(nextRingEdgePoint.add(v1))));
            }
        };
    }

    public void buildLasers() {
        for(LaserWrapper laser : lasers) {
            laser.stop();
        }
        lasers.clear();
        List<Vector3D> ringEdgePoints = getRingData().calculateRingEdgePoints(this.location.toVector3D(), Math.toRadians(rotation));
        for (int i = 0; i < getRingData().getRingCount(); i++) {
            LaserWrapper laser;
            Vector3D ringedgePoint = ringEdgePoints.get(i);
            Vector3D nextRingEdgePoint = ringEdgePoints.get((i + 1) % ringEdgePoints.size());
            laser = Nightclub.getLaserFactory().build(Location.fromVector3D(ringedgePoint),Location.fromVector3D(nextRingEdgePoint), -1, 256, lightType);
            lasers.add(laser);
        }
    }

    public void start() {
        stop();
        for(LaserWrapper laser : lasers) {
            laser.start();
        }
        executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(run, 0, DELAY, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    public void stop() {
        executorService.shutdown();
        for(LaserWrapper laser : lasers) {
            laser.stop();
        }
    }

    public void spin() {
        // Replicates beat sabers ring system which is random, TODO: Implement chroma support of customizable ring direction
        rotationTime = Math.min(random.nextBoolean() ? rotationTime + Math.sqrt(ringData.getRingRotation()) : rotationTime - Math.sqrt(ringData.getRingRotation()),45);
    }

    public void ringZoom() {
        isZoomed = !isZoomed;
        zoomTime = isZoomed ? 1 : 0;
    }
}
