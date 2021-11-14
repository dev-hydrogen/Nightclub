package com.ilm9001.nightclub.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.light.LightUniverse;
import com.ilm9001.nightclub.light.LightUniverseManager;
import org.bukkit.command.CommandSender;

import java.util.concurrent.atomic.AtomicReference;

@CommandAlias("lightuniverse|lu")
@CommandPermission("nightclub.lightuniverse")
public class LightUniverseCommand extends BaseCommand {
    private static final LightUniverseManager manager = Nightclub.getLightUniverseManager();
    
    public static boolean isUnloaded() {
        return manager.getLoadedUniverse() == null;
    }
    
    @Subcommand("build")
    @Description("Build a new LightUniverse")
    @CommandPermission("nightclub.lightuniverse")
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
    @CommandPermission("nightclub.lightuniverse")
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
    @CommandPermission("nightclub.lightuniverse")
    public static void onUnload() {
        LightUniverse lightUniverse = manager.getLoadedUniverse();
        if (lightUniverse != null && lightUniverse.isLoaded()) {
            lightUniverse.unload();
            manager.setLoadedUniverse(null);
        }
    }
    
    @Subcommand("setname")
    @Description("Set the currently loaded LightUniverses name")
    @CommandPermission("nightclub.lightuniverse")
    public static void onSetName(String[] args) {
        if (args.length < 1 || isUnloaded()) return;
        manager.getLoadedUniverse().setName(args[0]);
    }
    
    @Subcommand("getid")
    @Description("Get ID of loaded LightUniverse")
    @CommandPermission("nightclub.lightuniverse")
    public static void onGetID(CommandSender sender) {
        if (isUnloaded()) return;
        sender.sendMessage("Currently loaded ID: " + manager.getLoadedUniverse().getId());
    }
    @Subcommand("getname")
    @Description("Get name of loaded LightUniverse")
    @CommandPermission("nightclub.lightuniverse")
    public static void onGetName(CommandSender sender) {
        if (isUnloaded()) return;
        sender.sendMessage("Currently loaded ID: " + manager.getLoadedUniverse().getName());
    }
}
