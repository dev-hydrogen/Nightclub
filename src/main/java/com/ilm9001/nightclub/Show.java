package com.ilm9001.nightclub;

import com.ilm9001.nightclub.lights.Lights;
import com.ilm9001.nightclub.parse.LE;
import com.ilm9001.nightclub.parse.LE_list;

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
    
    
    private void ev_handle(int ty, int va) {
        switch (ty) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                switch (va) {
                    case 0:
                        Lights.TOPDOWN_CIRCLE.getLight().off();
                        Lights.TOPDOWNDOUBLE_CIRCLE.getLight().off();
                        break;
                    case 1:
                    case 5:
                        Lights.TOPDOWN_CIRCLE.getLight().on();
                        Lights.TOPDOWNDOUBLE_CIRCLE.getLight().on();
                        break;
                    case 2:
                    case 6:
                        Lights.TOPDOWN_CIRCLE.getLight().flash();
                        Lights.TOPDOWNDOUBLE_CIRCLE.getLight().flash();
                        break;
                    case 3:
                    case 7:
                        Lights.TOPDOWN_CIRCLE.getLight().flashOff();
                        Lights.TOPDOWNDOUBLE_CIRCLE.getLight().flashOff();
                        break;
                    default:
                        break;
                }
            case 8:
            case 9:
            case 12:
            case 13:
            default:
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
            for(Lights lght : Lights.values()) { lght.getLight().setRunning(true); }
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
            //stg.Stop();
            for(Lights lght : Lights.values()) { lght.getLight().setRunning(false); }
            is_running = false;
        }
    }
}
