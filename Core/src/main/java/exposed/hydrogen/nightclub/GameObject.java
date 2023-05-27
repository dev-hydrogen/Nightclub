package exposed.hydrogen.nightclub;

import exposed.hydrogen.nightclub.util.Location;

import java.util.List;

public interface GameObject {
    String name();
    void name(String name);
    void position(Location location);
    void active(boolean active);
    void scale(Location vec);
    void rotation(Location vec);
    void lightID(int id);
    Location position();
    boolean active();
    Location scale();
    Location rotation();
    int lightID();
    void destroy();
    List<GameObject> duplicate(int amount);
}
