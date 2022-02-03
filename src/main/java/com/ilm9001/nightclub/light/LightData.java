package com.ilm9001.nightclub.light;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@Builder
public class LightData implements Cloneable {
    private LightPatternData patternData;
    private LightPatternData secondPatternData;
    private ArrayList<Integer> lightIDs;
    private double maxLength;
    private double onLength; // 0 to 100, percentage of maxLength
    private int timeToFadeToBlack; // x * 100 ms
    private int lightCount;
    private boolean flipStartAndEnd; // flipped start and end makes downward pointing beams brighter, upward pointing beams less bright
    //Auto-generated clone method
    @Override
    public LightData clone() {
        try {
            LightData clone = (LightData) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
