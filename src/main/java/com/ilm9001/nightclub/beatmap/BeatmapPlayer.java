package com.ilm9001.nightclub.beatmap;

import com.ilm9001.nightclub.light.event.LightChannel;
import com.ilm9001.nightclub.light.event.LightEventHandler;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BeatmapPlayer {
    private final List<LightEvent> events;
    private final InfoData info;
    private final List<TimerTask> tasks;
    private final String name;
    
    public BeatmapPlayer(String name) {
        info = BeatmapParser.getInfoData(name);
        events = BeatmapParser.getEvents(name);
        this.name = name;
        tasks = new ArrayList<>();
    }
    
    public void play(List<Player> playTo) {
        Timer timer = new Timer();
        
        playTo.forEach((player) -> player.playSound(player.getLocation(), name, 1, 1));
        
        events.forEach((event) -> {
            // i cant lambda this :(
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    handle(event);
                }
            };
            tasks.add(task);
            timer.schedule(task, event.getTime());
        });
    }
    
    public void stop() {
        tasks.forEach(TimerTask::cancel);
    }
    
    private void handle(LightEvent event) {
        Color color = new Color(0x000000);
        if (event.getValue() < 4 && event.getValue() != 0) {
            color = new Color(0x0066ff);
        } else if (event.getValue() > 4) {
            color = new Color(0xff0066);
        }
        
        // could do some sort of LightChannel.x.getType(); call here and compare that way
        // could be neater, but this works too
        switch (event.getType()) {
            case 0 -> handleValue(LightChannel.BACK_LASERS.getHandler(), event.getValue(), color);
            case 1 -> handleValue(LightChannel.RING_LIGHTS.getHandler(), event.getValue(), color);
            case 2 -> handleValue(LightChannel.LEFT_ROTATING_LASERS.getHandler(), event.getValue(), color);
            case 3 -> handleValue(LightChannel.RIGHT_ROTATING_LASERS.getHandler(), event.getValue(), color);
            case 4 -> handleValue(LightChannel.CENTER_LIGHTS.getHandler(), event.getValue(), color);
            
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
