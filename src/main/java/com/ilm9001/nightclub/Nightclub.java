package com.ilm9001.nightclub;

import com.ilm9001.nightclub.commands.PlayCommand;
import com.ilm9001.nightclub.commands.PlayCommandTabComplete;
import com.ilm9001.nightclub.commands.Test;
import org.bukkit.plugin.java.JavaPlugin;

public final class Nightclub extends JavaPlugin {
    private static Nightclub instance;
    private static Show show;
    
    @Override
    public void onEnable() {
        instance = this;
        show = new Show();
        this.saveDefaultConfig();
        this.getCommand("playbp").setExecutor(new PlayCommand());
        this.getCommand("playbp").setTabCompleter(new PlayCommandTabComplete());
        this.getCommand("test").setExecutor(new Test());
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
}
