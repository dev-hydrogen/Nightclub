package exposed.hydrogen.nightclub;

import co.aikar.commands.MinestomCommandManager;
import dev.hypera.chameleon.core.exceptions.instantiation.ChameleonInstantiationException;
import dev.hypera.chameleon.platforms.minestom.MinestomChameleon;
import lombok.Getter;
import net.minestom.server.extensions.Extension;

public class NightclubMinestom extends Extension {
    @Getter private static NightclubMinestom instance;
    @Getter private static MinestomUtil util;
    @Getter private static MinestomCommandManager commandManager;
    private MinestomChameleon chameleon;

    @Override

    public void initialize() {
        instance = this;
        util = new MinestomUtil();
        commandManager = new MinestomCommandManager();
        Nightclub.registerCommands(commandManager);
        Nightclub.registerCompletions(commandManager.getCommandCompletions());
        Nightclub.setCrossCompatUtil(util);

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
