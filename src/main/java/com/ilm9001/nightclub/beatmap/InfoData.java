package com.ilm9001.nightclub.beatmap;

import lombok.Builder;
import lombok.Data;

import java.awt.*;

/**
 * This class provides extra information about a beatmap through its info.dat file
 * BPM, Artist Name, Song Name, Song Sub Name and Level Author Name are all stored in this class.
 */
@Data
public final class InfoData {
    private final Number beatsPerMinute;
    private final String songAuthorName;
    private final String songName;
    private final String songSubName;
    private final String mapperName;
    private final String beatmapFileName;
    private final boolean isChroma;
    private final Color primaryColor;
    private final Color secondaryColor;
    
    @Builder
    public InfoData(Number bpm, String author, String song, String songSubName, String mapper, String beatmapFileName, boolean isChroma, Color primaryColor, Color secondaryColor) {
        this.beatsPerMinute = bpm;
        this.songAuthorName = author;
        this.songName = song;
        this.mapperName = mapper;
        this.songSubName = songSubName;
        this.beatmapFileName = beatmapFileName;
        this.isChroma = isChroma;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
    }
}
