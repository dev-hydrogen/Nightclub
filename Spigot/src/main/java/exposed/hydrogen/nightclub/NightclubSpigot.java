package exposed.hydrogen.nightclub;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import dev.hypera.chameleon.core.exceptions.instantiation.ChameleonInstantiationException;
import dev.hypera.chameleon.platforms.spigot.SpigotChameleon;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class NightclubSpigot extends JavaPlugin {
    private SpigotChameleon chameleon;
    @Getter private static ProtocolManager protocolManager;

    @Override
    public void onEnable() {
        try {
            chameleon = new SpigotChameleon(Nightclub.class, this);
            chameleon.onEnable();
        } catch (ChameleonInstantiationException ex) {
            ex.printStackTrace();
        }
        protocolManager = ProtocolLibrary.getProtocolManager();

    }

    @Override
    public void onDisable() {
        chameleon.onDisable();
    }
}
