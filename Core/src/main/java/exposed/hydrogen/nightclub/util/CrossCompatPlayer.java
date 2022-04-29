package exposed.hydrogen.nightclub.util;

public interface CrossCompatPlayer {

    Location getLocation();

    void playSound(Location location, String sound, float volume, float pitch);

    void stopSound(String name);
}
