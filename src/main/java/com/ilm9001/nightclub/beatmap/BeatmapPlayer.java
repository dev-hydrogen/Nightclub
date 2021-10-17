package com.ilm9001.nightclub.beatmap;

import com.ilm9001.nightclub.light.event.LightChannel;
import com.ilm9001.nightclub.light.event.LightEventHandler;
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
    
    public BeatmapPlayer(String name) {
        info = BeatmapParser.getInfoData(name);
        events = BeatmapParser.getEvents(name);
        this.name = name;
        playTo = new ArrayList<>();
    }
    
    public void play(List<Player> playTo) {
        executorService = Executors.newScheduledThreadPool(1);
        playTo.forEach((player) -> player.playSound(player.getLocation(), name, 1, 1));
        this.playTo = playTo;
        events.forEach((event) -> {
            // i cant lambda this :(
            Runnable task = () -> {
                handle(event);
            };
            executorService.schedule(task, event.getTime(), TimeUnit.MICROSECONDS);
        });
    }
    
    public void stop() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
        playTo.forEach(player -> player.stopSound(name));
    }
    
    private void handle(LightEvent event) {
        Color color;
        if (event.getValue() < 4 && event.getValue() != 0) {
            color = new Color(0x0066ff);
        } else if (event.getValue() > 4) {
            color = new Color(0xff0066);
        } else {
            color = new Color(0x000000);
        }
        
        // shorter way of handling events than using a switch case
        if (event.getType() >= 0 || event.getType() < 5) {
            Optional<LightChannel> channel = Arrays.stream(LightChannel.values()).filter((lc) -> event.getType() == lc.getType()).findFirst();
            channel.ifPresent(lightChannel -> handleValue(lightChannel.getHandler(), event.getValue(), color));
        } else switch (event.getType()) {
            // Ring spin
            case 8 -> this.getClass();
            //toggle zoom
            case 9 -> this.getClass();
            
            //rotation speed multiplier for left lasers
            case 12 -> LightChannel.LEFT_ROTATING_LASERS.getHandler().setSpeed(event.getValue());
            //rotation speed multiplier for right lasers
            case 13 -> LightChannel.RIGHT_ROTATING_LASERS.getHandler().setSpeed(event.getValue());
        }
    }
    
    private void handleValue(LightEventHandler handler, int value, Color color) {
        switch (value) {
            case 0 -> handler.off(color.getRGB());
            case 1, 5 -> handler.on(color.getRGB());
            case 2, 6 -> handler.flash(color.getRGB());
            case 3, 7 -> handler.flashOff(color.getRGB());
        }
    }
}
