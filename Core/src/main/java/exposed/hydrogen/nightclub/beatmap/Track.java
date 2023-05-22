package exposed.hydrogen.nightclub.beatmap;

import exposed.hydrogen.nightclub.GameObject;
import exposed.hydrogen.nightclub.util.Location;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class Track implements GameObject, Cloneable {
    String name;
    List<GameObject> objects;
    List<Track> children;
    @Nullable Track parent;

    Location loc;
    Location localLoc;
    Location scale;
    Location rot;
    Location localRot;
    boolean active;

    public Track(String name, List<GameObject> objects, List<Track> children, @Nullable Track parent) {
        this(name,objects,children,parent,Location.of(),Location.of(),Location.of(),Location.of(),Location.of(),true);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void position(Location location) {
        loc = location;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void scale(Location vec) {
        this.scale = vec;
    }

    @Override
    public void rotation(Location vec) {
        rot = vec;
    }

    @Override
    public Location position() {
        return loc;
    }

    @Override
    public boolean active() {
        return active;
    }

    @Override
    public Location scale() {
        return scale;
    }

    @Override
    public Location rotation() {
        return rot;
    }

    private void updateChildren() {
        children.forEach((c) -> {
            c.position(loc);
            c.rotation(rot);
            c.scale(scale);
        });
    }

    @Override
    public List<GameObject> duplicate(int amount) {
        List<GameObject> these = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            these.add(this.clone());
        }
        return these;
    }

    @Override
    public Track clone() {
        try {
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return (Track) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
