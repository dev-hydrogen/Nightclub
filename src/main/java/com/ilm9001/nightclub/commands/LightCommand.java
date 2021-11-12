package com.ilm9001.nightclub.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
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

import java.util.Objects;
import java.util.UUID;

@CommandAlias("light|li")
public class LightCommand extends BaseCommand {
    private static Light light;
    
    @Subcommand("build")
    @CommandAlias("b")
    @Description("Build a new Light!")
    public static void onBuild(Player player, String[] args) {
        light = new Light(UUID.randomUUID(), "Unnamed-Light", Location.getFromBukkitLocation(player.getLocation().add(0, 1, 0)),
                15, 80, 0.3, 5, 45, 3, player.getLocation().getPitch() > -10, LightPattern.CIRCLE,
                LightType.GUARDIAN_BEAM, LightChannel.CENTER_LIGHTS, LightSpeedChannel.DEFAULT);
        light.start();
        light.on();
        LightUniverseManager manager = Nightclub.getLightUniverseManager();
        if (manager.getLoadedUniverse() != null) {
            manager.getLoadedUniverse().addLight(light);
            manager.save();
        }
    }
    
    @Subcommand("remove")
    @CommandAlias("r")
    @Description("Remove currently loaded Light")
    public static void onRemove() {
        LightUniverseManager manager = Nightclub.getLightUniverseManager();
        if (manager.getLoadedUniverse() == null && light == null) {
            return;
        }
        light.off();
        light.stop();
        light.getChannel().getHandler().removeListener(light);
        manager.getLoadedUniverse().removeLight(light);
        manager.save();
        light = null;
    }
    
    @Subcommand("load")
    @CommandAlias("l")
    @Description("Load a Light from currently loaded LightUniverse")
    @CommandCompletion("@lights")
    public static void onLoad(String[] args) {
        LightUniverseManager manager = Nightclub.getLightUniverseManager();
        LightUniverse universe = manager.getLoadedUniverse();
        if (universe == null || args.length < 1) {
            return;
        }
        Light nullableLight = universe.getLight(args[0]);
        if (nullableLight == null) {
            return;
        }
        light = nullableLight;
        light.start();
        light.on();
    }
    
    @Subcommand("data")
    @CommandAlias("d")
    @Description("Modify a Light's data")
    public class LightDataCommand extends BaseCommand {
        
        @Subcommand("name")
        @CommandAlias("n")
        @Description("Alter Light Name")
        public static void onNameChange(String[] args) {
            if (args.length < 1) {
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
        public static void onPattern(String[] args) {
            if (light == null) return;
            light.setPattern(LightPattern.valueOf(args[0]));
            light.on();
        }
        
        @Subcommand("maxlength")
        @CommandAlias("ml")
        @Description("Alter max length multiplier")
        public static void onMaxLength(String[] args) {
            if (light == null) return;
            light.setMaxLength(Util.parseNumber(args[0]).doubleValue());
            light.on();
        }
        
        @Subcommand("onlength")
        @CommandAlias("ol")
        @Description("Alter the on length percentage")
        public static void onModifyOnLength(String[] args) {
            if (light == null) return;
            light.setOnLength(Util.parseNumber(args[0]).doubleValue());
            light.on();
        }
        
        @Subcommand("patternmultiplier")
        @CommandAlias("pm")
        @Description("Alter the pattern size multiplier")
        public static void onModifyPatternMultiplier(String[] args) {
            if (light == null) return;
            light.setPatternSizeMultiplier(Util.parseNumber(args[0]).doubleValue());
            light.on();
        }
        
        @Subcommand("speed")
        @CommandAlias("s")
        @Description("Alter speed")
        public static void onModifySpeed(String[] args) {
            if (light == null) return;
            light.setSpeed(Util.parseNumber(args[0]).doubleValue());
        }
        
        @Subcommand("lightcount")
        @CommandAlias("lc")
        @Description("Alter the amount of lights")
        public static void onModifyLightCount(String[] args) {
            if (light == null) return;
            light.setLightCount(Util.parseNumber(args[0]).intValue());
            light.on();
        }
        
        @Subcommand("type")
        @CommandAlias("t")
        @Description("Alter the Light's type")
        @CommandCompletion("@type")
        public static void onModifyType(String[] args) {
            if (light == null) return;
            light.setType(LightType.valueOf(args[0]));
            light.on();
        }
        
        @Subcommand("rotation")
        @CommandAlias("r")
        @Description("Alter rotation")
        public static void onModifyRotation(String[] args) {
            if (light == null) return;
            light.setRotation(Math.toRadians(Util.parseNumber(args[0]).doubleValue()));
            light.on();
        }
        
        @Subcommand("channel")
        @CommandAlias("c")
        @Description("Change a Light's channel")
        @CommandCompletion("@channels")
        public static void onModifyChannel(String[] args) {
            if (light == null) return;
            light.setChannel(LightChannel.valueOf(args[0]));
        }
        @Subcommand("speedchannel")
        @CommandAlias("sc")
        @Description("Change a Light's speed channel")
        @CommandCompletion("@speedchannels")
        public static void onModifySpeedChannel(String[] args) {
            if (light == null) return;
            light.setSpeedChannel(LightSpeedChannel.valueOf(args[0]));
        }
        
        @Subcommand("setlocation")
        @CommandAlias("sl")
        @Description("Set the lights location to your location, including pitch and yaw")
        public static void onSetLocation(Player player, String[] args) {
            if (light == null || player == null) return;
            light.setLocation(Location.getFromBukkitLocation(player.getLocation().add(0, 1, 0)));
        }
    }
    
    @Subcommand("control")
    @CommandAlias("c")
    @Description("Control a light, for example, turn it on or off")
    public class LightControlCommand extends BaseCommand {
        
        @Subcommand("on")
        @Description("Turn light on")
        public static void onTurnOn() {
            if (light == null) return;
            light.on();
        }
        @Subcommand("off")
        @Description("Turn light off")
        public static void onTurnOff() {
            if (light == null) return;
            light.off();
        }
        @Subcommand("flash")
        @Description("Flash light")
        public static void onFlash() {
            if (light == null) return;
            light.flash();
        }
        @Subcommand("flashoff")
        @Description("Flash off light")
        public static void onFlashOff() {
            if (light == null) return;
            light.flashOff();
        }
        
    }
}