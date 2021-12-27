package com.ilm9001.nightclub.commands;

import lombok.Getter;

public enum CommandError {
    LIGHT_UNLOADED("- There is no Light loaded"),
    LIGHTUNIVERSE_UNLOADED("- There is no LightUniverse loaded"),
    BEATMAP_PLAYING("- A beatmap is currently playing"),
    NO_BEATMAP_IS_PLAYING("- A beatmap is not currently playing"),
    TOO_LITTLE_ARGUMENTS("- Not enough arguments were provided in the command"),
    INVALID_ARGUMENT("- An argument was invalid"),
    NAME_ALREADY_EXISTS("- The name provided is already in use"),
    COMMAND_SENT_FROM_CONSOLE("- Command can only be executed as a player"),
    VALID("");
    
    @Getter private final String errorMessage;
    CommandError(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
}
