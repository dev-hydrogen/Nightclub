package exposed.hydrogen.nightclub.light;

import exposed.hydrogen.nightclub.Nightclub;
import exposed.hydrogen.nightclub.light.data.LightType;
import exposed.hydrogen.nightclub.light.data.RingData;
import exposed.hydrogen.nightclub.util.Location;
import exposed.hydrogen.nightclub.wrapper.LaserWrapper;
import lombok.*;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.*;
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

    private final transient LinkedHashMap<Integer,List<LaserWrapper>> lasers;
    private transient double zoomTime = 0; // 0 to 1, current zoom time. does nothing if <=0
    private transient double rotationTime = 0; // current rotation time
    @Getter @Setter private transient double rotation = 0; // 0 to 360, degrees rotation
    private transient boolean isZoomed = false;
    private final transient Runnable run;
    private final transient Random random = new Random(56789);
    private transient boolean isBuilt;

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
        lasers = new LinkedHashMap<>();
        this.run = () -> {
            if (isZoomed ? zoomTime < 1 : zoomTime > 0) {
                double duration = getRingData().getRingMovementData().getDuration();
                zoomTime = isZoomed ?
                        zoomTime + duration / 1000 / (DELAY / 10.0)
                        : zoomTime - duration / 1000 / (DELAY / 10.0);
            }
            zoomTime = zoomTime < 0 && !isZoomed ? 0 : zoomTime;
            zoomTime = zoomTime > 1 && isZoomed ? 1 : zoomTime;

            if(rotationTime == 0 && (isZoomed ? zoomTime >= 1 : zoomTime <= 0)) {
                // do nothing if no rotation or zooming needs to be happening
                return;
            }
            rotationTime = rotationTime > 120 ? 120 : rotationTime;
            rotationTime = rotationTime < -120 ? -120 : rotationTime;

            // if rotation time is more than 2, then we need to slow down rotation until its 0
            // if rotation time is less than -2, then we need to speed up rotation until it is 0
            if (rotationTime > 2) {
                rotationTime -= (rotationTime/15)+1;
            }
            if (rotationTime < -2) {
                rotationTime += (-rotationTime/15)+1;
            }

            if (rotationTime < 2 && rotationTime > -2) {
                rotationTime = 0;
            }
            rotation = rotation + rotationTime;
            rotation = rotation % (360*this.ringData.getRingCount());
            for (int ring = 0; ring < this.ringData.getRingCount(); ring++) {
                // a (invisible) "ray" the size of length, pointing towards the set pitch and yaw
                Vector3D v = new Vector3D(Math.toRadians(this.location.getYaw()), Math.toRadians(this.location.getPitch()))
                        .normalize().scalarMultiply(((ring+1) * this.ringData.getRingSpacing()) / (zoomTime+1));

                double ringRotation = (rotation*(this.ringData.getRingOffset()+ring))/this.ringData.getRingCount();

                List<Vector3D> ringEdgePoints = RingData.calculateRingEdgePoints(v,
                        Math.toRadians(ringRotation), this.ringData.getRingLightCount(), this.ringData.getRingSize());

                List<LaserWrapper> laserWrappers = lasers.get(ring);

                for (int i = 0; i < laserWrappers.size(); i++) {
                    LaserWrapper laser = laserWrappers.get(i);
                    Vector3D ringedgePoint = ringEdgePoints.get(i);
                    Vector3D nextRingEdgePoint = ringEdgePoints.get((i + 1) % ringEdgePoints.size());
                    Vector3D v1 = getRingData().getRingMovementData().calculateMovement(zoomTime);

                    laser.setStart(this.location.clone().add(Location.fromVector3D(ringedgePoint.add(v1).add(v))));
                    laser.setEnd(this.location.clone().add(Location.fromVector3D(nextRingEdgePoint.add(v1).add(v))));
                }
            }
        };
    }

    public void buildLasers() {
        for(List<LaserWrapper> laserWrappers : lasers.values()) {
            for(LaserWrapper laserWrapper : laserWrappers) {
                laserWrapper.stop();
            }
            laserWrappers.clear();
        }
        lasers.clear();
        for (int ring = 0; ring < this.ringData.getRingCount(); ring++) {
            // a (invisible) "ray" the size of length, pointing towards the set pitch and yaw
            Vector3D v = new Vector3D(Math.toRadians(this.location.getYaw()), Math.toRadians(this.location.getPitch()))
                    .normalize().scalarMultiply(((ring+1) * this.ringData.getRingSpacing()) / (zoomTime+1));

            double ringRotation = (rotation*(this.ringData.getRingOffset()+ring))/this.ringData.getRingCount();

            List<Vector3D> ringEdgePoints = RingData.calculateRingEdgePoints(v,
                    Math.toRadians(ringRotation), this.ringData.getRingLightCount(), this.ringData.getRingSize());

            List<LaserWrapper> laserWrappers = new LinkedList<>();

            for (int i = 0; i < ringData.getRingLightCount(); i++) {
                Vector3D ringedgePoint = ringEdgePoints.get(i);
                Vector3D nextRingEdgePoint = ringEdgePoints.get((i + 1) % ringEdgePoints.size());
                Vector3D v1 = getRingData().getRingMovementData().calculateMovement(zoomTime);
                LaserWrapper laser = Nightclub.getLaserFactory().build(
                        this.location.clone().add(Location.fromVector3D(ringedgePoint.add(v1).add(v))),
                        this.location.clone().add(Location.fromVector3D(nextRingEdgePoint.add(v1).add(v))),
                        -1, 256, lightType,false);
                laserWrappers.add(laser);
            }
            lasers.put(ring, laserWrappers);
        }
        isZoomed = true;
        run.run();
        isZoomed = false;
        isBuilt = true;
    }

    public void start() {
        stop();
        for(List<LaserWrapper> laserWrappers : lasers.values()) {
            for(LaserWrapper laserWrapper : laserWrappers) {
                laserWrapper.start();
            }
        }
        executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(run, 0, DELAY, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    public void stop() {
        executorService.shutdown();
        for(List<LaserWrapper> laserWrappers : lasers.values()) {
            for(LaserWrapper laserWrapper : laserWrappers) {
                laserWrapper.stop();
            }
        }
    }

    public void reset() {
        this.zoomTime = 0;
        this.rotation = 0;
        this.rotationTime = 0;
        this.isZoomed = true;
        run.run();
        this.isZoomed = false;
    }

    public void unload() {
        for(List<LaserWrapper> laserWrappers : lasers.values()) {
            for(LaserWrapper laserWrapper : laserWrappers) {
                laserWrapper.kill();
            }
        }
    }

    public void spin() {
        // Replicates beat sabers ring system which is random, TODO: Implement chroma support of customizable ring direction
        rotationTime = random.nextBoolean() ? rotationTime + ringData.getRingRotation() + Math.abs(rotation/getRingData().getRingCount())
                : rotationTime - ringData.getRingRotation() - Math.abs(rotation/getRingData().getRingCount());
        for(List<LaserWrapper> laserWrappers : lasers.values()) {
            for(LaserWrapper laserWrapper : laserWrappers) {
                laserWrapper.changeColor();
            }
        }
    }

    public void ringZoom() {
        isZoomed = !isZoomed;
    }
}
