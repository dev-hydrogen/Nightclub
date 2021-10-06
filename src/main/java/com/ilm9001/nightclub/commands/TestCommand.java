package com.ilm9001.nightclub.commands;

import com.ilm9001.nightclub.laser.Laser;
import com.ilm9001.nightclub.laser.LaserWrapper;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        double pitch = Math.toRadians(player.getLocation().getPitch()*2);
        double yaw = Math.toRadians(player.getLocation().getYaw());
        Vector3D x = new Vector3D(yaw,pitch).normalize().scalarMultiply(10);
        LaserWrapper laser2 = new LaserWrapper(player.getLocation(),player.getLocation().clone().add(x.getX(),x.getZ(),x.getZ()),120,64, Laser.LaserType.GUARDIAN);
        laser2.start();
        
        for (int i = 0; i < 360; i+=36) {
            Vector3D y = new Vector3D(Math.toRadians(i),0).normalize().scalarMultiply(5);
            Vector3D z = x.add(y);
            Location endpoint = player.getLocation().clone().add(z.getX(),z.getY(),z.getZ());
            LaserWrapper laser = new LaserWrapper(player.getLocation(),endpoint,120,64, Laser.LaserType.GUARDIAN);
            laser.start();
        }
        return false;
    }
}
