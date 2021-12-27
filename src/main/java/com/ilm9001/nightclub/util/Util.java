package com.ilm9001.nightclub.util;

import com.ilm9001.nightclub.commands.CommandError;
import com.ilm9001.nightclub.light.Light;
import com.ilm9001.nightclub.light.LightUniverse;
import org.bukkit.ChatColor;

import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Util {
    public static double getDegreesFromPercentage(double percentage) {
        return 360 * percentage / 100;
    }
    
    public static Number parseNumber(String number) {
        try {
            return NumberFormat.getInstance().parse(number);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public static Collection<String> getStringValuesFromArray(Object[] objects) {
        Collection<Object> collection = Arrays.asList(objects);
        Collection<String> strings = new ArrayList<>();
        collection.forEach(object -> strings.add(object.toString()));
        return strings;
    }
    public static Collection<String> getStringValuesFromFiles(File[] files) {
        Collection<File> collection = Arrays.asList(files);
        Collection<String> strings = new ArrayList<>();
        collection.forEach(file -> strings.add(file.getName()));
        return strings;
    }
    public static Collection<String> getStringValuesFromLights(List<Light> lights) {
        Collection<String> strings = new ArrayList<>();
        lights.forEach(light -> strings.add(light.getName()));
        return strings;
    }
    public static Collection<String> getStringValuesFromLightUniverses(List<LightUniverse> lightUniverses) {
        Collection<String> strings = new ArrayList<>();
        lightUniverses.forEach(lightUniverse -> strings.add(lightUniverse.getName()));
        return strings;
    }
    public static String formatErrors(List<CommandError> errors) {
        //cba to use components
        if (errors.stream().noneMatch(commandError -> commandError != CommandError.VALID)) {
            return "";
        }
        String formattedErrors = ChatColor.RED + "One or more errors occurred while trying to execute the command:" + System.lineSeparator();
        for (CommandError error : errors) {
            if (error == CommandError.VALID) {
                continue;
            }
            formattedErrors += "" + ChatColor.BLUE + ChatColor.ITALIC + error.getErrorMessage() + System.lineSeparator();
        }
        return formattedErrors;
    }
    
}
