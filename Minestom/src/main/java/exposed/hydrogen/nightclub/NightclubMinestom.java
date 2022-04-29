package exposed.hydrogen.nightclub;

import dev.hypera.chameleon.core.exceptions.instantiation.ChameleonInstantiationException;
import dev.hypera.chameleon.platforms.minestom.MinestomChameleon;
import lombok.Getter;
import net.minestom.server.extensions.Extension;

public class NightclubMinestom extends Extension {
    @Getter private static NightclubMinestom instance;
    @Getter private static MinestomUtil util;
    private MinestomChameleon chameleon;

    @Override

    public void initialize() {
        instance = this;
        util = new MinestomUtil();
        try {
            chameleon = new MinestomChameleon(Nightclub.class, this, Nightclub.getPluginData());
            chameleon.onEnable();
        } catch (ChameleonInstantiationException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void terminate() {
        chameleon.onDisable();
    }
}
