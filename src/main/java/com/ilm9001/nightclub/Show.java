package com.ilm9001.nightclub;

import com.ilm9001.nightclub.lights.LightHandler;
import com.ilm9001.nightclub.parse.ConfigParser;
import com.ilm9001.nightclub.parse.LE;
import com.ilm9001.nightclub.parse.LE_list;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Show {
    private final ScheduledExecutorService sch;
    private boolean is_running;
    
    public Show() {
        sch = Executors.newScheduledThreadPool(1);
        is_running = false;
    }
    
    /**
     * Handles an event and launches the proper light events associated with it.
     * Very messy (You could 100% condense this code if you cared)
     * and the LightHandler class could very well be integrated here, but I think this looks nicer.
     *
     * @param ty Type of event.
     * @param va Value of the event.
     */
    private void ev_handle(int ty, int va) {
        switch (ty) {
            //Back Lasers
            case 0:
                switch (va) {
                    case 0 -> LightHandler.BackLasers.off();
                    case 1 -> LightHandler.BackLasers.on(true);
                    case 5 -> LightHandler.BackLasers.on(false);
                    case 2 -> LightHandler.BackLasers.flash(true);
                    case 6 -> LightHandler.BackLasers.flash(false);
                    case 3 -> LightHandler.BackLasers.flashOff(true);
                    case 7 -> LightHandler.BackLasers.flashOff(false);
                }
                break;
            // Ring Lights
            case 1:
                switch (va) {
                    case 0 -> LightHandler.RingLights.off();
                    case 1 -> LightHandler.RingLights.on(true);
                    case 5 -> LightHandler.RingLights.on(false);
                    case 2 -> LightHandler.RingLights.flash(true);
                    case 6 -> LightHandler.RingLights.flash(false);
                    case 3 -> LightHandler.RingLights.flashOff(true);
                    case 7 -> LightHandler.RingLights.flashOff(false);
                }
                break;
            // Left Rotating Lasers
            case 2:
                switch (va) {
                    case 0 -> LightHandler.LeftLasers.off();
                    case 1 -> LightHandler.LeftLasers.on(true);
                    case 5 -> LightHandler.LeftLasers.on(false);
                    case 2 -> LightHandler.LeftLasers.flash(true);
                    case 6 -> LightHandler.LeftLasers.flash(false);
                    case 3 -> LightHandler.LeftLasers.flashOff(true);
                    case 7 -> LightHandler.LeftLasers.flashOff(false);
                }
                break;
            // Right Rotating Lasers
            case 3:
                switch (va) {
                    case 0 -> LightHandler.RightLasers.off();
                    case 1 -> LightHandler.RightLasers.on(true);
                    case 5 -> LightHandler.RightLasers.on(false);
                    case 2 -> LightHandler.RightLasers.flash(true);
                    case 6 -> LightHandler.RightLasers.flash(false);
                    case 3 -> LightHandler.RightLasers.flashOff(true);
                    case 7 -> LightHandler.RightLasers.flashOff(false);
                }
                break;
            //Center Lights
            case 4:
                switch (va) {
                    case 0 -> LightHandler.CenterLights.off();
                    case 1 -> LightHandler.CenterLights.on(true);
                    case 5 -> LightHandler.CenterLights.on(false);
                    case 2 -> LightHandler.CenterLights.flash(true);
                    case 6 -> LightHandler.CenterLights.flash(false);
                    case 3 -> LightHandler.CenterLights.flashOff(true);
                    case 7 -> LightHandler.CenterLights.flashOff(false);
                }
                break;
            // Ring spin
            case 8:
                ConfigParser.getRings().spin();
                break;
            // Toggle zoom, no value.
            case 9:
                ConfigParser.getRings().zoom();
                break;
            // Rotation speed, left lasers, va is multiplier.
            case 12:
                LightHandler.BackLasers.setSpeed(va);
                LightHandler.RingLights.setSpeed(va);
                LightHandler.LeftLasers.setSpeed(va);
                LightHandler.CenterLights.setSpeed(va);
                break;
    
            // Rotation speed, right lasers, va is multiplier.
            case 13:
                LightHandler.BackLasers.setSpeed(va);
                LightHandler.RingLights.setSpeed(va);
                LightHandler.RightLasers.setSpeed(va);
                LightHandler.CenterLights.setSpeed(va);
                break;
        }
    }
    
    public boolean Run(LE_list ev_list, double end_time)
    {
        if (is_running) {
            return false;
        }
        is_running = true;
        sch.schedule(new Show_runnable(ev_list, end_time), 0, TimeUnit.MILLISECONDS);
        return true;
    }
    
    public class Show_runnable implements Runnable {
        private final int le_size;
        private final LE_list ev_list;
        private final double end_time;
        
        public Show_runnable(LE_list ev_list, double end_time) {
            this.ev_list = ev_list;
            this.le_size = ev_list.size();
            this.end_time = end_time;
        }
        @Override
        public void run() {
            //stg.Run();
            ConfigParser.getRings().on();
            long t_start = System.currentTimeMillis();
            for (int i=0; i < le_size; ++i) {
                LE ev = ev_list.get(i);
                long from_start = System.currentTimeMillis() - t_start;
                long delay = ev.time_ms - from_start;
                if (delay < 0) {
                    // Negative delay, we are late!
                    if (delay < -1) {
                        Nightclub.getInstance().getLogger().info(String.format("Can't keep up at %.1fs delay=%d",
                                ev.time_ms/1000.0, delay));
                    }
                }
                else if (0 < delay && delay < 20) {
                    Nightclub.getInstance().getLogger().info(String.format("Quite busy at %.1f", ev.time_ms/1000.0));
                }
                else if (delay > 1) {
                    // Now sleeping for "delay" milliseconds for exact timing of event
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // fire the event, after possible delay
                ev_handle(ev.type, ev.value);
            }
            for (int i = 0; i < 4; i++) {
                ev_handle(i,0);
            } // Stop everything.
            ConfigParser.getRings().off();
            is_running = false;
        }
    }
}
