package com.ilm9001.nightclub;

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
    
    @Override
    public void onEnable() {
        direction = Directions.valueOf(getConfig().getString("FacingTowards"));
        instance = this;
        show = new Show();
        this.saveDefaultConfig();
    
        int pluginId = 12300;
        Metrics metrics = new Metrics(this, pluginId);
        
        ConfigParser.summonFromConfig();
        this.getCommand("playbp").setExecutor(new PlayCommand());
        this.getCommand("playbp").setTabCompleter(new PlayCommandTabComplete());
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
}
