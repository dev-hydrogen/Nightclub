package exposed.hydrogen.nightclub.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import exposed.hydrogen.nightclub.Nightclub;
import exposed.hydrogen.nightclub.beatmap.BeatmapParser;
import exposed.hydrogen.nightclub.beatmap.BeatmapPlayer;
import exposed.hydrogen.nightclub.util.Util;
import lombok.Getter;
import team.unnamed.creative.sound.Sound;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@CommandAlias("beatmap")
@CommandPermission("nightclub.beatmap")
public class BeatmapCommand extends BaseCommand {

    @Getter private static BeatmapPlayer player;


    @Subcommand("play")
    @Description("Play a beatmap")
    @CommandCompletion("@beatmaps")
    @CommandPermission("nightclub.beatmap")
    public static void onPlay(CommandIssuer sender, String[] args) {
        Runnable task = () -> {
            try {
                List<CommandError> errors = LightUniverseCommand.isUnloaded(args, 1);

                errors.add(Util.getStringValuesFromArray(new File(Nightclub.DATA_FOLDER.getAbsolutePath()).listFiles(File::isDirectory))
                        .stream().noneMatch(beatmap -> args[0].equals(beatmap)) ? CommandError.INVALID_ARGUMENT : CommandError.VALID);

                errors.add(args.length >= 2 && !(args[1].toLowerCase().contains("true") || args[1].toLowerCase().contains("false")) ? CommandError.INVALID_ARGUMENT : CommandError.VALID);
                if (errors.stream().anyMatch(error -> error != CommandError.VALID)) {
                    sender.sendMessage(Util.formatErrors(errors));
                    return;
                }
                var info = BeatmapParser.getInfoData(args[0], true);
                var sounds = Util.getSounds(new File(Nightclub.DATA_FOLDER.getAbsolutePath()));
                var sound = (Sound.File) sounds.get(info).values().toArray()[0];
                player = new BeatmapPlayer(
                        sound.key().asString(),
                        args[0],
                        args.length >= 2 && Boolean.parseBoolean(args[1]),
                        Nightclub.getLightUniverseManager().getLoadedUniverse());

                player.play(Nightclub.getCrossCompatUtil().getListOfPlayers());
                String playBackMessage = "\u00A76" + "Now Playing: " + "\u00A7b" + info.getSongAuthorName() + " - " + info.getSongName() + " " + info.getSongSubName() + System.lineSeparator()
                        + "\u00A76" + "Mapped by: " + "\u00A7b" + info.getMapperName();
                sender.sendMessage(playBackMessage);
            } catch (Exception e) {
                Nightclub.getChameleon().getLogger().error("An error occurred while playing a beatmap", e);
            }
        };
        Executors.newSingleThreadScheduledExecutor().schedule(task, 100, TimeUnit.MILLISECONDS);
    }

    @Subcommand("stop")
    @Description("Stop the current beatmap")
    @CommandPermission("nightclub.beatmap")
    public static void onStop(CommandIssuer sender) {
        List<CommandError> errors = LightUniverseCommand.isUnloaded();
        errors.add(player == null || !player.isPlaying() ? CommandError.NO_BEATMAP_IS_PLAYING : CommandError.VALID);
        if (errors.stream().anyMatch(error -> error != CommandError.VALID && error != CommandError.BEATMAP_PLAYING)) {
            sender.sendMessage(Util.formatErrors(errors));
            return;
        }
        player.stop();
    }

}
