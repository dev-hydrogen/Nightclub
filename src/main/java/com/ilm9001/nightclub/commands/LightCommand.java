package com.ilm9001.nightclub.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.light.Light;
import com.ilm9001.nightclub.light.LightType;
import com.ilm9001.nightclub.light.LightUniverse;
import com.ilm9001.nightclub.light.LightUniverseManager;
import com.ilm9001.nightclub.light.event.LightChannel;
import com.ilm9001.nightclub.light.event.LightSpeedChannel;
import com.ilm9001.nightclub.light.pattern.LightPattern;
import com.ilm9001.nightclub.util.Location;
import com.ilm9001.nightclub.util.Util;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@CommandAlias("light|li")
@CommandPermission("nightclub.light")
public class LightCommand extends BaseCommand {
    private static Light light;
    
    public static boolean isUnloaded() {
        return light == null || Nightclub.getLightUniverseManager().getLoadedUniverse() == null || BeatmapCommand.getPlayer().isPlaying();
    }
    
    @Subcommand("build")
    @CommandAlias("b")
    @Description("Build a new Light!")
    @CommandPermission("nightclub.light")
    public static void onBuild(Player player, String[] args) {
        LightUniverseManager manager = Nightclub.getLightUniverseManager();
        if (manager.getLoadedUniverse() == null || BeatmapCommand.getPlayer().isPlaying()) {
            return;
        }
        light = new Light(UUID.randomUUID(), "Unnamed-Light" + new Random().nextInt(), Location.getFromBukkitLocation(player.getLocation().add(0, 1, 0)),
                15, 80, 0.3, 5, 45, 3, player.getLocation().getPitch() > -10, LightPattern.CIRCLE,
                LightType.GUARDIAN_BEAM, LightChannel.CENTER_LIGHTS, LightSpeedChannel.DEFAULT);
        light.start();
        light.on(new Color(0x0066ff));
        manager.getLoadedUniverse().addLight(light);
        manager.save();
    }
    
    @Subcommand("remove")
    @CommandAlias("r")
    @Description("Remove currently loaded Light")
    @CommandPermission("nightclub.light")
    public static void onRemove() {
        LightUniverseManager manager = Nightclub.getLightUniverseManager();
        if (isUnloaded()) return;
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
    public static void onLoad(String[] args) {
        LightUniverseManager manager = Nightclub.getLightUniverseManager();
        LightUniverse universe = manager.getLoadedUniverse();
        if (universe == null || args.length < 1 || BeatmapCommand.getPlayer().isPlaying()) {
            return;
        }
        Light nullableLight = universe.getLight(args[0]);
        if (nullableLight == null) {
            return;
        }
        light = nullableLight;
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
        public static void onNameChange(String[] args) {
            if (args.length < 1 || isUnloaded()) {
                return;
            } else {
                // check if lights name is already taken, if it is, return
                for (Light light : Nightclub.getLightUniverseManager().getLoadedUniverse().getLights()) {
                    if (Objects.equals(light.getName(), args[0])) {
                        return;
                    }
                }
            }
            light.setName(args[0]);
        }
        
        @Subcommand("pattern")
        @CommandAlias("p")
        @Description("Alter pattern")
        @CommandCompletion("@pattern")
        @CommandPermission("nightclub.light")
        public static void onPattern(String[] args) {
            if (isUnloaded()) return;
            light.setPattern(LightPattern.valueOf(args[0]));
            light.on(new Color(0x0066ff));
        }
        
        @Subcommand("maxlength")
        @CommandAlias("ml")
        @Description("Alter max length multiplier")
        @CommandPermission("nightclub.light")
        public static void onMaxLength(String[] args) {
            if (isUnloaded()) return;
            light.setMaxLength(Util.parseNumber(args[0]).doubleValue());
            light.on(new Color(0x0066ff));
        }
        
        @Subcommand("onlength")
        @CommandAlias("ol")
        @Description("Alter the on length percentage")
        @CommandPermission("nightclub.light")
        public static void onModifyOnLength(String[] args) {
            if (isUnloaded()) return;
            light.setOnLength(Util.parseNumber(args[0]).doubleValue());
            light.on(new Color(0x0066ff));
        }
        
        @Subcommand("patternmultiplier")
        @CommandAlias("pm")
        @Description("Alter the pattern size multiplier")
        @CommandPermission("nightclub.light")
        public static void onModifyPatternMultiplier(String[] args) {
            if (isUnloaded()) return;
            light.setPatternSizeMultiplier(Util.parseNumber(args[0]).doubleValue());
            light.on(new Color(0x0066ff));
        }
        
        @Subcommand("speed")
        @CommandAlias("s")
        @Description("Alter speed")
        @CommandPermission("nightclub.light")
        public static void onModifySpeed(String[] args) {
            if (isUnloaded()) return;
            light.setBaseSpeed(Util.parseNumber(args[0]).doubleValue());
        }
        
        @Subcommand("lightcount")
        @CommandAlias("lc")
        @Description("Alter the amount of lights")
        @CommandPermission("nightclub.light")
        public static void onModifyLightCount(String[] args) {
            if (isUnloaded()) return;
            light.setLightCount(Util.parseNumber(args[0]).intValue());
            light.on(new Color(0x0066ff));
        }
        
        @Subcommand("type")
        @CommandAlias("t")
        @Description("Alter the Light's type")
        @CommandCompletion("@type")
        @CommandPermission("nightclub.light")
        public static void onModifyType(String[] args) {
            if (isUnloaded()) return;
            light.setType(LightType.valueOf(args[0]));
            light.on(new Color(0x0066ff));
            light.buildLasers();
        }
        
        @Subcommand("rotation")
        @CommandAlias("r")
        @Description("Alter rotation")
        @CommandPermission("nightclub.light")
        public static void onModifyRotation(String[] args) {
            if (isUnloaded()) return;
            light.setRotation(Math.toRadians(Util.parseNumber(args[0]).doubleValue()));
            light.on(new Color(0x0066ff));
        }
        
        @Subcommand("channel")
        @CommandAlias("c")
        @Description("Change a Light's channel")
        @CommandCompletion("@channels")
        @CommandPermission("nightclub.light")
        public static void onModifyChannel(String[] args) {
            if (isUnloaded()) return;
            light.setChannel(LightChannel.valueOf(args[0]));
        }
        @Subcommand("speedchannel")
        @CommandAlias("sc")
        @Description("Change a Light's speed channel")
        @CommandCompletion("@speedchannels")
        @CommandPermission("nightclub.light")
        public static void onModifySpeedChannel(String[] args) {
            if (isUnloaded()) return;
            light.setSpeedChannel(LightSpeedChannel.valueOf(args[0]));
        }
        
        @Subcommand("setlocation")
        @CommandAlias("sl")
        @Description("Set the lights location to your location, including pitch and yaw")
        @CommandPermission("nightclub.light")
        public static void onSetLocation(Player player, String[] args) {
            if (isUnloaded() || player == null) return;
            light.setLocation(Location.getFromBukkitLocation(player.getLocation().add(0, 1, 0)));
            light.buildLasers();
            light.on(new Color(0x000000));
        }
        
        @Subcommand("flip")
        @CommandAlias("fl")
        @Description("Flip start and end locations of light")
        @CommandPermission("nightclub.light")
        public static void onSetLocation(String[] args) {
            if (isUnloaded()) return;
            light.setFlipStartAndEnd(!light.isFlipStartAndEnd());
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
        public static void onTurnOn() {
            if (isUnloaded()) return;
            light.on(new Color(0x0066ff));
        }
        @Subcommand("off")
        @Description("Turn light off")
        @CommandPermission("nightclub.light")
        public static void onTurnOff() {
            if (isUnloaded()) return;
            light.off(new Color(0x000000));
        }
        @Subcommand("flash")
        @Description("Flash light")
        @CommandPermission("nightclub.light")
        public static void onFlash() {
            if (isUnloaded()) return;
            light.flash(new Color(0x0066ff));
        }
        @Subcommand("flashoff")
        @Description("Flash off light")
        @CommandPermission("nightclub.light")
        public static void onFlashOff() {
            if (isUnloaded()) return;
            light.flashOff(new Color(0x0066ff));
        }
        
    }
}