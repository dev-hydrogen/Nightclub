package com.ilm9001.nightclub;

import com.ilm9001.nightclub.commands.PlayCommand;
import com.ilm9001.nightclub.commands.PlayCommandTabComplete;
import com.ilm9001.nightclub.lights.Directions;
import com.ilm9001.nightclub.parse.ConfigParser;
import org.bukkit.plugin.java.JavaPlugin;

public final class Nightclub extends JavaPlugin {
    private static Nightclub instance;
    private static Show show;
    private static Directions direction;
    
    @Override
    public void onEnable() {
        instance = this;
        show = new Show();
    
        ConfigParser.summonFromConfig();
        this.saveDefaultConfig();
        this.getCommand("playbp").setExecutor(new PlayCommand());
        this.getCommand("playbp").setTabCompleter(new PlayCommandTabComplete());
        
        direction = Directions.valueOf(getConfig().getString("FacingTowards"));
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
