package exposed.hydrogen.nightclub;

import exposed.hydrogen.nightclub.util.CrossCompatPlayer;
import exposed.hydrogen.nightclub.util.CrossCompatUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class SpigotUtil implements CrossCompatUtil {
    public SpigotUtil() {
    }

    public List<CrossCompatPlayer> getListOfPlayers() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        List<CrossCompatPlayer> list = new LinkedList<>();
        for (Player player : players) {
            list.add(new SpigotPlayer(player));
        }
        return list;
    }

    public static exposed.hydrogen.nightclub.util.Location getNightclubLocation(Location location) {
        return new exposed.hydrogen.nightclub.util.Location(location.getX(), location.getY(), location.getZ(),
                -location.getPitch(), location.getYaw() - 270);
    }

    public static Location getBukkitLocation(exposed.hydrogen.nightclub.util.Location location) {
        return new org.bukkit.Location(Bukkit.getWorlds().get(0),
                location.getX(), location.getY(), location.getZ(),
                (float) -location.getPitch(), (float) location.getYaw() + 270);
    }
}
