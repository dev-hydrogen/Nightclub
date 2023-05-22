package exposed.hydrogen.nightclub.beatmap.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import exposed.hydrogen.nightclub.util.Easing;

import java.util.ArrayList;
import java.util.List;

public class ColorPointDefinition {
    double r, g, b, a, t;
    Easing easing;
    public ColorPointDefinition(JsonArray arr) {
        r = arr.get(0).getAsDouble();
        g = arr.get(1).getAsDouble();
        b = arr.get(2).getAsDouble();
        a = arr.get(3).getAsDouble();
        t = arr.get(4).getAsDouble();
        easing = arr.size() == 6 ? Easing.valueOf(arr.get(5).getAsString()) : Easing.easeLinear;
    }
    public static List<ColorPointDefinition> fromArray(JsonElement arr) {
        if(arr == null) return List.of();
        if(arr.isJsonPrimitive()) return List.of(); //TODO: support pre-set point definitions
        List<ColorPointDefinition> result = new ArrayList<>();
        for(JsonElement element : arr.getAsJsonArray()) {
            var array = element.getAsJsonArray();
            result.add(new ColorPointDefinition(array));
        }
        return result;
    }
}