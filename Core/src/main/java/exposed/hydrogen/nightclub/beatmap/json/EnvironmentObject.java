package exposed.hydrogen.nightclub.beatmap.json;

import com.google.gson.JsonObject;
import exposed.hydrogen.nightclub.beatmap.Track;
import exposed.hydrogen.nightclub.commands.BeatmapCommand;
import exposed.hydrogen.nightclub.util.Location;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

import static exposed.hydrogen.nightclub.util.Location.fromJsonArray;
//@Data
public record EnvironmentObject(String id,
                                LookupMethod lookupMethod,
                                Optional<Integer> duplicate,
                                Optional<Boolean> active,
                                Optional<Location> scale,
                                Optional<Location> position,
                                Optional<Location> localPosition,
                                Optional<Location> rotation,
                                Optional<Location> localRotation,
                                Optional<Integer> lightID,
                                Optional<Track> track) {
    public static EnvironmentObject fromObject(JsonObject obj) {
        if(obj.get("_geometry") != null) throw new IllegalArgumentException("_geometry objects not allowed");
        if(BeatmapCommand.getPlayer() == null) throw new IllegalStateException("Beatmap player is null");
        String id = obj.get("_id").getAsString();
        LookupMethod lookupMethod = LookupMethod.valueOf(obj.get("_lookupMethod").getAsString().toUpperCase(Locale.ROOT));
        JsonObject duplicateObj = obj.getAsJsonObject("_duplicate");
        JsonObject activeObj = obj.getAsJsonObject("_active");
        Integer duplicate = null;
        Boolean active = null;
        if(duplicateObj != null) {
            duplicate = duplicateObj.getAsInt();
        }
        if(activeObj != null) {
            active = activeObj.getAsBoolean();
        }
        Location scale = fromJsonArray(obj.getAsJsonArray("_scale"));
        Location position = fromJsonArray(obj.getAsJsonArray("_position"));
        Location localPosition = fromJsonArray(obj.getAsJsonArray("_localPosition"));
        Location rotation = fromJsonArray(obj.getAsJsonArray("_rotation"));
        Location localRotation = fromJsonArray(obj.getAsJsonArray("_localRotation"));

        JsonObject lightIDObj = obj.getAsJsonObject("_lightID");
        JsonObject track = obj.getAsJsonObject("_track");
        Integer lightID = null;
        Track trackObj;
        if(lightIDObj != null) {
            lightID = lightIDObj.getAsInt();
        }
        if(track != null) {
            String trackStr = track.getAsString();
            trackObj = BeatmapCommand.getPlayer().getTrackRegistry()
                    .stream()
                    .filter(trck -> trck.name().equals(trackStr))
                    .findFirst()
                    .orElse(new Track(trackStr, new ArrayList<>(), new ArrayList<>(),null));
        } else {
            trackObj = null;
        }

        return new EnvironmentObject(
                id,
                lookupMethod,
                Optional.ofNullable(duplicate),
                Optional.ofNullable(active),
                Optional.ofNullable(scale),
                Optional.ofNullable(position),
                Optional.ofNullable(localPosition),
                Optional.ofNullable(rotation),
                Optional.ofNullable(localRotation),
                Optional.ofNullable(lightID),
                Optional.ofNullable(trackObj)
        );
    }
    public enum LookupMethod {
        REGEX, EXACT, CONTAINS
    }
}
