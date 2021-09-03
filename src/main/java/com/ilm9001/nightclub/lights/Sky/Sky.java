package com.ilm9001.nightclub.lights.Sky;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.data.worldgen.BiomeDecoratorGroups;
import net.minecraft.data.worldgen.WorldGenSurfaceComposites;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeFog;
import net.minecraft.world.level.biome.BiomeSettingsGeneration;
import net.minecraft.world.level.biome.BiomeSettingsMobs;
import net.minecraft.world.level.levelgen.WorldGenStage;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;

import java.awt.*;

public class Sky {
    private int color;
    private String colorName;
    private int id;
    
    public Sky(int r, int g, int b) {
        this(new Color(r,g,b).getRGB());
    }
    public Sky(int color) {
        this.color = color;
        colorName = ""+color;
        DedicatedServer ds = ((CraftServer) Bukkit.getServer()).getHandle().getServer();
        BiomeBase bBase = getBiomeBase();
    
        ResourceKey<BiomeBase> bbKey = ResourceKey.a(IRegistry.aO, new MinecraftKey(colorName));
        IRegistryWritable<BiomeBase> writable = ds.getCustomRegistry().b(IRegistry.aO);
        writable.a(bbKey,bBase, Lifecycle.stable()); // Register our biome to the custom biome Registry. This can only be set once before players join.
        id = ds.getCustomRegistry().d(IRegistry.aO).getId(bBase); // Store the biome ID for later, used in PacketListener.
    }
    
    public BiomeBase getBiomeBase() {
        BiomeSettingsGeneration.a gen = (new BiomeSettingsGeneration.a()).a(WorldGenSurfaceComposites.p);
        gen.a(WorldGenStage.Decoration.j, BiomeDecoratorGroups.W);
        
        BiomeFog.a foggers = new BiomeFog.a()
                .a(color) // fog
                .b(0xffffff) // water
                .c(0xffffff) // water fog
                .d(color); // sky
    
        return new BiomeBase.a()
                .a(BiomeBase.Precipitation.a) //none
                .a(BiomeBase.Geography.a) //none
                .a(0F) //ocean depth
                .b(0F) //scale - lower = flatter
                .c(0F) //temperature
                .d(0F) //maybe important, foliage and grass color
                .a(foggers.a()) //biomefog
                .a(BiomeSettingsMobs.c) //same as void biome
                .a(gen.a()) //same as void biome
                .a();
    }
    
    public int getColor() {
        return color;
    }
    
    public String getColorName() {
        return colorName;
    }
    
    public int getId() {
        return id;
    }
}
