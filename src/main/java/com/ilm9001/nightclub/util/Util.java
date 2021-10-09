package com.ilm9001.nightclub.util;

import java.text.NumberFormat;
import java.text.ParseException;

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
}
