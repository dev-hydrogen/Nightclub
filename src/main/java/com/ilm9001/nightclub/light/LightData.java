package com.ilm9001.nightclub.light;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LightData {
    private LightPattern pattern;
    private LightPattern secondPattern;
    private double maxLength;
    private double onLength; // 0 to 100, percentage of maxLength
    private double speed;
    private double secondarySpeed;
    private double patternSizeMultiplier;
    private double secondaryPatternSizeMultiplier;
    private int timeToFadeToBlack; // x * 100 ms
    private int lightCount;
    private boolean flipStartAndEnd; // flipped start and end makes downward pointing beams brighter, upward pointing beams less bright
    private double rotation;
    private double secondaryRotation;
}
