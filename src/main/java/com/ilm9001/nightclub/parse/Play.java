package com.ilm9001.nightclub.parse;

import com.ilm9001.nightclub.Nightclub;
import com.ilm9001.nightclub.Show;
import com.ilm9001.nightclub.parse.LE_list;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

import static org.bukkit.Bukkit.getServer;

public class Play {
    public static void play(String bfile, Show show) {
        // get specified path
        String df = Nightclub.getInstance().getDataFolder().getAbsolutePath();
        String bfile_a = df + "/" + bfile + "/" + bfile + ".dat";
    
        String info = df + "/" + bfile + "/" + "info.dat";
    
        JSONParser jsonParser = new JSONParser();
        JSONObject json_o;
        JSONObject json_info;
    
        // parse beatmap from path
        try {
            FileReader reader = new FileReader(bfile_a);
            FileReader inforeader = new FileReader(info);
        
            json_o = (JSONObject) jsonParser.parse(reader);
            json_info = (JSONObject) jsonParser.parse(inforeader);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return;
        }
        JSONArray json_ev_list = (JSONArray) json_o.get("_events");
        Object bpm = json_info.get("_beatsPerMinute");
        Object artistName = json_info.get("_songAuthorName");
        Object songName = json_info.get("_songName");
        Object mapperName = json_info.get("_levelAuthorName");
    
        double bpmVal;
        if (bpm instanceof Long) {
            bpmVal = ((Long) bpm).doubleValue();
        } else if (bpm instanceof Double) {
            bpmVal = (Double) bpm;
        } else {
            bpmVal = (double) bpm;
        }
        if (json_ev_list == null) return;
        
        // Iterate over event array, call method parseEvent for each element on list
        LE_list le_list = new LE_list();
        // noinspection unchecked
        json_ev_list.forEach(eve -> le_list.add((JSONObject) eve, (int) bpmVal));
    
        final int le_size = le_list.size();
        long last_time = 0;
        int failcount = 0;
        int n_ev = 0;
        // We check that time of events is always increasing.
        for (int i = 0; i < le_size; ++i) {
            ++n_ev;
            long t = le_list.get(i).time_ms;
            if (t >= last_time) {
                last_time = t;
            } else {
                ++failcount;
            }
        }
        Nightclub.getInstance().getLogger().info(String.format("Beatmap %s bpm %f len %d, failcount: %d, last time: %.1f s",
                bfile_a, bpmVal, le_size, failcount, last_time / 1000.0));
    
        if (n_ev == 0) {
            Nightclub.getInstance().getLogger().info("Beatmap with zero events?");
            return;
        }
        if (failcount > 0) {
            Nightclub.getInstance().getLogger().info("Beatmap is invalid, time runs backwards somewhere in it.");
            return;
        }
    
        // New scheduling method!
        if (show.Run(le_list, last_time / 1000.0)) {
            Player[] players = getServer().getOnlinePlayers().toArray(new Player[0]);
            for (Player plr : players) {
                plr.playSound(plr.getLocation(), "minecraft:" + bfile, 1, 1);
                plr.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "Nightclub" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE + "Now playing " + ChatColor.AQUA + artistName + ChatColor.WHITE + " - " + ChatColor.AQUA + songName);
                plr.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "Nightclub" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE + "Mapped by " + ChatColor.AQUA + mapperName);
            }
        } else {
            Nightclub.getInstance().getLogger().info("Beatmap playback active, cannot start new one.");
        }
    }
}
