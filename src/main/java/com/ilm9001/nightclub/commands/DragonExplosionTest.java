package com.ilm9001.nightclub.commands;

/*import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.ilm9001.nightclub.Nightclub;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DragonExplosionTest implements CommandExecutor {
    private static int ENTITY_ID = 6000;
    private ScheduledExecutorService sch;
    
    public DragonExplosionTest() {
        sch = Executors.newScheduledThreadPool(12);
    }
    
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player plr = (Player) sender;
    
        for (int i = 0; i < Integer.parseInt(args[0]); i++) {
            PacketContainer dragon = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
            PacketContainer dragonDeath = new PacketContainer(PacketType.Play.Server.ENTITY_STATUS);
            PacketContainer dragonDestroy = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
    
            List<Integer> intList = new ArrayList<>();
            intList.add(ENTITY_ID+i);
    
            dragon.getIntegers()
                    .write(0, ENTITY_ID+i)
                    .write(1, 20);
            dragon.getDoubles()
                    .write(0, plr.getLocation().getX())
                    .write(1, plr.getLocation().getY())
                    .write(2, plr.getLocation().getZ());
            dragonDeath.getIntegers()
                    .write(0, ENTITY_ID+i);
            dragonDeath.getBytes()
                    .write(0, (byte) 3);
            dragonDestroy.getIntLists()
                    .write(0, intList);
    
            sch.schedule(new Dragon(plr,dragon),i, TimeUnit.MILLISECONDS);
            sch.schedule(new Dragon(plr,dragonDeath),2+i, TimeUnit.MILLISECONDS);
            sch.schedule(new Dragon(plr,dragonDestroy),10, TimeUnit.SECONDS);
        }
    
        return true;
    }
    
    class Dragon implements Runnable {
        Player plr;
        PacketContainer packet;
        
        Dragon(Player plr, PacketContainer packet) {
            this.plr = plr;
            this.packet = packet;
        }
        @Override
        public void run() {
            try {
                Nightclub.getProtocolManager().sendServerPacket(plr, packet);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
 */
