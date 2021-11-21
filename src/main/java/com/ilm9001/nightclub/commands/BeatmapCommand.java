package com.ilm9001.nightclub.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ilm9001.nightclub.beatmap.BeatmapPlayer;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.ArrayList;

@CommandAlias("beatmap|bp")
@CommandPermission("nightclub.beatmap")
public class BeatmapCommand extends BaseCommand {
    
    @Getter private static BeatmapPlayer player;
    
    @Subcommand("play")
    @CommandAlias("p")
    @Description("Play a beatmap")
    @CommandCompletion("@beatmaps")
    @CommandPermission("nightclub.beatmap")
    public static void onPlay(String[] args) {
        if (player != null && player.isPlaying()) {
            return;
        }
        player = new BeatmapPlayer(args[0]);
        player.play(new ArrayList<>(Bukkit.getOnlinePlayers()));
    }
    
    @Subcommand("stop")
    @CommandAlias("s")
    @Description("Stop the current beatmap")
    @CommandPermission("nightclub.beatmap")
    public static void onStop() {
        player.stop();
    }
    
}
