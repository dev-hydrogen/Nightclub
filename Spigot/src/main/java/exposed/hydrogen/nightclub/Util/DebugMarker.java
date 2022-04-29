package exposed.hydrogen.nightclub.Util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.MinecraftKey;
import exposed.hydrogen.nightclub.SpigotUtil;
import exposed.hydrogen.nightclub.util.CrossCompatPlayer;
import exposed.hydrogen.nightclub.util.Location;
import exposed.hydrogen.nightclub.wrapper.DebugMarkerWrapper;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.PacketDataSerializer;
import org.bukkit.entity.Player;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DebugMarker extends DebugMarkerWrapper {
    private static final PacketContainer STOP_ALL_MARKERS = new PacketContainer(PacketType.Play.Server.CUSTOM_PAYLOAD);
    private PacketDataSerializer data;
    private final PacketContainer marker;
    private org.bukkit.Location location;
    private Color color;
    private String name;
    private int duration;
    private int distanceSquared;
    private final List<Player> seen;
    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public DebugMarker(org.bukkit.Location location, Color color, String name, int duration, List<Player> showTo) throws InvocationTargetException {
        this(location, color, name, duration);
        for (Player player : showTo) {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, marker);
        }
    }

    public DebugMarker(org.bukkit.Location location, Color color, String name, int duration) {
        super(SpigotUtil.getNightclubLocation(location), color, name, duration);
        this.location = location;
        this.color = color;
        this.name = name;
        this.duration = duration;
        seen = new ArrayList<>();
        marker = new PacketContainer(PacketType.Play.Server.CUSTOM_PAYLOAD);
        data = new PacketDataSerializer(Unpooled.buffer());
        data.a(new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ())); // location
        data.writeInt(color.getRGB()); // color
        data.a(name); // name
        data.writeInt(duration); // lifetime of marker

        marker.getMinecraftKeys().write(0, new MinecraftKey("debug/game_test_add_marker"));
        marker.getSpecificModifier(PacketDataSerializer.class).write(0, data);
    }

    public void start(int distance) {
        start(distance, () -> {
        }); // do-nothing runnable
    }

    public void start(int distance, Runnable callback) {
        if (!executorService.isShutdown()) {
            stop();
        }
        distanceSquared = distance < 0 ? -1 : distance * distance;
        long startTime = System.currentTimeMillis();
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
            for (Player p : location.getWorld().getPlayers()) {
                if (isCloseEnough(p.getLocation()) && !seen.contains(p)) {
                    setData(SpigotUtil.getNightclubLocation(location), color, name, (int) (endTime - System.currentTimeMillis())); // make sure death time is the same for all players
                    try {
                        ProtocolLibrary.getProtocolManager().sendServerPacket(p, marker);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    seen.add(p);
                } else if (!isCloseEnough(p.getLocation()) && seen.contains(p)) {
                    setData(SpigotUtil.getNightclubLocation(location), new Color(0, 0, 0, 0), "", 0);
                    try {
                        ProtocolLibrary.getProtocolManager().sendServerPacket(p, marker);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    seen.remove(p);
                }
            }
        };
        executorService.scheduleAtFixedRate(run, 0, 200, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        setData(SpigotUtil.getNightclubLocation(location), new Color(0, 0, 0, 0), "", 0);
        for (Player p : location.getWorld().getPlayers()) {
            if (distanceSquared == -1 || this.location.distanceSquared(p.getLocation()) <= distanceSquared) {
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(p, marker);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        seen.clear();
        executorService.shutdownNow();
    }

    public void stopAll(int distance) {
        int distanceSquared = distance < 0 ? -1 : distance * distance;
        // probably not the most efficient way of doing this
        for (Player p : location.getWorld().getPlayers()) {
            if (distanceSquared == -1 || this.location.distanceSquared(p.getLocation()) <= distanceSquared) {
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(p, STOP_ALL_MARKERS);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stopAll(List<CrossCompatPlayer> stopTo) {
        for (CrossCompatPlayer player : stopTo) {
            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket((Player) player, STOP_ALL_MARKERS);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopAll(Location location, int distance) {
        int distanceSquared = distance < 0 ? -1 : distance * distance;
        // probably not the most efficient way of doing this
        for (Player p : SpigotUtil.getBukkitLocation(location).getWorld().getPlayers()) {
            if (distanceSquared == -1 || location.distanceSquared(SpigotUtil.getNightclubLocation(p.getLocation())) <= distanceSquared) {
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(p, STOP_ALL_MARKERS);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setData(Location location, Color color, String name, int duration) {
        data = new PacketDataSerializer(Unpooled.buffer());
        data.a(new BlockPosition(location.getX(), location.getY(), location.getZ()));
        data.writeInt(color.getRGB());
        data.a(name);
        data.writeInt(duration);
        marker.getSpecificModifier(PacketDataSerializer.class).write(0, data);
    }

    public void setLocation(Location location) {
        this.location = SpigotUtil.getBukkitLocation(location);
        setData(location, this.color, this.name, this.duration);
    }

    public void setColor(Color color) {
        this.color = color;
        setData(SpigotUtil.getNightclubLocation(this.location), this.color, this.name, this.duration);
    }

    public void setName(String name) {
        this.name = name;
        setData(SpigotUtil.getNightclubLocation(this.location), this.color, this.name, this.duration);
    }

    public void setDuration(int duration) {
        this.duration = duration;
        setData(SpigotUtil.getNightclubLocation(this.location), this.color, this.name, this.duration);
    }

    public Location getLocation() {
        return SpigotUtil.getNightclubLocation(location);
    }

    public Color getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    private boolean isCloseEnough(org.bukkit.Location location) {
        return distanceSquared == -1 ||
                this.location.distanceSquared(location) <= distanceSquared;
    }

    static {
        STOP_ALL_MARKERS.getMinecraftKeys().write(0, new MinecraftKey("debug/game_test_clear"));
        STOP_ALL_MARKERS.getSpecificModifier(PacketDataSerializer.class).write(0, new PacketDataSerializer(Unpooled.buffer()));
    }
}
