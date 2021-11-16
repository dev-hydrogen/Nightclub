package com.ilm9001.nightclub.beatmap;

import com.ilm9001.nightclub.light.event.LightChannel;
import com.ilm9001.nightclub.light.event.LightEventHandler;
import lombok.Getter;
import org.bukkit.entity.Player;

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
    private List<Player> playTo;
    private final InfoData info;
    private final String name;
    private ScheduledExecutorService executorService;
    @Getter private boolean isPlaying;
    
    /**
     * Constructor that defines a new Beatmap you can play from.
     *
     * @param name name of the folder the beatmap itself resides in (/name/ExpertPlus.dat)
     */
    public BeatmapPlayer(String name) {
        info = BeatmapParser.getInfoData(name);
        events = BeatmapParser.getEvents(name);
        this.name = name;
        playTo = new ArrayList<>();
    }
    /**
     * Play the Beatmap defined in the constructor.
     *
     * @param playTo List of Players that should hear the music, lasers will still be shown normally to anyone in range.
     * @return Info of the beatmap file, for example if you wanted to broadcast the song name to all players.
     */
    public InfoData play(List<Player> playTo) {
        List<LightChannel> channelList = Arrays.asList(LightChannel.values());
        this.playTo = playTo;
        executorService = Executors.newScheduledThreadPool(1);
        playTo.forEach((player) -> player.playSound(player.getLocation(), name, 1, 1));
        isPlaying = true;
        
        channelList.forEach(channel -> {
            //start all channels up and then turn them off to wait for beatmap instructions
            channel.getHandler().start();
            channel.getHandler().off(new Color(0x000000));
        });
        
        events.forEach((event) -> {
            Runnable task = () -> handle(event);
            executorService.schedule(task, event.getTime(), TimeUnit.MICROSECONDS);
        });
        
        //schedule turn off after the show is over
        Runnable task = () -> channelList.forEach(channel -> {
            channel.getHandler().off(new Color(0x000000));
            channel.getHandler().stop();
            isPlaying = false;
        });
        executorService.schedule(task, events.get(events.size() - 1).getTime() + 5000000, TimeUnit.MICROSECONDS);
        
        return info;
    }
    /**
     * Stops execution of this BeatmapPlayer and stops sound for all players that music is being played to.
     */
    public void stop() {
        List<LightChannel> channelList = Arrays.asList(LightChannel.values());
        if (executorService != null) {
            executorService.schedule(() -> channelList.forEach(channel -> {
                channel.getHandler().off(new Color(0x000000));
                channel.getHandler().stop();
                isPlaying = false;
            }), 0, TimeUnit.MILLISECONDS);
            executorService.shutdownNow();
        }
        playTo.forEach(player -> player.stopSound(name));
    }
    /**
     * https://bsmg.wiki/mapping/map-format.html
     * Internal handler of LightEvents.
     */
    private void handle(LightEvent event) {
        // shorter way of handling events than using a switch case
        if (event.getType() >= 0 && event.getType() < 5) {
            Optional<LightChannel> channel = Arrays.stream(LightChannel.values()).filter((lc) -> event.getType() == lc.getType()).findFirst();
            channel.ifPresent(lightChannel -> handleValue(lightChannel.getHandler(), event.getValue(), event.getColor()));
        } else switch (event.getType()) {
            // Ring spin
            case 8 -> this.getClass();
            // Toggle ring zoom
            case 9 -> this.getClass();
            
            // Rotation speed multiplier for left lasers
            case 12 -> LightChannel.LEFT_ROTATING_LASERS.getHandler().setSpeed(event.getValue());
            // Rotation speed multiplier for right lasers
            case 13 -> LightChannel.RIGHT_ROTATING_LASERS.getHandler().setSpeed(event.getValue());
        }
    }
    
    private void handleValue(LightEventHandler handler, int value, Color color) {
        switch (value) {
            case 0 -> handler.off(color);
            case 1, 5 -> handler.on(color);
            case 2, 6 -> handler.flash(color);
            case 3, 7 -> handler.flashOff(color);
        }
    }
}
