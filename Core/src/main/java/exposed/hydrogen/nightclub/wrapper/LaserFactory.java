package exposed.hydrogen.nightclub.wrapper;

import exposed.hydrogen.nightclub.light.data.LightType;
import exposed.hydrogen.nightclub.util.Location;

import java.lang.reflect.InvocationTargetException;

public record LaserFactory<T extends LaserWrapper>(Class<T> clazz) {

    public LaserWrapper build(Location start, Location end, int duration, int distance, LightType type, boolean glow) {
        try {
            return getT(start, end, duration, distance, type,glow);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private T getT(Location start, Location end, Integer duration, Integer distance, LightType type, Boolean glow) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return clazz.getDeclaredConstructor(Location.class, Location.class, Integer.class, Integer.class, LightType.class, Boolean.class).newInstance(start, end, duration, distance, type,glow);
    }
}