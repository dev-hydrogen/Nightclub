package com.ilm9001.nightclub.beatmap;

import lombok.Builder;
import lombok.Getter;

/**
 * This class provides extra information about a beatmap through its info.dat file
 * BPM, Artist Name, Song Name, Song Sub Name and Level Author Name are all stored in this class.
 */

public class InfoData {
    @Getter private final Number beatsPerMinute;
    @Getter private final String songAuthorName;
    @Getter private final String songName;
    @Getter private final String songSubName;
    @Getter private final String mapperName;
    @Getter private final String beatmapFileName;
    @Getter private final boolean isChroma;
    
    @Builder
    public InfoData(Number bpm, String author, String song, String songSubName, String mapper, String beatmapFileName, boolean isChroma) {
        this.beatsPerMinute = bpm;
        this.songAuthorName = author;
        this.songName = song;
        this.mapperName = mapper;
        this.songSubName = songSubName;
        this.beatmapFileName = beatmapFileName;
        this.isChroma = isChroma;
    }
}
