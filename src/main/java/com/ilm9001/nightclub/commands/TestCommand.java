package com.ilm9001.nightclub.commands;

import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.util.Laser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TestCommand implements CommandExecutor {
    Laser.CrystalLaser lsr;
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player) {
            if (args[0].equals("new")) {
                try {
                    lsr = new Laser.CrystalLaser(((Player) sender).getLocation(),((Player) sender).getLocation().add(0,5,0),-1,128);
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            } else if (args[0].equals("on")) {
                if(!lsr.isStarted()) lsr.start(Nightclub.getInstance());
            } else if (args[0].equals("off")) {
                if (lsr.isStarted()) lsr.stop();
            }
            return true;
        }
        return false;
    }
}
