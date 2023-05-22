package exposed.hydrogen.nightclub;

import exposed.hydrogen.nightclub.util.CrossCompatPlayer;
import exposed.hydrogen.nightclub.util.Location;
import org.bukkit.entity.Player;

public record SpigotPlayer(Player player) implements CrossCompatPlayer {
    
    @Override
    public Location getLocation() {
        org.bukkit.Location location = player.getLocation();
        return new Location(location.getX(), location.getY(), location.getZ(), -location.getPitch(), location.getYaw()-270);
    }

    @Override
    public void playSound(Location location, String sound, float volume, float pitch) {
        player.playSound(SpigotUtil.getBukkitLocation(location), sound, volume, pitch);
    }

    @Override
    public void stopSound(String name) {
        player.stopSound(name);
    }

    @Override
    public void teleport(Location location) {
        player.teleport(SpigotUtil.getBukkitLocation(location));
    }
}
