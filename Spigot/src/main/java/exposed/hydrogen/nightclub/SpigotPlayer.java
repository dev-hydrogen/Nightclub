package exposed.hydrogen.nightclub;

import exposed.hydrogen.nightclub.util.CrossCompatPlayer;
import exposed.hydrogen.nightclub.util.Location;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public record SpigotPlayer(Player player) implements CrossCompatPlayer {
    
    @Override
    public Location getLocation() {
        org.bukkit.Location location = player.getLocation();
        return new Location(location.getX(), location.getY(), location.getZ(), Location.translateMinecraftsStupidFuckingPitch(location.getPitch()), Location.translateMinecraftsStupidFuckingYaw(location.getYaw()));
    }

    @Override
    public void playSound(Location location, String sound, float volume, float pitch) {
        player.playSound(new org.bukkit.Location(Bukkit.getWorlds().get(0), location.getX(), location.getY(), location.getZ()), sound, volume, pitch);
    }

    @Override
    public void stopSound(String name) {
        player.stopSound(name);
    }
}
