package com.ilm9001.nightclub.lights.Sky;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.util.Util;
import net.minecraft.network.protocol.game.PacketPlayOutMapChunk;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_17_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class SkyHandler {
    private static int r=0;
    private static int g=0;
    private static int b=0;
    
    public static void setSkyForAllPlayers(int r, int g, int b) {setSkyForAllPlayers(new Color(r,g,b).getRGB());}
    public static void setSkyForAllPlayers(int RGB) {
        Color clr = new Color(RGB);
        r=clr.getRed();
        g=clr.getGreen();
        b=clr.getBlue();
        for(Player plr : Nightclub.getInstance().getServer().getOnlinePlayers()) {
            for(Chunk chunk : Util.getChunkAround(plr.getLocation().getChunk(),10)) {
                net.minecraft.world.level.chunk.Chunk c = ((CraftChunk)chunk).getHandle();
                ((CraftPlayer) plr).getHandle().b.sendPacket(new PacketPlayOutMapChunk(c));
            }
        }
    }
    
    public static int getR() {return r;}
    public static int getG() {return g;}
    public static int getB() {return b;}
}
