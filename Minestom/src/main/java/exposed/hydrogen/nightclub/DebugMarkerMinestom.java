package exposed.hydrogen.nightclub;

import exposed.hydrogen.nightclub.util.CrossCompatPlayer;
import exposed.hydrogen.nightclub.util.Location;
import exposed.hydrogen.nightclub.wrapper.DebugMarkerWrapper;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.play.PluginMessagePacket;
import net.minestom.server.utils.binary.BinaryWriter;

import java.awt.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DebugMarkerMinestom extends DebugMarkerWrapper {
    private static final PluginMessagePacket STOP_ALL_MARKERS;
    private Marker marker;
    private final List<Player> seen;

    public DebugMarkerMinestom(Location location, Color color, String name, Integer duration) {
        super(location, color, name, duration);
        marker = new Marker(location, color, name, duration);
        seen = new LinkedList<>();
    }

    @Override
    public void start(int distance) {
        start(distance, () -> {});
    }

    @Override
    public void start(int distance, Runnable callback) {
        if (!executorService.isShutdown()) {
            stop();
        }
        distanceSquared = distance < 0 ? -1 : distance * distance;
        long endTime = System.currentTimeMillis() + duration;
        executorService = Executors.newScheduledThreadPool(1);
        // probably not the most efficient way of doing this
        Runnable run = () -> {
            if (System.currentTimeMillis() > endTime) {
                seen.clear();
                callback.run();
                executorService.shutdown();
                return;
            }
            for (Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                if (!seen.contains(player) && isCloseEnough(MinestomUtil.getNightclubLocation(player.getPosition()))) {
                    setData(location, color, name, (int) (endTime - System.currentTimeMillis())); // make sure death time is the same for all players
                    player.sendPacket(getMarkerPacket(marker));
                    seen.add(player);
                } else if (seen.contains(player) && !isCloseEnough(MinestomUtil.getNightclubLocation(player.getPosition()))) {
                    setData(location, new Color(0, 0, 0, 0), "", 0);
                    player.sendPacket(getMarkerPacket(marker));
                    seen.remove(player);
                }
            }
        };
        executorService.scheduleAtFixedRate(run, 0, 200, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {
        marker = new Marker(location, new Color(0,0,0,0), "", 0);
        sendPacketToPlayerIfCloseEnough(getMarkerPacket(marker), location);
        seen.clear();
        executorService.shutdownNow();
    }

    @Override
    public void stopAll(int distance) {
        stopAll(location, distance);
    }

    @Override
    public void stopAll(Location location, int distance) {
        distanceSquared = distance < 0 ? -1 : distance * distance;
        sendPacketToPlayerIfCloseEnough(getMarkerPacket(marker), location);
    }

    @Override
    public void stopAll(List<CrossCompatPlayer> players) {
        for (CrossCompatPlayer player : players) {
            ((MinestomPlayer) player).player().sendPacket(STOP_ALL_MARKERS);
        }
    }

    @Override
    public void setData(Location location, Color color, String name, int duration) {
        marker = new Marker(location, color, name, duration);
    }

    /**
     * Sends a packet to players if they are in range
     * @param packet packet to send
     * @return all players who got the packet
     */
    private Collection<Player> sendPacketToPlayerIfCloseEnough(PluginMessagePacket packet, Location location) {
        Collection<Player> players = new LinkedList<>();
        for (Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            if (isCloseEnough(MinestomUtil.getNightclubLocation(player.getPosition()),location)) {
                player.sendPacket(getMarkerPacket(marker));
                players.add(player);
            }
        }
        return players;
    }



    private static PluginMessagePacket getMarkerPacket(Marker marker) {
        BinaryWriter buffer = new BinaryWriter();
        marker.write(buffer);
        return new PluginMessagePacket("minecraft:debug/game_test_add_marker", buffer.toByteArray());
    }
    public record Marker(Location position, Color color, String message, int duration) {
        public void write(BinaryWriter writer) {
            writer.writeBlockPosition(MinestomUtil.getMinestomPos(position));
            writer.writeInt(color.getRGB());
            writer.writeSizedString(message);
            writer.writeInt(duration);
        }
    }

    static {
        STOP_ALL_MARKERS = new PluginMessagePacket("minecraft:debug/game_test_clear", new byte[0]);
    }
}
