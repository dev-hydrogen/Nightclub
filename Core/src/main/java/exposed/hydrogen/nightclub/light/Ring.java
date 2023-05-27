package exposed.hydrogen.nightclub.light;

import exposed.hydrogen.nightclub.GameObject;
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
public class Ring implements GameObject, Cloneable {
    private transient ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private static final int DELAY = 100; // run every x ms

    @Getter private final UUID uniqueId;
    @Getter @Setter private String name;
    @Getter @Setter private Location location; // only changes when manually set
    @Getter @Setter private RingData ringData;
    @Getter @Setter private LightType lightType;
    @Getter @Setter private int lightID;

    private final transient LinkedHashMap<Integer,List<LaserWrapper>> lasers;
    private transient double zoomTime = 0; // 0 to 1, current zoom time. does nothing if <=0
    private transient double rotationTime = 0; // current rotation time
    @Getter @Setter private transient double rotation = 0; // 0 to 360, degrees rotation
    private transient boolean isZoomed = false;
    private final transient Runnable run;
    private final transient Random random = new Random(56789);
    private transient boolean isBuilt;
    private transient boolean isActive;
    private transient Location currentLocation;
    private transient Location scale;
    private transient Location rotationVec;

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
        isActive = true;
        currentLocation = location.clone();
        this.run = () -> {
            if(!isActive) return;
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
                Vector3D v = new Vector3D(Math.toRadians(this.currentLocation.getYaw()), Math.toRadians(this.currentLocation.getPitch()))
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

                    laser.setStart(this.currentLocation.clone().add(Location.fromVector3D(ringedgePoint.add(v1).add(v))));
                    laser.setEnd(this.currentLocation.clone().add(Location.fromVector3D(nextRingEdgePoint.add(v1).add(v))));
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

    @Override
    public String name() {
        return getName();
    }

    @Override
    public void name(String name) {
        setName(name);
    }

    @Override
    public void position(Location location) {
        this.currentLocation = location;
    }

    @Override
    public void active(boolean active) {
        if(active) {
            start();
        } else {
            stop();
        }
        this.isActive = active;
    }

    @Override
    public void scale(Location vec) {
        scale = vec;
    }

    @Override
    public void rotation(Location vec) {
        rotationVec = vec;
    }

    @Override
    public void lightID(int id) {
        lightID = id;
    }

    @Override
    public Location position() {
        return currentLocation;
    }

    @Override
    public boolean active() {
        return isActive;
    }

    @Override
    public Location scale() {
        return scale;
    }

    @Override
    public Location rotation() {
        return rotationVec;
    }

    @Override
    public int lightID() {
        return lightID;
    }

    @Override
    public void destroy() {
        reset();
        stop();
        unload();
    }

    @Override
    public List<GameObject> duplicate(int amount) {
        List<Ring> list = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            try {
                list.add(this.clone());
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
        return new ArrayList<>(list);
    }

    // clone method
    @Override
    public Ring clone() throws CloneNotSupportedException {
        Ring ring = (Ring) super.clone();
        return ring;
    }
}
