package com.ilm9001.nightclub.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.light.LightUniverse;
import com.ilm9001.nightclub.light.LightUniverseManager;
import org.bukkit.command.CommandSender;

import java.util.concurrent.atomic.AtomicReference;

@CommandAlias("lightuniverse|lu")
public class LightUniverseCommand extends BaseCommand {
    private static final LightUniverseManager manager = Nightclub.getLightUniverseManager();
    
    @Subcommand("build")
    @Description("Build a new LightUniverse")
    public static void onBuild(String[] args) {
        LightUniverse universe = new LightUniverse();
        manager.add(universe);
        if (manager.getLoadedUniverse() != null) {
            manager.getLoadedUniverse().unload();
        }
        manager.setLoadedUniverse(universe);
    }
    
    @Subcommand("load")
    @Description("Load a LightUniverse from provided argument")
    @CommandCompletion("@universes")
    public static void onLoad(String[] args) {
        LightUniverse lightUniverse = manager.getLoadedUniverse();
        if (args.length < 1) {
            return;
        }
        AtomicReference<LightUniverse> lightUniverseAtomic = new AtomicReference<>();
        Nightclub.getLightUniverseManager().getUniverses().forEach((universe -> {
            if (args[0].equals(universe.getName())) {
                lightUniverseAtomic.set(universe);
            }
        }));
        if (lightUniverse != null && lightUniverse.isLoaded()) {
            lightUniverse.unload();
        }
        lightUniverse = lightUniverseAtomic.get();
        lightUniverse.load();
        manager.setLoadedUniverse(lightUniverse);
    }
    
    @Subcommand("unload")
    @Description("Unload currently loaded LightUniverse")
    public static void onUnload() {
        LightUniverse lightUniverse = manager.getLoadedUniverse();
        if (lightUniverse != null && lightUniverse.isLoaded()) {
            lightUniverse.unload();
            manager.setLoadedUniverse(null);
        }
    }
    
    @Subcommand("setname")
    @Description("Set the currently loaded LightUniverses name")
    public static void onSetName(String[] args) {
        if (args.length < 1) {
            return;
        }
        if (manager.getLoadedUniverse() != null) {
            manager.getLoadedUniverse().setName(args[0]);
        }
    }
    
    @Subcommand("getid")
    @Description("Get ID of loaded LightUniverse")
    public static void onGetID(CommandSender sender) {
        if (manager.getLoadedUniverse() != null) {
            sender.sendMessage("Currently loaded ID: " + manager.getLoadedUniverse().getId());
        }
    }
    @Subcommand("getname")
    @Description("Get name of loaded LightUniverse")
    public static void onGetName(CommandSender sender) {
        if (manager.getLoadedUniverse() != null) {
            sender.sendMessage("Currently loaded ID: " + manager.getLoadedUniverse().getName());
        }
    }
}
