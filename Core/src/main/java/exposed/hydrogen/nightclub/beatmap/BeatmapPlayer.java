package exposed.hydrogen.nightclub.beatmap;

import com.google.gson.JsonArray;
import exposed.hydrogen.nightclub.GameObject;
import exposed.hydrogen.nightclub.Nightclub;
import exposed.hydrogen.nightclub.beatmap.json.*;
import exposed.hydrogen.nightclub.beatmap.json.events.AnimateTrack;
import exposed.hydrogen.nightclub.beatmap.json.events.AssignPlayerToTrack;
import exposed.hydrogen.nightclub.beatmap.json.events.AssignTrackParent;
import exposed.hydrogen.nightclub.light.Light;
import exposed.hydrogen.nightclub.light.LightUniverse;
import exposed.hydrogen.nightclub.light.Ring;
import exposed.hydrogen.nightclub.light.event.LightChannel;
import exposed.hydrogen.nightclub.util.CrossCompatPlayer;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BeatmapPlayer {
    private final BeatmapParser.ParsedBeatmap parsedBeatmap;
    private final List<LightEvent> events;
    private final List<CustomEvent<?>> customEvents;
    private final List<EnvironmentObject> environment;
    private final List<GameObject> clonedObjects;
    private List<CrossCompatPlayer> playTo;
    @Getter private final List<Track> trackRegistry;
    private final InfoData info;
    private final String name;
    private final String sound;
    private final LightUniverse universe;
    private ScheduledExecutorService executorService;
    @Getter private boolean isPlaying;

    /**
     * Constructor that defines a new Beatmap you can play from.
     *
     * @param name name of the folder the beatmap itself resides in (/name/ExpertPlus.dat)
     */
    public BeatmapPlayer(String sound, String name, boolean useChroma, LightUniverse universe) {
        info = BeatmapParser.getInfoData(name, useChroma);
        parsedBeatmap = BeatmapParser.parseBeatmap(info, name);
        events = parsedBeatmap.getEvents();
        customEvents = parsedBeatmap.getCustomEvents();
        environment = parsedBeatmap.getEnvironment();
        trackRegistry = new ArrayList<>();
        clonedObjects = new ArrayList<>();
        this.universe = universe;
        this.sound = sound;
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
        executorService = Executors.newScheduledThreadPool(8);
        playTo.forEach((player) -> player.playSound(player.getLocation(), sound, 1, 1));
        isPlaying = true;
        Nightclub.getLightUniverseManager().getLoadedUniverse().getRings().forEach(Ring::reset);
        Nightclub.getLightUniverseManager().getLoadedUniverse().getRings().forEach(Ring::start);

        //start all channels up and then turn them off to wait for beatmap instructions
        channelList.forEach(LightChannel::initializePlayback);

        setupEnvironment();

        long startTime = System.currentTimeMillis();
        Nightclub.getChameleon().getLogger().info("Event count: " + events.size());
        for (int i = 0; i < events.size(); i++) {
            // account for the time it takes to parse the beatmap in the first place
            long diff = System.currentTimeMillis()-startTime;

            LightEvent event = events.get(i);
            List<LightEvent> filteredEvents = events.parallelStream().filter(e -> e.getType().equals(event.getType())).toList();
            int nextIndex = filteredEvents.indexOf(event) + 1;
            LightEvent nextEvent = nextIndex < filteredEvents.size() ? filteredEvents.get(nextIndex) : null;

            Runnable task = () -> handle(event, nextEvent);
            executorService.schedule(task, event.getTime()-(diff*1000), TimeUnit.MICROSECONDS);
        }

        long customEventStart = System.currentTimeMillis();
        for (int i = 0; i < customEvents.size(); i++) {

            long diff = System.currentTimeMillis()-customEventStart;
            CustomEvent<?> event = customEvents.get(i);
            CustomEvent<?> nextEvent = i+1 < customEvents.size() ? customEvents.get(i+1) : null;

            Runnable task = () -> handle(event, nextEvent);
            executorService.schedule(task,event.getTime()-(diff*1000), TimeUnit.MICROSECONDS);
        }

        //schedule turn off 5s after the show is over
        Runnable task = () -> {
            isPlaying = false;
            channelList.forEach(LightChannel::terminatePlayback);
            Nightclub.getLightUniverseManager().getLoadedUniverse().getRings().forEach(Ring::stop);
            Nightclub.getLightUniverseManager().getLoadedUniverse().getRings().forEach(Ring::reset);
            clonedObjects.forEach(GameObject::destroy);
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
        playTo.forEach(player -> player.stopSound(sound));
    }

    private void setupEnvironment() {
        for(EnvironmentObject object : environment) {
            GameObject oGameObject = universe.getGameObject(object.id(), object.lookupMethod());
            if (oGameObject == null) {
                continue;
            }
            List<GameObject> duplicated = oGameObject.duplicate(object.duplicate().orElse(1));
            for(GameObject gameObject : duplicated) {
                gameObject.active(object.active().orElse(true));
                if (object.position().isPresent()) {
                    gameObject.position(object.position().get());
                }
                if (object.rotation().isPresent()) {
                    gameObject.rotation(object.rotation().get());
                }
                if (object.scale().isPresent()) {
                    gameObject.scale(object.scale().get());
                }
                if (object.localPosition().isPresent()) {
                    gameObject.position(gameObject.position().clone().add(object.localPosition().get()));
                }
                if (object.localRotation().isPresent()) {
                    gameObject.rotation(gameObject.rotation().clone().add(object.localRotation().get()));
                }
                if(object.lightID().isPresent()) {
                    gameObject.lightID(object.lightID().get());
                }
                if(object.track().isPresent()) {
                    getTrackOrCreate(object.track().get()).addGameObject(gameObject);
                }
                this.clonedObjects.add(gameObject);
            }
        }
    }

    public Track getTrack(String track) {
        return trackRegistry.stream().filter(t -> t.name().equals(track)).findFirst().orElse(null);
    }

    public List<Track> getTracks(List<String> track) {
        return track.stream().map(this::getTrack).toList();
    }

    public Track getTrackOrCreate(String track) {
        Track t = getTrack(track);
        if(t == null) {
            t = new Track(track, new ArrayList<>(), new ArrayList<>(),null);
            trackRegistry.add(t);
        }
        return t;
    }

    /**
     * https://bsmg.wiki/mapping/map-format.html
     * Internal handler of LightEvents.
     */
    private void handle(LightEvent event, @Nullable LightEvent nextEvent) {
        // shorter way of handling events than using a switch case
        if (event.getType() >= 0 && event.getType() < 5) {
            Optional<LightChannel> channel = Arrays.stream(LightChannel.values()).filter((lc) -> event.getType() == lc.getType()).findFirst();
            channel.ifPresent(lightChannel -> handleValue(lightChannel, event.getValue(), event.getColor(), event.getLightID(),
                    nextEvent != null ? (int) (nextEvent.getTime() - event.getTime()) / 1000 : 500,event.getGradientEvent()));
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

    private void handle(CustomEvent<?> event, @Nullable CustomEvent<?> nextEvent) {
        if(event instanceof AssignTrackParent ev) {
            Track parent = getTrack(ev.getData().get_parentTrack());
            if(parent == null) { return; }
            getTracks(ev.getData().get_childrenTracks()).forEach(parent::addChild);
        }
        if(event instanceof AssignPlayerToTrack ev) {

        }
        if(event instanceof AnimateTrack ev) {
            List<Track> track = getTracks(ev.getData().get_track());
            if(track == null) { return; }
            track.forEach(t -> t.animate(ev));
        }
    }

    private void handleValue(LightChannel handler, int value, Color color, JsonArray lightIDs, int duration, @Nullable GradientEvent gradientEvent) {
        switch (value) {
            case 0 -> handler.off(color, lightIDs, duration,gradientEvent);
            case 1, 5 -> handler.on(color, lightIDs, duration,gradientEvent);
            case 2, 6 -> handler.flash(color, lightIDs, duration,gradientEvent);
            case 3, 7 -> handler.flashOff(color, lightIDs, duration,gradientEvent);
        }
    }
}
