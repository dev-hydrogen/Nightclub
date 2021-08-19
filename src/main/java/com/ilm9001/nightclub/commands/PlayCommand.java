package com.ilm9001.nightclub.commands;

import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.parse.Play;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class PlayCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender.hasPermission("nightclub.playbp")) {
            Play.play(args[0], Nightclub.getShow());
        }
        return true;
    }
}
