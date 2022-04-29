package exposed.hydrogen.nightclub;

import exposed.hydrogen.nightclub.util.CrossCompatPlayer;
import exposed.hydrogen.nightclub.util.CrossCompatUtil;
import exposed.hydrogen.nightclub.util.Location;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class MinestomUtil implements CrossCompatUtil {
    public MinestomUtil() {
    }

    public List<CrossCompatPlayer> getListOfPlayers() {
        Collection<Player> players = MinecraftServer.getConnectionManager().getOnlinePlayers();
        List<CrossCompatPlayer> list = new LinkedList<>();
        for (Player player : players) {
            list.add(new MinestomPlayer(player));
        }
        return list;
    }

    public static Location getNightclubLocation(Pos pos) {
        return new Location(pos.x(), pos.y(), pos.z(),
                -pos.pitch(), pos.yaw() - 270);
    }

    public static Pos getMinestomPos(exposed.hydrogen.nightclub.util.Location location) {
        return new Pos(location.getX(), location.getY(), location.getZ(),
                (float) -location.getPitch(), (float) location.getYaw() + 270);
    }
}
