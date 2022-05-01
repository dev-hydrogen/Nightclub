package exposed.hydrogen.nightclub;

import exposed.hydrogen.nightclub.util.CrossCompatPlayer;
import exposed.hydrogen.nightclub.util.Location;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;

public record MinestomPlayer(Player player) implements CrossCompatPlayer {

    @Override
    public Location getLocation() {
        Pos pos = player.getPosition();
        return new Location(pos.x(), pos.y(), pos.z(), Location.translateMinecraftsStupidFuckingPitch(pos.pitch()), Location.translateMinecraftsStupidFuckingYaw(pos.yaw()));
    }

    @Override
    public void playSound(Location location, String sound, float volume, float pitch) {
        player.playSound(Sound.sound(Key.key(sound), Sound.Source.MASTER, volume, pitch));
    }

    @Override
    public void stopSound(String name) {
        player.stopSound(SoundStop.named(Key.key(name)));
    }
}
