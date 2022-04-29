package exposed.hydrogen.nightclub.light.data;

import com.google.gson.InstanceCreator;
import exposed.hydrogen.nightclub.util.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Type;
import java.util.ArrayList;

@Data
@AllArgsConstructor
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

    //Auto-generated clone method
    @Override
    public LightData clone() {
        try {
            return (LightData) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public static class LightDataInstanceCreator implements InstanceCreator<LightData> {
        public LightData createInstance(Type type) {
            return new LightData(
                    new LightPatternData(LightPattern.CIRCLE, 0, 0, 0, 0), new LightPatternData(LightPattern.STILL,
                    0, 0, 0, 0), new ArrayList<>(), new RingMovementData(new Location(), 1, 1), 0, 0, 0,
                    0, false);
        }
    }
}
