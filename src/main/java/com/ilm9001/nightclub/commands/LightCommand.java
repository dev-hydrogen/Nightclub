package com.ilm9001.nightclub.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.ilm9001.nightclub.light.Light;
import com.ilm9001.nightclub.light.LightType;
import com.ilm9001.nightclub.light.event.LightChannel;
import com.ilm9001.nightclub.light.pattern.LightPattern;
import com.ilm9001.nightclub.util.Location;
import com.ilm9001.nightclub.util.Util;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("light|li")
public class LightCommand extends BaseCommand {
    private static Light light;
    
    @Subcommand("build")
    @CommandAlias("b")
    @Description("Build a new Light!")
    public static void onBuild(Player player, String[] args) {
        light = new Light(UUID.randomUUID(), "Unnamed-Light", Location.getFromBukkitLocation(player.getLocation().add(0, 1, 0)),
                15, 70, 2, 3, 20, 5, true, LightPattern.CIRCLE,
                LightType.GUARDIAN_BEAM, LightChannel.CENTER_LIGHTS);
        light.start();
        light.on();
    }
    
    @Subcommand("data")
    @CommandAlias("d")
    @Description("Modify a Light's data")
    public class LightDataCommand extends BaseCommand {
        
        @Subcommand("pattern")
        @CommandAlias("p")
        @Description("Alter pattern")
        @CommandCompletion("@pattern")
        public static void onPattern(String[] args) {
            light.setPattern(LightPattern.valueOf(args[0]));
        }
        
        @Subcommand("maxlength")
        @CommandAlias("ml")
        @Description("Alter max length multiplier")
        public static void onMaxLength(String[] args) {
            light.setMaxLength(Util.parseNumber(args[0]).doubleValue());
            light.on();
        }
        
        @Subcommand("onlength")
        @CommandAlias("ol")
        @Description("Alter the on length percentage")
        public static void onModifyOnLength(String[] args) {
            light.setOnLength(Util.parseNumber(args[0]).doubleValue());
            light.on();
        }
        
        @Subcommand("patternmultiplier")
        @CommandAlias("pm")
        @Description("Alter the pattern size multiplier")
        public static void onModifyPatternMultiplier(String[] args) {
            light.setPatternSizeMultiplier(Util.parseNumber(args[0]).doubleValue());
        }
        
        @Subcommand("speed")
        @CommandAlias("s")
        @Description("Alter speed")
        public static void onModifySpeed(String[] args) {
            light.setSpeed(Util.parseNumber(args[0]).doubleValue());
        }
        
        @Subcommand("lightcount")
        @CommandAlias("lc")
        @Description("Alter the amount of lights")
        public static void onModifyLightCount(String[] args) {
            light.setLightCount(Util.parseNumber(args[0]).intValue());
            light.buildLasers();
            light.on();
        }
        
        @Subcommand("type")
        @CommandAlias("t")
        @Description("Alter the Light's type")
        @CommandCompletion("@type")
        public static void onModifyType(String[] args) {
            light.setType(LightType.valueOf(args[0]));
            light.buildLasers();
            light.on();
        }
        
        @Subcommand("rotation")
        @CommandAlias("r")
        @Description("Alter rotation")
        public static void onModifyRotation(String[] args) {
            light.getLocation().setRotation(Util.parseNumber(args[0]).doubleValue());
        }
        
        @Subcommand("channel")
        @CommandAlias("c")
        @Description("Change a Light's channel")
        @CommandCompletion("@channels")
        public static void onModifyChannel(String[] args) {
            light.setChannel(LightChannel.valueOf(args[0]));
        }
    }
    
    @Subcommand("control")
    @CommandAlias("c")
    @Description("Control a light, for example, turn it on or off")
    public class LightControlCommand extends BaseCommand {
        
        @Subcommand("on")
        @Description("Turn light on")
        public static void onTurnOn() {
            light.on();
        }
        @Subcommand("off")
        @Description("Turn light off")
        public static void onTurnOff() {
            light.off();
        }
        @Subcommand("flash")
        @Description("Flash light")
        public static void onFlash() {
            light.flash();
        }
        @Subcommand("flashoff")
        @Description("Flash off light")
        public static void onFlashOff() {
            light.flashOff();
        }
        
    }
}
