package com.ilm9001.nightclub.commands;

import com.ilm9001.nightclub.lights.Sky.SkyHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SkyTest implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(strings.length > 2) {
            commandSender.sendMessage(strings);
            SkyHandler.setSkyForAllPlayers(Integer.parseInt(strings[0]),Integer.parseInt(strings[1]),Integer.parseInt(strings[2]));
        }
        return true;
    }
}
