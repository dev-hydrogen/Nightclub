package com.ilm9001.nightclub;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.ilm9001.nightclub.commands.DragonExplosionTest;
import com.ilm9001.nightclub.commands.PlayCommand;
import com.ilm9001.nightclub.commands.PlayCommandTabComplete;
import com.ilm9001.nightclub.lights.Directions;
import com.ilm9001.nightclub.parse.ConfigParser;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public final class Nightclub extends JavaPlugin {
    private static Nightclub instance;
    private static Show show;
    private static Directions direction;
    private static ProtocolManager protocolManager;
    
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        direction = Directions.valueOf(getConfig().getString("FacingTowards"));
        instance = this;
        show = new Show();
        protocolManager = ProtocolLibrary.getProtocolManager();
    
        int pluginId = 12300;
        Metrics metrics = new Metrics(this, pluginId);
        
        ConfigParser.summonFromConfig();
        this.getCommand("playbp").setExecutor(new PlayCommand());
        this.getCommand("playbp").setTabCompleter(new PlayCommandTabComplete());
        this.getCommand("test").setExecutor(new DragonExplosionTest());
    }
    
    @Override
    public void onDisable() {
    }
    
    public static Nightclub getInstance() {
        return instance;
    }
    
    public static Show getShow() {
        return show;
    }
 
    public static Directions getDirection() { return direction; }
    
    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }
}
