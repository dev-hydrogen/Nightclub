package com.ilm9001.nightclub.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

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
    
    public static <T extends Enum<T>> Collection<String> getStringValuesFromEnum(Class<T> enumValues) {
        Collection<T> collection = new ArrayList<>(Arrays.asList(enumValues.getEnumConstants()));
        Collection<String> strings = new ArrayList<>();
        collection.forEach((pattern) -> strings.add(pattern.toString()));
        return strings;
    }
}
