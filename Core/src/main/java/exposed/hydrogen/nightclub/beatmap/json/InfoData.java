package exposed.hydrogen.nightclub.beatmap.json;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.awt.*;

/**
 * This class provides extra information about a beatmap through its info.dat file
 * BPM, Artist Name, Song Name, Song Sub Name and Level Author Name are all stored in this class.
 */
@Data
@Builder
@AllArgsConstructor
public final class InfoData {
    private final Number beatsPerMinute;
    private final String songAuthorName;
    private final String songName;
    private final String songSubName;
    private final String mapperName;
    private final String beatmapFileName;
    private final String songFileName;
    private final boolean isChroma;
    private final Color primaryColor;
    private final Color secondaryColor;
}
