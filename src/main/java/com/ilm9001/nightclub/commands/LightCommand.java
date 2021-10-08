package com.ilm9001.nightclub.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ilm9001.nightclub.light.Light;
import com.ilm9001.nightclub.light.LightChannel;
import com.ilm9001.nightclub.light.LightType;
import com.ilm9001.nightclub.light.pattern.LightPattern;
import com.ilm9001.nightclub.util.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("li")
public class LightCommand extends BaseCommand {
    private static Light light;
    
    @Default
    @Subcommand("build")
    @CommandAlias("b")
    @Description("Build a new Light!")
    public static void onBuild(Player player, String[] args) {
        light = new Light(UUID.randomUUID(), "Unnamed-Light", Location.getFromBukkitLocation(player.getLocation()),
                2, 70, 2, 5, 20, 10, true, LightPattern.CIRCLE,
                LightType.GUARDIAN_BEAM, LightChannel.CENTER_LIGHTS);
        light.start();
        light.on();
        player.sendMessage("wtf? p: " + light.getLocation().getPitch() + " y: " + light.getLocation().getYaw());
    }
    
    @Subcommand("pattern")
    @CommandAlias("p")
    @Description("Alter pattern!")
    @CommandCompletion("@pattern")
    public static void onModify(Player player, String[] args) {
        light.setPattern(LightPattern.valueOf(args[0]));
    }
}
