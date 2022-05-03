package exposed.hydrogen.nightclub;

import co.aikar.commands.BukkitCommandManager;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import dev.hypera.chameleon.core.exceptions.instantiation.ChameleonInstantiationException;
import dev.hypera.chameleon.platforms.spigot.SpigotChameleon;
import exposed.hydrogen.nightclub.laser.LaserTranslator;
import exposed.hydrogen.nightclub.util.DebugMarker;
import exposed.hydrogen.nightclub.wrapper.DebugMarkerFactory;
import exposed.hydrogen.nightclub.wrapper.LaserFactory;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public class NightclubSpigot extends JavaPlugin {
    @Getter private static NightclubSpigot instance;
    @Getter private static ProtocolManager protocolManager;
    @Getter private static SpigotUtil util;
    @Getter private static BukkitCommandManager commandManager;
    private SpigotChameleon chameleon;

    @Override
    public void onEnable() {
        instance = this;
        util = new SpigotUtil();
        Nightclub.setCrossCompatUtil(util);
        Nightclub.setLaserFactory(new LaserFactory<>(LaserTranslator.class));
        Nightclub.setMarkerFactory(new DebugMarkerFactory<>(DebugMarker.class));

        protocolManager = ProtocolLibrary.getProtocolManager();

        //register bstats
        Metrics metrics = new Metrics(this, 12300);
        try {
            chameleon = new SpigotChameleon(Nightclub.class, this, Nightclub.getPluginData());
            chameleon.onEnable();
        } catch (ChameleonInstantiationException ex) {
            ex.printStackTrace();
        }
        // needs to be after chameleon
        commandManager = new BukkitCommandManager(this);
        Nightclub.registerCommands(commandManager);
        Nightclub.registerCompletions(commandManager.getCommandCompletions());
    }

    @Override
    public void onDisable() {
        Nightclub.getLightUniverseManager().save();
        chameleon.onDisable();
    }
}
