package com.ilm9001.nightclub.parse;

import org.json.simple.JSONObject;

public class LE {
    public long time_ms;
    public int type;
    public int value;
    
    // The constructor checks the fields of given JSON object
    // and makes the necessary type conversions to store them
    // into more easily handled datatypes.
    public LE(JSONObject lightEvent, int bpm) {
        Object t = lightEvent.get("_time");
        double time_d;
        if (t instanceof Long) {
            time_d = ((Long) t).doubleValue();
        }
        else if (t instanceof Double){
            time_d = (Double)t;
        }
        else {
            time_d = (double)t;
        }
        time_ms = (long)(time_d * 1000.0 * 60.0 / bpm);
        long type_l = (long)lightEvent.get("_type");
        type = ((int) type_l);
        long value_l = (long) lightEvent.get("_value");
        value = ((int)value_l);
    }
}
