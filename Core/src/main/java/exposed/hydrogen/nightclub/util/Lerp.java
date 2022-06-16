package exposed.hydrogen.nightclub.util;

import java.awt.*;
import java.awt.color.ColorSpace;

public enum Lerp {
    HSV,
    RGB;

    public Color lerp(Lerp lerp, Color start, Color end, Number fraction, Easing easing) {
        return lerp == HSV ? hsvLerp(start,end,fraction,easing)
                : rgbLerp(start,end,fraction,easing);
    }
    private Color hsvLerp(Color start, Color end, Number fraction, Easing easing) {
        float[] startColor = ColorSpace.getInstance(ColorSpace.TYPE_HSV).fromRGB(start.getColorComponents(null));
        float[] endColor = ColorSpace.getInstance(ColorSpace.TYPE_HSV).fromRGB(end.getColorComponents(null));
        float[] lerpedColor = new float[3];
        for (int i = 0; i < 3; i++) {
            lerpedColor[i] = (float) Util.lerp(startColor[i],endColor[i],fraction,easing);
        }
        return Color.getHSBColor(lerpedColor[0],lerpedColor[1],lerpedColor[2]);
    }
    private Color rgbLerp(Color start, Color end, Number fraction, Easing easing) {
        var r = Util.lerp(start.getRed(),end.getRed(),fraction,easing);
        var g = Util.lerp(start.getGreen(),end.getGreen(),fraction,easing);
        var b = Util.lerp(start.getBlue(),end.getBlue(),fraction,easing);
        return new Color((int) r, (int) g, (int) b);
    }
}
