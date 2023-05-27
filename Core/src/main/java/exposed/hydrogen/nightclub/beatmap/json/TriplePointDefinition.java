package exposed.hydrogen.nightclub.beatmap.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import exposed.hydrogen.nightclub.util.Easing;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TriplePointDefinition {
    double x, y, z, t;
    Easing easing;

    public TriplePointDefinition(JsonArray arr) {
        x = Math.max(Math.min(arr.get(0).getAsDouble(),50),-50);
        y = Math.max(Math.min(arr.get(1).getAsDouble(),20),-20);
        z = Math.max(Math.min(arr.get(2).getAsDouble(),50),-50);
        t = arr.size() >= 4 ? arr.get(3).getAsDouble() : 0;
        easing = arr.size() == 5 ? Easing.value(arr.get(4).getAsString()) : Easing.easeLinear;
    }
    public static List<TriplePointDefinition> fromArray(JsonElement arr) {
        if(arr == null) return List.of();
        if(arr.isJsonPrimitive()) return List.of(); //TODO: support pre-set point definitions
        List<TriplePointDefinition> result = new ArrayList<>();
        if(arr.getAsJsonArray().get(0).isJsonPrimitive()) return List.of(new TriplePointDefinition(arr.getAsJsonArray()));
        for(JsonElement element : arr.getAsJsonArray()) {
            var array = element.getAsJsonArray();
            result.add(new TriplePointDefinition(array));
        }
        return result;
    }
}