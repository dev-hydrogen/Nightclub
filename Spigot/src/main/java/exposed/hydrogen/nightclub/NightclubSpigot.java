package exposed.hydrogen.nightclub;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import dev.hypera.chameleon.core.exceptions.instantiation.ChameleonInstantiationException;
import dev.hypera.chameleon.platforms.spigot.SpigotChameleon;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public class NightclubSpigot extends JavaPlugin {
    @Getter private static NightclubSpigot instance;
    @Getter private static ProtocolManager protocolManager;
    @Getter private static SpigotUtil util;
    private SpigotChameleon chameleon;

    @Override
    public void onEnable() {
        instance = this;
        util = new SpigotUtil();
        try {
            chameleon = new SpigotChameleon(Nightclub.class, this, Nightclub.getPluginData());
            chameleon.onEnable();
        } catch (ChameleonInstantiationException ex) {
            ex.printStackTrace();
        }
        protocolManager = ProtocolLibrary.getProtocolManager();

        //register bstats
        Metrics metrics = new Metrics(this, 12300);
    }

    @Override
    public void onDisable() {
        chameleon.onDisable();
    }
}
