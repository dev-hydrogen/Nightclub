package exposed.hydrogen.nightclub.light.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LightData implements Cloneable {
    private LightPatternData patternData;
    private LightPatternData secondPatternData;
    private ArrayList<Integer> lightIDs;
    private RingMovementData ringMovementData;
    private double maxLength;
    private double onLength; // 0 to 100, percentage of maxLength
    private int timeToFadeToBlack; // x * 100 ms
    private int lightCount;
    private boolean flipStartAndEnd; // flipped start and end makes downward pointing beams brighter, upward pointing beams less bright
    private boolean isDuplicate; // is this laser a duplicate; does it need to be cleared when beatmap finishes

    //Auto-generated clone method
    @Override
    public LightData clone() {
        try {
            return (LightData) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
