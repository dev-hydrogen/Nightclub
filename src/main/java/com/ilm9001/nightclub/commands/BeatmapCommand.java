package com.ilm9001.nightclub.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.ilm9001.nightclub.beatmap.BeatmapPlayer;
import org.bukkit.Bukkit;

import java.util.ArrayList;

@CommandAlias("beatmap|bp")
public class BeatmapCommand extends BaseCommand {
    
    private static BeatmapPlayer player;
    
    @Subcommand("play")
    @CommandAlias("p")
    @Description("Play a beatmap")
    @CommandCompletion("@beatmaps")
    public static void onPlay(String[] args) {
        player = new BeatmapPlayer(args[0]);
        player.play(new ArrayList<>(Bukkit.getOnlinePlayers()));
    }
    
    @Subcommand("stop")
    @CommandAlias("s")
    @Description("Stop the current beatmap")
    public static void onStop() {
        player.stop();
    }
    
}
