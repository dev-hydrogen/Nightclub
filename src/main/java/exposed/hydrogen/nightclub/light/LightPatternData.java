package exposed.hydrogen.nightclub.light;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LightPatternData {
    private LightPattern pattern;
    private double speed;
    private double patternSizeMultiplier;
    private double rotation;
    private double startX;
}
