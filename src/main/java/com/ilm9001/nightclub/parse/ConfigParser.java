package com.ilm9001.nightclub.parse;

import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.lights.DownTop.DownTopCircle;
import com.ilm9001.nightclub.lights.TopDown.TopDownCircle;
import com.ilm9001.nightclub.lights.TopDown.TopDownDoubleCircle;
import com.ilm9001.nightclub.lights.TopDown.TopDownLineCircle;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigParser {
    private static final List<TopDownCircle> TDCList = new ArrayList<>();
    private static final List<TopDownDoubleCircle> TDDCList = new ArrayList<>();
    private static final List<TopDownLineCircle> TDLCList = new ArrayList<>();
    private static final List<DownTopCircle> DTCList = new ArrayList<>();
    
    public static void summonFromConfig() {
        FileConfiguration config = Nightclub.getInstance().getConfig();
        List<?> tdcList = config.getList("TopDownCircle");
        List<?> tdlcList = config.getList("TopDownLineCircle");
        List<?> tddcList = config.getList("TopDownDoubleCircle");
        List<?> dtcList = config.getList("DownTopCircle");
        
        if(tdcList == null || tddcList == null || tdlcList == null || dtcList == null) return;
        
        for(Object lst : tdcList) {
            List<Double> list = (List<Double>) lst;
            World mainWorld = Nightclub.getInstance().getServer().getWorlds().get(0);
            TDCList.add(new TopDownCircle(
                    new Location(mainWorld,list.get(0),list.get(1),list.get(2)),
                    list.get(3).intValue()));
        }
        for(Object lst : tddcList) {
            List<Double> list = (List<Double>) lst;
            World mainWorld = Nightclub.getInstance().getServer().getWorlds().get(0);
            TDDCList.add(new TopDownDoubleCircle(
                    new Location(mainWorld,list.get(0),list.get(1), list.get(2)),
                    list.get(3).intValue()));
        }
        for(Object lst : tdlcList) {
            List<Double> list = (List<Double>) lst;
            World mainWorld = Nightclub.getInstance().getServer().getWorlds().get(0);
            TDLCList.add(new TopDownLineCircle(
                    new Location(mainWorld,list.get(0),list.get(1),list.get(2)),
                    list.get(3).intValue()));
        }
        for(Object lst : dtcList) {
            List<Double> list = (List<Double>) lst;
            World mainWorld = Nightclub.getInstance().getServer().getWorlds().get(0);
            DTCList.add(new DownTopCircle(
                    new Location(mainWorld,list.get(0),list.get(1),list.get(2)),
                    list.get(3).intValue()));
        }
    }
    
    public static List<TopDownCircle> getTopDownCircleList() {
        return TDCList;
    }
    
    public static List<TopDownDoubleCircle> getTopDownDoubleCircleList() {
        return TDDCList;
    }
    
    public static List<TopDownLineCircle> getTopDownLineCircleList() {
        return TDLCList;
    }
    
    public static List<DownTopCircle> getDownTopCircleList() {
        return DTCList;
    }
}
