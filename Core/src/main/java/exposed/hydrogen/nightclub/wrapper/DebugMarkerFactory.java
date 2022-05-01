package exposed.hydrogen.nightclub.wrapper;

import exposed.hydrogen.nightclub.util.Location;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class DebugMarkerFactory<T extends DebugMarkerWrapper> {
    private Class<T> clazz;

    public DebugMarkerFactory(Class<T> clazz) {
        this.clazz = clazz;
    }
    public DebugMarkerWrapper build(Location location, Color color, String name, int duration) {
        try {
            return getT(location, color, name, duration);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private T getT(Location location, Color color, String name, Integer duration) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return clazz.getDeclaredConstructor(Location.class,Color.class,String.class,Integer.class).newInstance(location, color, name, duration);
    }
}
