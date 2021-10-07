package com.ilm9001.nightclub.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;

@CommandAlias("li")
public class LightCommand extends BaseCommand {
    
    @Default
    @Subcommand("build")
    @Description("Build a new Light!")
    public static void onBuild(Player player, String[] args) {
        
    }
}
