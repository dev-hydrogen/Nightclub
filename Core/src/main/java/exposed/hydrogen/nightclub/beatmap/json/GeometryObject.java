package exposed.hydrogen.nightclub.beatmap.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import exposed.hydrogen.nightclub.beatmap.Track;
import exposed.hydrogen.nightclub.commands.BeatmapCommand;
import exposed.hydrogen.nightclub.util.Location;
import exposed.hydrogen.nightclub.util.Util;

import java.awt.*;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

import static exposed.hydrogen.nightclub.util.Location.fromJsonArray;

public record GeometryObject(
        Track track,
        Optional<Color> color,
        Location scale,
        Location position,
        Location rotation,
        GeometryType geometryType,
        GeometryMaterial geometryMaterial,
        Optional<Integer> count
) {

    public static GeometryObject fromObject(JsonObject obj) {
        String track = obj.get("_track").getAsString();
        Track trackObj = BeatmapCommand.getPlayer().getTrackRegistry()
                .stream()
                .filter(trck -> trck.name().equals(track))
                .findFirst()
                .orElse(new Track(track, new ArrayList<>(), new ArrayList<>(),null));
        JsonArray color = obj.getAsJsonArray("_color");
        Color colorObj = null;
        if(color != null) {
            colorObj = Util.translateBeatSaberColor(color);
        }
        JsonObject geometry = obj.getAsJsonObject("_geometry");
        String geometryTypeStr = geometry.get("_type").getAsString();
        String geometryMaterialStr = geometry.get("_material").getAsString();
        GeometryType geometryType = GeometryType.valueOf(geometryTypeStr.toUpperCase(Locale.ROOT));
        GeometryMaterial geometryMaterial = GeometryMaterial.valueOf(geometryMaterialStr.toUpperCase(Locale.ROOT));

        Location scale = fromJsonArray(obj.getAsJsonArray("_scale"));
        Location position = fromJsonArray(obj.getAsJsonArray("_position"));
        Location rotation = fromJsonArray(obj.getAsJsonArray("_rotation"));

        JsonElement count = obj.get("_count");
        Integer integerObj = null;
        if (count != null) {
            integerObj = count.getAsInt();
        }
        return new GeometryObject(
                trackObj,
                Optional.ofNullable(colorObj),
                scale,
                position,
                rotation,
                geometryType,
                geometryMaterial,
                Optional.ofNullable(integerObj)
        );
    }

    public enum GeometryType {
        SPHERE("{\"meta\":{\"format_version\":\"4.0\",\"model_format\":\"java_block\",\"box_uv\":false},\"name\":\"cube\",\"parent\":\"\",\"ambientocclusion\":true,\"front_gui_light\":false,\"visible_box\":[1,1,0],\"variable_placeholders\":\"\",\"variable_placeholder_buttons\":[],\"resolution\":{\"width\":16,\"height\":16},\"elements\":[{\"name\":\"cube\",\"rescale\":false,\"locked\":false,\"from\":[0,0,0],\"to\":[16,16,16],\"autouv\":1,\"color\":1,\"origin\":[0,0,0],\"faces\":{\"north\":{\"uv\":[0,0,16,16]},\"east\":{\"uv\":[0,0,16,16]},\"south\":{\"uv\":[0,0,16,16]},\"west\":{\"uv\":[0,0,16,16]},\"up\":{\"uv\":[0,0,16,16]},\"down\":{\"uv\":[0,0,16,16]}},\"type\":\"cube\",\"uuid\":\"e19b46fa-1903-f98d-aa04-6f8f7740654e\"}],\"outliner\":[\"e19b46fa-1903-f98d-aa04-6f8f7740654e\"],\"textures\":[]}"),
        CAPSULE("{\"meta\":{\"format_version\":\"4.0\",\"model_format\":\"java_block\",\"box_uv\":false},\"name\":\"cylinder\",\"parent\":\"\",\"ambientocclusion\":true,\"front_gui_light\":false,\"visible_box\":[1,1,0],\"variable_placeholders\":\"\",\"variable_placeholder_buttons\":[],\"resolution\":{\"width\":16,\"height\":16},\"elements\":[{\"name\":\"cube\",\"rescale\":false,\"locked\":false,\"from\":[0,0,0],\"to\":[16,16,16],\"autouv\":1,\"color\":1,\"origin\":[0,0,0],\"faces\":{\"north\":{\"uv\":[0,0,16,16]},\"east\":{\"uv\":[0,0,16,16]},\"south\":{\"uv\":[0,0,16,16]},\"west\":{\"uv\":[0,0,16,16]},\"up\":{\"uv\":[0,0,16,16]},\"down\":{\"uv\":[0,0,16,16]}},\"type\":\"cube\",\"uuid\":\"e19b46fa-1903-f98d-aa04-6f8f7740654e\"},{\"name\":\"cube\",\"rescale\":false,\"locked\":false,\"from\":[0,0,0],\"to\":[16,16,16],\"autouv\":1,\"color\":1,\"rotation\":[0,-45,0],\"origin\":[8,8,8],\"faces\":{\"north\":{\"uv\":[0,0,16,16]},\"east\":{\"uv\":[0,0,16,16]},\"south\":{\"uv\":[0,0,16,16]},\"west\":{\"uv\":[0,0,16,16]},\"up\":{\"uv\":[0,0,16,16]},\"down\":{\"uv\":[0,0,16,16]}},\"type\":\"cube\",\"uuid\":\"4aa05e33-a88a-5487-2439-25b52c73f38b\"},{\"name\":\"cube\",\"rescale\":false,\"locked\":false,\"from\":[0,0,0],\"to\":[16,16,16],\"autouv\":1,\"color\":1,\"rotation\":[0,22.5,0],\"origin\":[8,8,8],\"faces\":{\"north\":{\"uv\":[0,0,16,16]},\"east\":{\"uv\":[0,0,16,16]},\"south\":{\"uv\":[0,0,16,16]},\"west\":{\"uv\":[0,0,16,16]},\"up\":{\"uv\":[0,0,16,16],\"rotation\":90},\"down\":{\"uv\":[0,0,16,16],\"rotation\":270}},\"type\":\"cube\",\"uuid\":\"5863780a-edd9-d100-7e2e-c87812d153d4\"},{\"name\":\"cube\",\"rescale\":false,\"locked\":false,\"from\":[0,0,0],\"to\":[16,16,16],\"autouv\":1,\"color\":1,\"rotation\":[0,-22.5,0],\"origin\":[8,8,8],\"faces\":{\"north\":{\"uv\":[0,0,16,16]},\"east\":{\"uv\":[0,0,16,16]},\"south\":{\"uv\":[0,0,16,16]},\"west\":{\"uv\":[0,0,16,16]},\"up\":{\"uv\":[0,0,16,16],\"rotation\":90},\"down\":{\"uv\":[0,0,16,16],\"rotation\":270}},\"type\":\"cube\",\"uuid\":\"1bd8de13-859f-42fc-da78-3ecf91ca80fa\"}],\"outliner\":[\"e19b46fa-1903-f98d-aa04-6f8f7740654e\",\"4aa05e33-a88a-5487-2439-25b52c73f38b\",\"5863780a-edd9-d100-7e2e-c87812d153d4\",\"1bd8de13-859f-42fc-da78-3ecf91ca80fa\"],\"textures\":[]}"),
        CYLINDER("{\"meta\":{\"format_version\":\"4.0\",\"model_format\":\"java_block\",\"box_uv\":false},\"name\":\"cylinder\",\"parent\":\"\",\"ambientocclusion\":true,\"front_gui_light\":false,\"visible_box\":[1,1,0],\"variable_placeholders\":\"\",\"variable_placeholder_buttons\":[],\"resolution\":{\"width\":16,\"height\":16},\"elements\":[{\"name\":\"cube\",\"rescale\":false,\"locked\":false,\"from\":[0,0,0],\"to\":[16,16,16],\"autouv\":1,\"color\":1,\"origin\":[0,0,0],\"faces\":{\"north\":{\"uv\":[0,0,16,16]},\"east\":{\"uv\":[0,0,16,16]},\"south\":{\"uv\":[0,0,16,16]},\"west\":{\"uv\":[0,0,16,16]},\"up\":{\"uv\":[0,0,16,16]},\"down\":{\"uv\":[0,0,16,16]}},\"type\":\"cube\",\"uuid\":\"e19b46fa-1903-f98d-aa04-6f8f7740654e\"},{\"name\":\"cube\",\"rescale\":false,\"locked\":false,\"from\":[0,0,0],\"to\":[16,16,16],\"autouv\":1,\"color\":1,\"rotation\":[0,-45,0],\"origin\":[8,8,8],\"faces\":{\"north\":{\"uv\":[0,0,16,16]},\"east\":{\"uv\":[0,0,16,16]},\"south\":{\"uv\":[0,0,16,16]},\"west\":{\"uv\":[0,0,16,16]},\"up\":{\"uv\":[0,0,16,16]},\"down\":{\"uv\":[0,0,16,16]}},\"type\":\"cube\",\"uuid\":\"4aa05e33-a88a-5487-2439-25b52c73f38b\"},{\"name\":\"cube\",\"rescale\":false,\"locked\":false,\"from\":[0,0,0],\"to\":[16,16,16],\"autouv\":1,\"color\":1,\"rotation\":[0,22.5,0],\"origin\":[8,8,8],\"faces\":{\"north\":{\"uv\":[0,0,16,16]},\"east\":{\"uv\":[0,0,16,16]},\"south\":{\"uv\":[0,0,16,16]},\"west\":{\"uv\":[0,0,16,16]},\"up\":{\"uv\":[0,0,16,16],\"rotation\":90},\"down\":{\"uv\":[0,0,16,16],\"rotation\":270}},\"type\":\"cube\",\"uuid\":\"5863780a-edd9-d100-7e2e-c87812d153d4\"},{\"name\":\"cube\",\"rescale\":false,\"locked\":false,\"from\":[0,0,0],\"to\":[16,16,16],\"autouv\":1,\"color\":1,\"rotation\":[0,-22.5,0],\"origin\":[8,8,8],\"faces\":{\"north\":{\"uv\":[0,0,16,16]},\"east\":{\"uv\":[0,0,16,16]},\"south\":{\"uv\":[0,0,16,16]},\"west\":{\"uv\":[0,0,16,16]},\"up\":{\"uv\":[0,0,16,16],\"rotation\":90},\"down\":{\"uv\":[0,0,16,16],\"rotation\":270}},\"type\":\"cube\",\"uuid\":\"1bd8de13-859f-42fc-da78-3ecf91ca80fa\"}],\"outliner\":[\"e19b46fa-1903-f98d-aa04-6f8f7740654e\",\"4aa05e33-a88a-5487-2439-25b52c73f38b\",\"5863780a-edd9-d100-7e2e-c87812d153d4\",\"1bd8de13-859f-42fc-da78-3ecf91ca80fa\"],\"textures\":[]}"),
        CUBE("{\"meta\":{\"format_version\":\"4.0\",\"model_format\":\"java_block\",\"box_uv\":false},\"name\":\"cube\",\"parent\":\"\",\"ambientocclusion\":true,\"front_gui_light\":false,\"visible_box\":[1,1,0],\"variable_placeholders\":\"\",\"variable_placeholder_buttons\":[],\"resolution\":{\"width\":16,\"height\":16},\"elements\":[{\"name\":\"cube\",\"rescale\":false,\"locked\":false,\"from\":[0,0,0],\"to\":[16,16,16],\"autouv\":1,\"color\":1,\"origin\":[0,0,0],\"faces\":{\"north\":{\"uv\":[0,0,16,16]},\"east\":{\"uv\":[0,0,16,16]},\"south\":{\"uv\":[0,0,16,16]},\"west\":{\"uv\":[0,0,16,16]},\"up\":{\"uv\":[0,0,16,16]},\"down\":{\"uv\":[0,0,16,16]}},\"type\":\"cube\",\"uuid\":\"e19b46fa-1903-f98d-aa04-6f8f7740654e\"}],\"outliner\":[\"e19b46fa-1903-f98d-aa04-6f8f7740654e\"],\"textures\":[]}"),
        PLANE("{\"meta\":{\"format_version\":\"4.0\",\"model_format\":\"java_block\",\"box_uv\":false},\"name\":\"plane\",\"parent\":\"\",\"ambientocclusion\":true,\"front_gui_light\":false,\"visible_box\":[1,1,0],\"variable_placeholders\":\"\",\"variable_placeholder_buttons\":[],\"resolution\":{\"width\":16,\"height\":16},\"elements\":[{\"name\":\"cube\",\"rescale\":false,\"locked\":false,\"from\":[0,0,0],\"to\":[16,0,16],\"autouv\":1,\"color\":1,\"origin\":[0,0,0],\"faces\":{\"north\":{\"uv\":[0,0,16,0]},\"east\":{\"uv\":[0,0,16,0]},\"south\":{\"uv\":[0,0,16,0]},\"west\":{\"uv\":[0,0,16,0]},\"up\":{\"uv\":[0,0,16,16]},\"down\":{\"uv\":[0,0,16,16]}},\"type\":\"cube\",\"uuid\":\"e19b46fa-1903-f98d-aa04-6f8f7740654e\"}],\"outliner\":[\"e19b46fa-1903-f98d-aa04-6f8f7740654e\"],\"textures\":[]}"),
        QUAD("{\"meta\":{\"format_version\":\"4.0\",\"model_format\":\"java_block\",\"box_uv\":false},\"name\":\"quad\",\"parent\":\"\",\"ambientocclusion\":true,\"front_gui_light\":false,\"visible_box\":[1,1,0],\"variable_placeholders\":\"\",\"variable_placeholder_buttons\":[],\"resolution\":{\"width\":16,\"height\":16},\"elements\":[{\"name\":\"cube\",\"rescale\":false,\"locked\":false,\"from\":[8,0,0],\"to\":[8,16,16],\"autouv\":1,\"color\":1,\"origin\":[8,8,8],\"faces\":{\"north\":{\"uv\":[0,0,0,16]},\"east\":{\"uv\":[0,0,16,16]},\"south\":{\"uv\":[0,0,0,16]},\"west\":{\"uv\":[0,0,16,16]},\"up\":{\"uv\":[0,0,16,0],\"rotation\":90},\"down\":{\"uv\":[0,0,16,0],\"rotation\":270}},\"type\":\"cube\",\"uuid\":\"1bd8de13-859f-42fc-da78-3ecf91ca80fa\"}],\"outliner\":[\"1bd8de13-859f-42fc-da78-3ecf91ca80fa\"],\"textures\":[]}");

        String bbmodel;
        GeometryType(String bbmodel) {
            this.bbmodel = bbmodel;
        }
    }
    public enum GeometryMaterial {
        CUBE("iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAB1JREFUOI1jzMzK+s9AAWCiRPOoAaMGjBowmAwAAFLgAlyC2shtAAAAAElFTkSuQmCC");

        String png;
        GeometryMaterial(String base64png) {
            png = base64png;
        }
    }
}
