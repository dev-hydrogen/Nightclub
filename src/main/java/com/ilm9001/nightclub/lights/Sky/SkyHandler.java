package com.ilm9001.nightclub.lights.Sky;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.util.Util;
import net.minecraft.network.protocol.game.PacketPlayOutMapChunk;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_17_R1.CraftChunk;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class SkyHandler {
    
    
    public SkyHandler() {
    
    }
    
    public void setSkyForAllPlayers(int r, int g, int b) {
        for(Player plr : Nightclub.getInstance().getServer().getOnlinePlayers()) {
            for(Chunk chunk : Util.getChunkAround(plr.getLocation().getChunk(),10)) {
                net.minecraft.world.level.chunk.Chunk c = ((CraftChunk)chunk).getHandle();
                PacketContainer mapChunk = new PacketContainer(PacketType.Play.Server.MAP_CHUNK,new PacketPlayOutMapChunk(c));
                try {
                    Nightclub.getProtocolManager().sendServerPacket(plr,mapChunk);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
