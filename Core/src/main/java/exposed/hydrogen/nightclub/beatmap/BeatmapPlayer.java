package exposed.hydrogen.nightclub.beatmap;

import com.google.gson.JsonArray;
import exposed.hydrogen.nightclub.Nightclub;
import exposed.hydrogen.nightclub.light.Light;
import exposed.hydrogen.nightclub.light.Ring;
import exposed.hydrogen.nightclub.light.event.LightChannel;
import exposed.hydrogen.nightclub.util.CrossCompatPlayer;
import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BeatmapPlayer {
    private final List<LightEvent> events;
    private List<CrossCompatPlayer> playTo;
    private final InfoData info;
    private final String name;
    private ScheduledExecutorService executorService;
    @Getter private boolean isPlaying;

    public BeatmapPlayer(String name) {
        this(name, false);
    }

    /**
     * Constructor that defines a new Beatmap you can play from.
     *
     * @param name name of the folder the beatmap itself resides in (/name/ExpertPlus.dat)
     */
    public BeatmapPlayer(String name, boolean useChroma) {
        info = BeatmapParser.getInfoData(name, useChroma);
        events = BeatmapParser.getEvents(name, useChroma);
        this.name = name;
        playTo = new ArrayList<>();
    }

    /**
     * Play the Beatmap defined in the constructor.
     *
     * @param playTo List of Players that should hear the music, lasers will still be shown normally to anyone in range.
     * @return Info of the beatmap file, for example if you wanted to broadcast the song name to all players.
     */
    public InfoData play(List<CrossCompatPlayer> playTo) {
        List<LightChannel> channelList = Arrays.asList(LightChannel.values());
        this.playTo = playTo;
        executorService = Executors.newScheduledThreadPool(1);
        playTo.forEach((player) -> player.playSound(player.getLocation(), name, 1, 1));
        isPlaying = true;
        Nightclub.getLightUniverseManager().getLoadedUniverse().getRings().forEach(Ring::reset);
        Nightclub.getLightUniverseManager().getLoadedUniverse().getRings().forEach(Ring::start);

        //start all channels up and then turn them off to wait for beatmap instructions
        channelList.forEach(LightChannel::initializePlayback);

        events.forEach((event) -> {
            Runnable task = () -> handle(event, events);
            executorService.schedule(task, event.getTime(), TimeUnit.MICROSECONDS);
        });

        //schedule turn off after the show is over
        Runnable task = () -> {
            isPlaying = false;
            channelList.forEach(LightChannel::terminatePlayback);
            Nightclub.getLightUniverseManager().getLoadedUniverse().getRings().forEach(Ring::stop);
            Nightclub.getLightUniverseManager().getLoadedUniverse().getRings().forEach(Ring::reset);
        };
        executorService.schedule(task, events.get(events.size() - 1).getTime() + 5000000, TimeUnit.MICROSECONDS);
        Nightclub.getChameleon().getLogger().info(info.toString());
        return info;
    }

    /**
     * Stops execution of this BeatmapPlayer and stops sound for all players that music is being played to.
     */
    public void stop() {
        List<LightChannel> channelList = Arrays.asList(LightChannel.values());
        channelList.forEach(LightChannel::terminatePlayback);
        isPlaying = false;
        if (executorService != null) {
            executorService.shutdownNow();
            Nightclub.getLightUniverseManager().getLoadedUniverse().getRings().forEach(Ring::stop);
            Nightclub.getLightUniverseManager().getLoadedUniverse().getRings().forEach(Ring::reset);
        }
        playTo.forEach(player -> player.stopSound(name));
    }

    /**
     * https://bsmg.wiki/mapping/map-format.html
     * Internal handler of LightEvents.
     */
    private void handle(LightEvent event, List<LightEvent> events) {
        // shorter way of handling events than using a switch case
        if (event.getType() >= 0 && event.getType() < 5) {
            Optional<LightChannel> channel = Arrays.stream(LightChannel.values()).filter((lc) -> event.getType() == lc.getType()).findFirst();
            channel.ifPresent(lightChannel -> handleValue(lightChannel, event.getValue(), event.getColor(), event.getLightID()));
        } else switch (event.getType()) {
            // Ring spin
            case 8 -> Nightclub.getLightUniverseManager().getLoadedUniverse().getRings().forEach(Ring::spin);
            // Toggle ring zoom
            case 9 -> {
                Nightclub.getLightUniverseManager().getLoadedUniverse().getLights().forEach(Light::ringZoom);
                Nightclub.getLightUniverseManager().getLoadedUniverse().getRings().forEach(Ring::ringZoom);
            }

            // Rotation speed multiplier for left lasers
            case 12 -> LightChannel.LEFT_ROTATING_LASERS.setSpeed(event.getValue());
            // Rotation speed multiplier for right lasers
            case 13 -> LightChannel.RIGHT_ROTATING_LASERS.setSpeed(event.getValue());
        }
    }

    private void handleValue(LightChannel handler, int value, Color color, JsonArray lightIDs) {
        switch (value) {
            case 0 -> handler.off(color, lightIDs);
            case 1, 5 -> handler.on(color, lightIDs);
            case 2, 6 -> handler.flash(color, lightIDs);
            case 3, 7 -> handler.flashOff(color, lightIDs);
        }
    }
}
