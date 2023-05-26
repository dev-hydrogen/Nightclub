package exposed.hydrogen.nightclub.beatmap;

import exposed.hydrogen.nightclub.GameObject;
import exposed.hydrogen.nightclub.util.Location;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class Track implements GameObject, Cloneable {
    private String name;
    private List<GameObject> objects;
    private List<Track> children;
    private @Nullable Track parent;

    private Location loc;
    private Location localLoc;
    private Location scale;
    private Location rot;
    private Location localRot;
    private boolean active;
    private int lightID;

    public Track(String name, List<GameObject> objects, List<Track> children, @Nullable Track parent) {
        this(name,objects,children,parent,Location.of(),Location.of(),Location.of(),Location.of(),Location.of(),true,0);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void position(Location location) {
        loc = location;
        updateChildren();
    }

    @Override
    public void active(boolean active) {
        this.active = active;
        updateChildren();
    }

    @Override
    public void scale(Location vec) {
        this.scale = vec;
        updateChildren();
    }

    @Override
    public void rotation(Location vec) {
        rot = vec;
        updateChildren();
    }

    @Override
    public void lightID(int id) {
        lightID = id;
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

    @Override
    public int lightID() {
        return lightID;
    }

    @Override
    public void destroy() {
        objects.forEach(GameObject::destroy);
        children.forEach(GameObject::destroy);
    }

    public void addChild(Track child) {
        if(child == this) throw new IllegalArgumentException("Cannot add a track to itself!");
        children.add(child);
        child.parent = this;
    }

    public void removeChild(Track child) {
        children.remove(child);
        child.parent = null;
    }

    public void addGameObject(GameObject object) {
        if(object instanceof Track) addChild((Track) object);
        if(object == this) throw new IllegalArgumentException("Cannot add a track to itself!");
        objects.add(object);
    }

    public void removeGameObject(GameObject object) {
        objects.remove(object);
    }

    private void updateChildren() {
        children.forEach((c) -> {
            c.active(active);
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
