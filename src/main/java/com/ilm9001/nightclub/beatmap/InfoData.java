package com.ilm9001.nightclub.beatmap;

import lombok.Builder;
import lombok.Getter;

public class InfoData {
    @Getter private final Number beatsPerMinute;
    @Getter private final String songAuthorName;
    @Getter private final String songName;
    @Getter private final String mapperName;
    
    @Builder
    public InfoData(Number bpm, String author, String song, String mapper) {
        this.beatsPerMinute = bpm;
        this.songAuthorName = author;
        this.songName = song;
        this.mapperName = mapper;
    }
}
