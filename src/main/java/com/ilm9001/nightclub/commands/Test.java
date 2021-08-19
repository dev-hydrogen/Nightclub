package com.ilm9001.nightclub.commands;

import com.ilm9001.nightclub.lights.Lights;
import com.ilm9001.nightclub.lights.TopDown.TopDownCircle;
import com.ilm9001.nightclub.util.LaserWrapper;
import com.ilm9001.nightclub.util.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Test implements CommandExecutor {
    TopDownCircle tdc;
    
    public Test() {
        tdc = (TopDownCircle) Lights.TOPDOWN_CIRCLE.getLight();
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        switch(args[0]) {
            case "on":
                tdc.on();
                break;
            case "off":
                tdc.off();
                break;
            case "flash":
                tdc.flash();
                break;
            case "flashoff":
                tdc.flashOff();
                break;
        }
        return true;
    }
}
