package com.ilm9001.nightclub.lights.Sky;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.ilm9001.nightclub.Nightclub;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class PacketListener {
    
    public PacketListener(Nightclub instance, ProtocolManager manager) {
        manager.addPacketListener(new PacketAdapter(instance, PacketType.Play.Server.MAP_CHUNK) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                
                int[] biomes = packet.getIntegerArrays().read(0);
                int biomeToUse = Nightclub.getSkies().getFromRGB(SkyHandler.getR(),SkyHandler.getG(),SkyHandler.getB()).getId();
                Arrays.fill(biomes,biomeToUse);
                packet.getIntegerArrays().write(0,biomes);
            }
        });
    }
}
