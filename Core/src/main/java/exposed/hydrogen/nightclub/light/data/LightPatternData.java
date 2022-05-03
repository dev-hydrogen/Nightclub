package exposed.hydrogen.nightclub.light.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LightPatternData {
    private LightPattern pattern;
    private double speed;
    private double patternSizeMultiplier;
    private double rotation;
    private double startX;
}
