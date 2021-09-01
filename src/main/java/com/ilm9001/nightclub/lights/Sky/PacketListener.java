package com.ilm9001.nightclub.lights.Sky;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.ilm9001.nightclub.Nightclub;
import org.bukkit.entity.Player;

public class PacketListener {
    
    public PacketListener(Nightclub instance, ProtocolManager manager) {
        manager.addPacketListener(new PacketAdapter(instance, PacketType.Play.Server.MAP_CHUNK) {
            @Override
            public void onPacketSending(PacketEvent event) {
                PacketContainer packet = event.getPacket();
                Player player = event.getPlayer();
                
                int[] biomes = packet.getIntegerArrays().read(0);
                // int biomeToUse =
            }
        });
    }
}
