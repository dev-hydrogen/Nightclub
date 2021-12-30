package com.ilm9001.nightclub.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.light.*;
import com.ilm9001.nightclub.light.event.LightChannel;
import com.ilm9001.nightclub.light.event.LightSpeedChannel;
import com.ilm9001.nightclub.util.Location;
import com.ilm9001.nightclub.util.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.List;
import java.util.*;

import static com.ilm9001.nightclub.util.Util.formatErrors;

@CommandAlias("light|li")
@CommandPermission("nightclub.light")
public class LightCommand extends BaseCommand {
    private static Light light;
    
    private static List<CommandError> isUnloaded() {
        List<CommandError> errors = new ArrayList<>();
        errors.add(light == null ? CommandError.LIGHT_UNLOADED : CommandError.VALID);
        errors.add(Nightclub.getLightUniverseManager().getLoadedUniverse() == null ? CommandError.LIGHTUNIVERSE_UNLOADED : CommandError.VALID);
        errors.add(BeatmapCommand.getPlayer() != null && BeatmapCommand.getPlayer().isPlaying() ? CommandError.BEATMAP_PLAYING : CommandError.VALID);
        return errors;
    }
    private static List<CommandError> isUnloaded(String[] args, int minArgsLength) {
        List<CommandError> errors = isUnloaded();
        errors.add(args.length < minArgsLength ? CommandError.TOO_LITTLE_ARGUMENTS : CommandError.VALID);
        return errors;
    }
    
    @Subcommand("build")
    @CommandAlias("b")
    @Description("Build a new Light!")
    @CommandPermission("nightclub.light")
    public static void onBuild(Player player, CommandSender sender) {
        LightUniverseManager manager = Nightclub.getLightUniverseManager();
        
        List<CommandError> errors = isUnloaded();
        errors.add(player == null ? CommandError.COMMAND_SENT_FROM_CONSOLE : CommandError.VALID);
        if (errors.stream().anyMatch(error -> error != CommandError.VALID && error != CommandError.LIGHT_UNLOADED)) {
            sender.sendMessage(formatErrors(errors));
            return;
        }
        LightData data = LightData.builder()
                .patternData(new LightPatternData(LightPattern.LINE, 0.3, 5, 0))
                .secondPatternData(new LightPatternData(LightPattern.LINE, 0.2, 3, 0))
                .maxLength(15)
                .onLength(80)
                .timeToFadeToBlack(45)
                .lightCount(4)
                .flipStartAndEnd(player.getLocation().getPitch() > -10).build();
        light = Light.builder()
                .uuid(UUID.randomUUID())
                .name("Unnamed-Light-" + new Random().nextInt())
                .location(Location.getFromBukkitLocation(player.getLocation().add(0, 1, 0)))
                .type(LightType.GUARDIAN_BEAM)
                .channel(LightChannel.CENTER_LIGHTS)
                .speedChannel(LightSpeedChannel.RIGHT_ROTATING_LASERS)
                .data(data).build();
        light.start();
        light.on(new Color(0x0066ff));
        manager.getLoadedUniverse().addLight(light);
        manager.save();
    }
    
    @Subcommand("remove")
    @CommandAlias("r")
    @Description("Remove currently loaded Light")
    @CommandPermission("nightclub.light")
    public static void onRemove(CommandSender sender) {
        LightUniverseManager manager = Nightclub.getLightUniverseManager();
        if (isUnloaded().stream().anyMatch(error -> error != CommandError.VALID)) {
            sender.sendMessage(formatErrors(isUnloaded()));
            return;
        }
        light.unload();
        manager.getLoadedUniverse().removeLight(light);
        manager.save();
        light = null;
    }
    
    @Subcommand("load")
    @CommandAlias("l")
    @Description("Load a Light from currently loaded LightUniverse")
    @CommandCompletion("@lights")
    @CommandPermission("nightclub.light")
    public static void onLoad(CommandSender sender, String[] args) {
        LightUniverseManager manager = Nightclub.getLightUniverseManager();
        LightUniverse universe = manager.getLoadedUniverse();
        
        List<CommandError> errors = isUnloaded();
        errors.add(args.length < 1 ? CommandError.TOO_LITTLE_ARGUMENTS : CommandError.VALID);
        if (errors.stream().noneMatch(error -> error == CommandError.LIGHTUNIVERSE_UNLOADED)) {
            errors.add(universe.getLight(args[0]) == null ? CommandError.INVALID_ARGUMENT : CommandError.VALID);
        }
        if (errors.stream().anyMatch(error -> error != CommandError.VALID && error != CommandError.LIGHT_UNLOADED)) {
            sender.sendMessage(formatErrors(errors));
            return;
        }
        
        light = universe.getLight(args[0]);
        light.start();
        light.on(new Color(0x0066ff));
    }
    
    @Subcommand("data")
    @CommandAlias("d")
    @Description("Modify a Light's data")
    @CommandPermission("nightclub.light")
    public class LightDataCommand extends BaseCommand {
        
        @Subcommand("name")
        @CommandAlias("n")
        @Description("Alter Light Name")
        @CommandPermission("nightclub.light")
        public static void onNameChange(CommandSender sender, String[] args) {
            List<CommandError> errors = isUnloaded(args, 1);
            errors.add(Nightclub.getLightUniverseManager().getLoadedUniverse().getLights().stream().anyMatch(l -> Objects.equals(l.getName(), args[0]))
                    ? CommandError.NAME_ALREADY_EXISTS : CommandError.VALID);
            
            if (errors.stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(formatErrors(errors));
                return;
            }
            light.setName(args[0]);
        }
        
        @Subcommand("pattern")
        @CommandAlias("p")
        @Description("Alter pattern")
        @CommandCompletion("@pattern")
        @CommandPermission("nightclub.light")
        public static void onPattern(CommandSender sender, String[] args) {
            List<CommandError> errors = isUnloaded(args, 1);
            try {
                LightPattern.valueOf(args[0]);
            } catch (IllegalArgumentException e) {
                errors.add(CommandError.INVALID_ARGUMENT);
            }
            if (errors.stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(formatErrors(errors));
                return;
            }
            light.getData().getPatternData().setPattern(LightPattern.valueOf(args[0]));
            light.on(new Color(0x0066ff));
        }
        @Subcommand("secondarypattern")
        @CommandAlias("sp")
        @Description("Alter secondary pattern")
        @CommandCompletion("@pattern")
        @CommandPermission("nightclub.light")
        public static void onSecondPattern(CommandSender sender, String[] args) {
            List<CommandError> errors = isUnloaded(args, 1);
            try {
                LightPattern.valueOf(args[0]);
            } catch (IllegalArgumentException e) {
                errors.add(CommandError.INVALID_ARGUMENT);
            }
            if (errors.stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(formatErrors(errors));
                return;
            }
            light.getData().getPatternData().setPattern(LightPattern.valueOf(args[0]));
            light.on(new Color(0x0066ff));
        }
        
        @Subcommand("maxlength")
        @CommandAlias("ml")
        @Description("Alter max length multiplier")
        @CommandPermission("nightclub.light")
        public static void onMaxLength(CommandSender sender, String[] args) {
            if (isUnloaded(args, 1).stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(formatErrors(isUnloaded(args, 1)));
                return;
            }
            light.getData().setMaxLength(Util.parseNumber(args[0]).doubleValue());
            light.on(new Color(0x0066ff));
        }
        
        @Subcommand("onlength")
        @CommandAlias("ol")
        @Description("Alter the on length percentage")
        @CommandPermission("nightclub.light")
        public static void onModifyOnLength(CommandSender sender, String[] args) {
            if (isUnloaded(args, 1).stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(formatErrors(isUnloaded(args, 1)));
                return;
            }
            light.getData().setOnLength(Util.parseNumber(args[0]).doubleValue());
            light.on(new Color(0x0066ff));
        }
        
        @Subcommand("patternmultiplier")
        @CommandAlias("pm")
        @Description("Alter the pattern size multiplier")
        @CommandPermission("nightclub.light")
        public static void onModifyPatternMultiplier(CommandSender sender, String[] args) {
            if (isUnloaded(args, 1).stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(formatErrors(isUnloaded(args, 1)));
                return;
            }
            light.getData().getPatternData().setPatternSizeMultiplier(Util.parseNumber(args[0]).doubleValue());
            light.on(new Color(0x0066ff));
        }
        @Subcommand("secondarypatternmultiplier")
        @CommandAlias("spm")
        @Description("Alter the secondary pattern size multiplier")
        @CommandPermission("nightclub.light")
        public static void onModifySecondaryPatternMultiplier(CommandSender sender, String[] args) {
            if (isUnloaded(args, 1).stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(formatErrors(isUnloaded(args, 1)));
                return;
            }
            light.getData().getPatternData().setPatternSizeMultiplier(Util.parseNumber(args[0]).doubleValue());
            light.on(new Color(0x0066ff));
        }
        
        @Subcommand("speed")
        @CommandAlias("s")
        @Description("Alter speed")
        @CommandPermission("nightclub.light")
        public static void onModifySpeed(CommandSender sender, String[] args) {
            if (isUnloaded(args, 1).stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(formatErrors(isUnloaded(args, 1)));
                return;
            }
            light.setBaseSpeed(Util.parseNumber(args[0]).doubleValue());
        }
        
        @Subcommand("secondaryspeed")
        @CommandAlias("ss")
        @Description("Alter secondary speed")
        @CommandPermission("nightclub.light")
        public static void onModifySecondarySpeed(CommandSender sender, String[] args) {
            if (isUnloaded(args, 1).stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(formatErrors(isUnloaded(args, 1)));
                return;
            }
            light.setSecondaryBaseSpeed(Util.parseNumber(args[0]).doubleValue());
        }
        
        @Subcommand("lightcount")
        @CommandAlias("lc")
        @Description("Alter the amount of lights")
        @CommandPermission("nightclub.light")
        public static void onModifyLightCount(CommandSender sender, String[] args) {
            if (isUnloaded(args, 1).stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(formatErrors(isUnloaded(args, 1)));
                return;
            }
            light.getData().setLightCount(Util.parseNumber(args[0]).intValue());
            light.buildLasers();
            light.on(new Color(0x0066ff));
        }
        
        @Subcommand("type")
        @CommandAlias("t")
        @Description("Alter the Light's type")
        @CommandCompletion("@type")
        @CommandPermission("nightclub.light")
        public static void onModifyType(CommandSender sender, String[] args) {
            List<CommandError> errors = isUnloaded(args, 1);
            try {
                LightType.valueOf(args[0]);
            } catch (IllegalArgumentException e) {
                errors.add(CommandError.INVALID_ARGUMENT);
            }
            if (errors.stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(formatErrors(errors));
                return;
            }
            light.setType(LightType.valueOf(args[0]));
            light.on(new Color(0x0066ff));
            light.buildLasers();
        }
        
        @Subcommand("rotation")
        @CommandAlias("r")
        @Description("Alter rotation")
        @CommandPermission("nightclub.light")
        public static void onModifyRotation(CommandSender sender, String[] args) {
            if (isUnloaded(args, 1).stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(formatErrors(isUnloaded(args, 1)));
                return;
            }
            light.getData().getPatternData().setRotation(Math.toRadians(Util.parseNumber(args[0]).doubleValue()));
            light.buildLasers();
            light.on(new Color(0x0066ff));
        }
        
        @Subcommand("secondaryrotation")
        @CommandAlias("sr")
        @Description("Alter secondary rotation")
        @CommandPermission("nightclub.light")
        public static void onModifySecondaryRotation(CommandSender sender, String[] args) {
            if (isUnloaded(args, 1).stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(formatErrors(isUnloaded(args, 1)));
                return;
            }
            light.getData().getSecondPatternData().setRotation(Math.toRadians(Util.parseNumber(args[0]).doubleValue()));
            light.buildLasers();
            light.on(new Color(0x0066ff));
        }
        
        @Subcommand("channel")
        @CommandAlias("c")
        @Description("Change a Light's channel")
        @CommandCompletion("@channels")
        @CommandPermission("nightclub.light")
        public static void onModifyChannel(CommandSender sender, String[] args) {
            List<CommandError> errors = isUnloaded(args, 1);
            try {
                LightChannel.valueOf(args[0]);
            } catch (IllegalArgumentException e) {
                errors.add(CommandError.INVALID_ARGUMENT);
            }
            if (errors.stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(formatErrors(errors));
                return;
            }
            light.setChannel(LightChannel.valueOf(args[0]));
        }
        @Subcommand("speedchannel")
        @CommandAlias("sc")
        @Description("Change a Light's speed channel")
        @CommandCompletion("@speedchannels")
        @CommandPermission("nightclub.light")
        public static void onModifySpeedChannel(CommandSender sender, String[] args) {
            List<CommandError> errors = isUnloaded(args, 1);
            try {
                LightSpeedChannel.valueOf(args[0]);
            } catch (IllegalArgumentException e) {
                errors.add(CommandError.INVALID_ARGUMENT);
            }
            if (errors.stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(formatErrors(errors));
                return;
            }
            light.setSpeedChannel(LightSpeedChannel.valueOf(args[0]));
        }
        
        @Subcommand("setlocation")
        @CommandAlias("sl")
        @Description("Set the lights location to your location, including pitch and yaw")
        @CommandPermission("nightclub.light")
        public static void onSetLocation(CommandSender sender, Player player, String[] args) {
            List<CommandError> errors = isUnloaded();
            errors.add(player == null ? CommandError.COMMAND_SENT_FROM_CONSOLE : CommandError.VALID);
            errors.add(args.length > 1 && args.length < 5 ? CommandError.TOO_LITTLE_ARGUMENTS : CommandError.VALID);
            if (errors.stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(formatErrors(errors));
                return;
            }
            if (args.length >= 5) {
                light.setLocation(new Location(Util.parseNumber(args[0]), Util.parseNumber(args[1]), Util.parseNumber(args[2]), // x y z
                        Util.parseNumber(args[3]), Util.parseNumber(args[4]))); // pitch and yaw
            } else {
                light.setLocation(Location.getFromBukkitLocation(player.getLocation().add(0, 1, 0)));
            }
            light.buildLasers();
            light.on(new Color(0x000000));
        }
        
        @Subcommand("flip")
        @CommandAlias("fl")
        @Description("Flip start and end locations of light")
        @CommandPermission("nightclub.light")
        public static void onFlip(CommandSender sender, String[] args) {
            if (isUnloaded().stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(formatErrors(isUnloaded()));
                return;
            }
            light.getData().setFlipStartAndEnd(!light.getData().isFlipStartAndEnd());
            light.buildLasers();
            light.on(new Color(0x000000));
        }
    }
    
    @Subcommand("control")
    @CommandAlias("c")
    @Description("Control a light, for example, turn it on or off")
    @CommandPermission("nightclub.light")
    public class LightControlCommand extends BaseCommand {
        
        @Subcommand("on")
        @Description("Turn light on")
        @CommandPermission("nightclub.light")
        public static void onTurnOn(CommandSender sender) {
            if (isUnloaded().stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(formatErrors(isUnloaded()));
                return;
            }
            light.on(new Color(0x0066ff));
        }
        @Subcommand("off")
        @Description("Turn light off")
        @CommandPermission("nightclub.light")
        public static void onTurnOff(CommandSender sender) {
            if (isUnloaded().stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(formatErrors(isUnloaded()));
                return;
            }
            light.off(new Color(0x000000));
        }
        @Subcommand("flash")
        @Description("Flash light")
        @CommandPermission("nightclub.light")
        public static void onFlash(CommandSender sender) {
            if (isUnloaded().stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(formatErrors(isUnloaded()));
                return;
            }
            light.flash(new Color(0x0066ff));
        }
        @Subcommand("flashoff")
        @Description("Flash off light")
        @CommandPermission("nightclub.light")
        public static void onFlashOff(CommandSender sender) {
            if (isUnloaded().stream().anyMatch(error -> error != CommandError.VALID)) {
                sender.sendMessage(formatErrors(isUnloaded()));
                return;
            }
            light.flashOff(new Color(0x0066ff));
        }
        
    }
}