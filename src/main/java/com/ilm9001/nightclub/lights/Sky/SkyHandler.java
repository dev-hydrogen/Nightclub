package com.ilm9001.nightclub.lights.Sky;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.util.concurrent.AtomicDouble;
import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.util.Util;
import net.minecraft.network.protocol.game.PacketPlayOutMapChunk;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_17_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;

public class SkyHandler {
    private static int r=0;
    private static int g=0;
    private static int b=0;
    private static BukkitTask task;
    private static boolean on = false;
    
    public static void setSkyForAllPlayers(int r, int g, int b) {setSkyForAllPlayers(new Color(r,g,b).getRGB());}
    public static synchronized void setSkyForAllPlayers(int RGB) {
        Color clr = new Color(RGB);
        r=clr.getRed();
        g=clr.getGreen();
        b=clr.getBlue();
        for(Player plr : Nightclub.getInstance().getServer().getOnlinePlayers()) {
            for(Chunk chunk : Util.getChunkAround(plr.getLocation().getChunk(),1)) {
                net.minecraft.world.level.chunk.Chunk c = ((CraftChunk)chunk).getHandle();
                ((CraftPlayer) plr).getHandle().b.sendPacket(new PacketPlayOutMapChunk(c));
            }
        }
    }
    
    public static synchronized void off() {
        //if(!task.isCancelled()) {task.cancel();}
        if(on) {
            setSkyForAllPlayers(0x000000);
            on = false;
        }
    }
    public static void on(int r, int g, int b) {on(new Color(r,g,b).getRGB());}
    public static synchronized void on(int rgb) {
        //if(!task.isCancelled()) {task.cancel();}
        if(!on) {
            setSkyForAllPlayers(rgb);
            on = true;
        }
    }
    public static void flash(int r, int g, int b) {flash(new Color(r,g,b).getRGB());}
    public static void flash(int rgb) {
        if(!on) {flashOff(rgb);}
        else {/*if(!task.isCancelled()) {task.cancel();}*/ on(rgb);}
    }
    public static void flashOff(int r, int g, int b) {
        flashOff(new Color(r,g,b).getRGB());
    }
    public static void flashOff(int rgb) {
        /*on = true;
        AtomicDouble i = new AtomicDouble();
        Color clrgb = new Color(rgb);
        float[] hsb = Color.RGBtoHSB(clrgb.getRed(),clrgb.getGreen(),clrgb.getBlue(),null);
        Bukkit.getScheduler().runTaskTimerAsynchronously(Nightclub.getInstance(), (BukkitTask self) -> {
            task = self;
            int rgbconverted = Color.HSBtoRGB(hsb[0],hsb[1], (float) (hsb[2]-i.get()));
            setSkyForAllPlayers(rgbconverted);
            i.getAndAdd(0.1);
            if(i.get() > 1.0) {self.cancel(); on = false;}
        },0,2);
         */
        on(rgb);
        Bukkit.getScheduler().runTaskLaterAsynchronously(Nightclub.getInstance(), SkyHandler::off,45);
    }
    
    public static int getR() {return r;}
    public static int getG() {return g;}
    public static int getB() {return b;}
}
