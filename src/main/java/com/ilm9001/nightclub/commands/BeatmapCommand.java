package com.ilm9001.nightclub.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.beatmap.BeatmapPlayer;
import com.ilm9001.nightclub.beatmap.InfoData;
import com.ilm9001.nightclub.util.Util;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.ilm9001.nightclub.util.Util.formatErrors;

@CommandAlias("beatmap|bp")
@CommandPermission("nightclub.beatmap")
public class BeatmapCommand extends BaseCommand {
    
    @Getter private static BeatmapPlayer player;
    
    @Subcommand("play")
    @CommandAlias("p")
    @Description("Play a beatmap")
    @CommandCompletion("@beatmaps")
    @CommandPermission("nightclub.beatmap")
    public static void onPlay(CommandSender sender, String[] args) {
        List<CommandError> errors = LightUniverseCommand.isUnloaded(args, 1);
        errors.add(Util.getStringValuesFromArray(new File(Nightclub.getInstance().getDataFolder().getAbsolutePath()).listFiles(File::isDirectory))
                .stream().noneMatch(beatmap -> args[0].equals(beatmap)) ? CommandError.INVALID_ARGUMENT : CommandError.VALID);
        if (errors.stream().anyMatch(error -> error != CommandError.VALID)) {
            sender.sendMessage(formatErrors(errors));
            return;
        }
        player = new BeatmapPlayer(args[0]);
        ArrayList<Player> playTo = new ArrayList<>(Bukkit.getOnlinePlayers());
        InfoData info = player.play(playTo);
        String playBackMessage = ChatColor.GOLD + "Now Playing: " + ChatColor.AQUA + info.getSongAuthorName() + " - " + info.getSongName() + " " + info.getSongSubName() + System.lineSeparator()
                + ChatColor.GOLD + "Mapped by: " + ChatColor.AQUA + info.getMapperName();
        sender.sendMessage(playBackMessage);
    }
    
    @Subcommand("stop")
    @CommandAlias("s")
    @Description("Stop the current beatmap")
    @CommandPermission("nightclub.beatmap")
    public static void onStop(CommandSender sender) {
        List<CommandError> errors = LightUniverseCommand.isUnloaded();
        errors.add(player == null || !player.isPlaying() ? CommandError.NO_BEATMAP_IS_PLAYING : CommandError.VALID);
        if (errors.stream().anyMatch(error -> error != CommandError.VALID && error != CommandError.BEATMAP_PLAYING)) {
            sender.sendMessage(formatErrors(errors));
            return;
        }
        player.stop();
    }
    
}
