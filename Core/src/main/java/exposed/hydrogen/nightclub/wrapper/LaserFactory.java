package exposed.hydrogen.nightclub.wrapper;

import exposed.hydrogen.nightclub.light.data.LightType;
import exposed.hydrogen.nightclub.util.Location;

import java.lang.reflect.InvocationTargetException;

public record LaserFactory<T extends LaserWrapper>(Class<T> clazz) {

    public LaserWrapper build(Location start, Location end, int duration, int distance, LightType type) {
        try {
            return getT(start, end, duration, distance, type);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private T getT(Location start, Location end, Integer duration, Integer distance, LightType type) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return clazz.getDeclaredConstructor(Location.class, Location.class, Integer.class, Integer.class, LightType.class).newInstance(start, end, duration, distance, type);
    }
}