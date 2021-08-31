package com.ilm9001.nightclub.parse;

import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.lights.DownTop.DownTopCircle;
import com.ilm9001.nightclub.lights.Ring.Rings;
import com.ilm9001.nightclub.lights.Side.FrontFacerCircle;
import com.ilm9001.nightclub.lights.TopDown.TopDownCircle;
import com.ilm9001.nightclub.lights.TopDown.TopDownDoubleCircle;
import com.ilm9001.nightclub.lights.TopDown.TopDownLineCircle;
import com.ilm9001.nightclub.util.Util;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Probably should be implemented some other way. I dont care though, it Works.
 * What do you mean i have absolutely no idea of proper OOP?
 *
 */
public class ConfigParser {
    private static final List<TopDownCircle> TDCList = new ArrayList<>();
    private static final List<TopDownDoubleCircle> TDDCList = new ArrayList<>();
    private static final List<TopDownLineCircle> TDLCList = new ArrayList<>();
    private static final List<DownTopCircle> DTCList = new ArrayList<>();
    private static final List<FrontFacerCircle> FFList = new ArrayList<>();
    //private static Cube cube = null;
    /*private static final List<CeilingCrystals> CCList = new ArrayList<>();*/
    private static Rings rings = null;
    
    public static void summonFromConfig() {
        FileConfiguration config = Nightclub.getInstance().getConfig();
        List<?> tdcList = config.getList("TopDownCircle");
        List<?> tdlcList = config.getList("TopDownLineCircle");
        List<?> tddcList = config.getList("TopDownDoubleCircle");
        List<?> dtcList = config.getList("DownTopCircle");
        List<?> ffList = config.getList("FrontFacerCircle");
        World mainWorld = Util.getMainWorld();
        /*List<?> ccList = config.getList("CeilingCrystals");*/
        
        if(tdcList == null || tddcList == null || tdlcList == null || dtcList == null || ffList == null) return;
        int i = 0;
        for(Object lst : tdcList) {
            i++;
            List<Double> list = (List<Double>) lst;
            TDCList.add(new TopDownCircle(
                    new Location(mainWorld,list.get(0),list.get(1),list.get(2)),
                    list.get(3).intValue(),i%2==0));
        }
        for(Object lst : tddcList) {
            i++;
            List<Double> list = (List<Double>) lst;
            TDDCList.add(new TopDownDoubleCircle(
                    new Location(mainWorld,list.get(0),list.get(1), list.get(2)),
                    list.get(3).intValue(),i%2==0));
        }
        for(Object lst : tdlcList) {
            i++;
            List<Double> list = (List<Double>) lst;
            TDLCList.add(new TopDownLineCircle(
                    new Location(mainWorld,list.get(0),list.get(1),list.get(2)),
                    list.get(3).intValue(),i%2==0));
        }
        for(Object lst : dtcList) {
            i++;
            List<Double> list = (List<Double>) lst;
            DTCList.add(new DownTopCircle(
                    new Location(mainWorld,list.get(0),list.get(1),list.get(2)),
                    list.get(3).intValue(),i%2==0));
        }
        for(Object lst : ffList) {
            i++;
            List<Double> list = (List<Double>) lst;
            FFList.add(new FrontFacerCircle(
                    new Location(mainWorld,list.get(0),list.get(1),list.get(2)),
                    list.get(3).intValue(),i%2==0));
        }
        int ringCount = Nightclub.getInstance().getConfig().getInt("RingCount");
        double ringSize = Nightclub.getInstance().getConfig().getDouble("RingSize");
        double ringSeperation = Nightclub.getInstance().getConfig().getDouble("RingSeperation");
        double cubeSize = Nightclub.getInstance().getConfig().getDouble("CubeSize");
        
        
        //cube = new Cube(Util.getLocFromConf("Cube"),cubeSize);
        
        rings = new Rings(Util.getLocFromConf("Rings"),
        ringCount,ringSize,ringSeperation);
        /*for(Object lst : ccList) {
            i++;
            List<Double> list = (List<Double>) lst;
            World mainWorld = Nightclub.getInstance().getServer().getWorlds().get(0);
            CCList.add(new CeilingCrystals(
                    new Location(mainWorld,list.get(0),list.get(1),list.get(2)),
                    list.get(3).intValue()));
        }*/
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
    public static List<FrontFacerCircle> getFrontFacerList() {
        return FFList;
    }
    public static Rings getRings() {return rings;}
    /*public static Cube getCube() {return cube;}*/
    /*public static List<CeilingCrystals> getCCList() { return CCList;}*/
}
