package exposed.hydrogen.nightclub.util;

import exposed.hydrogen.nightclub.commands.CommandError;
import exposed.hydrogen.nightclub.light.Light;
import exposed.hydrogen.nightclub.light.LightUniverse;
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

    public static Number parseNumber(String number) throws ParseException {
        return NumberFormat.getInstance().parse(number);
    }

    public static Collection<String> getStringValuesFromArray(Object[] objects) {
        Collection<Object> collection = Arrays.asList(objects);
        Collection<String> strings = new ArrayList<>();
        if (objects instanceof File[]) {
            collection.forEach((file) -> strings.add(((File) file).getName()));
        } else if (objects instanceof Light[]) {
            collection.forEach((light) -> strings.add(((Light) light).getName()));
        } else if (objects instanceof LightUniverse[]) {
            collection.forEach((universe) -> strings.add(((LightUniverse) universe).getName()));
        } else {
            collection.forEach(object -> strings.add(object.toString()));
        }
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
