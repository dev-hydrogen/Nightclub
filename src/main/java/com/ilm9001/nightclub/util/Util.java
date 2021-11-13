package com.ilm9001.nightclub.util;

import java.io.File;
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
    
    public static Collection<String> getStringValuesFromArray(Object[] objects) {
        Collection<Object> collection = Arrays.asList(objects);
        Collection<String> strings = new ArrayList<>();
        if (objects instanceof File[]) {
            collection.forEach((file) -> strings.add(((File) file).getName()));
        } else {
            collection.forEach((object) -> strings.add(object.toString()));
        }
        return strings;
    }
}
