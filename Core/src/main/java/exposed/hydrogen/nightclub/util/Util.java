package exposed.hydrogen.nightclub.util;

import com.google.gson.JsonArray;
import exposed.hydrogen.nightclub.Nightclub;
import exposed.hydrogen.nightclub.beatmap.BeatmapParser;
import exposed.hydrogen.nightclub.beatmap.InfoData;
import exposed.hydrogen.nightclub.commands.CommandError;
import exposed.hydrogen.nightclub.light.Light;
import exposed.hydrogen.nightclub.light.LightUniverse;
import exposed.hydrogen.nightclub.light.Ring;
import exposed.hydrogen.resources.ResourcePackHandler;
import exposed.hydrogen.resources.Resources;
import net.kyori.adventure.key.Key;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.sound.Sound;
import team.unnamed.creative.sound.SoundEvent;
import team.unnamed.creative.sound.SoundRegistry;

import java.awt.*;
import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.*;

public class Util {
    public static double getDegreesFromPercentage(double percentage) {
        return 360 * percentage / 100;
    }

    public static double lerp(Number start, Number end, Number fraction, Easing easing) {
        Number easedFraction = easing.getFunction().apply(fraction.doubleValue());
        return start.doubleValue() + (end.doubleValue() - start.doubleValue()) * easedFraction.doubleValue();
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
        } else if (objects instanceof Ring[]) {
            collection.forEach((ring) -> strings.add(((Ring) ring).getName()));
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
        String formattedErrors = "\u00A7c" + "One or more errors occurred while trying to execute the command:" + System.lineSeparator();
        for (CommandError error : errors) {
            if (error == CommandError.VALID) {
                continue;
            }
            // blue + italic
            formattedErrors += "\u00A79" + "\u00A7o" + error.getErrorMessage() + System.lineSeparator();
        }
        return formattedErrors;
    }

    public static List<Sound.File> getSoundFiles(File path) {
        var sounds = getSounds(path);
        List<Sound.File> soundFiles = new LinkedList<>();
        for (Map<SoundEvent,Sound.File> sound : sounds.values()) {
            soundFiles.addAll(sound.values());
        }
        return soundFiles;
    }

    public static SoundRegistry getSoundRegistry(File path) {
        var sounds = getSounds(path);
        var soundEvents = new LinkedList<SoundEvent>();
        for(Map<SoundEvent,Sound.File> soundEvent : sounds.values()) {
            soundEvent.forEach((soundEvent1, soundFile) -> soundEvents.add(soundEvent1));
        }
        return getSoundRegistry(soundEvents);
    }

    public static Map<InfoData,Map<SoundEvent,Sound.File>> getSounds(File path) {
        Map<InfoData,Map<SoundEvent,Sound.File>> sounds = new LinkedHashMap<>();
        File[] folders = path.listFiles(File::isDirectory);
        if(folders == null) {
            return sounds;
        }
        for (File folder : folders) {
            InfoData infoData = BeatmapParser.getInfoData(folder.getName(),true);
            if(infoData == null) {
                continue;
            }
            String soundFilename = infoData.getSongFileName();
            File file = new File(folder.getAbsolutePath() + File.separator + soundFilename);
            if(!file.isFile()){
                // compatability, i have a few beatmaps manually named like this
                file = new File(folder.getAbsolutePath() + File.separator + folder.getName() + ".ogg");
            }
            Key key = Key.key(Nightclub.NAMESPACE, folder.getName());
            Sound sound = Sound.builder()
                    .nameSound(key)
                    .stream(true)
                    .build();
            SoundEvent event = SoundEvent.builder()
                    .sounds(sound)
                    .subtitle(infoData.getSongAuthorName() + " - " + infoData.getSongName() + " " + infoData.getSongSubName())
                    .build();
            Sound.File soundFile = Sound.File.of(key, Writable.file(file));
            sounds.put(infoData, Map.of(event,soundFile));
        }
        return sounds;
    }

    public static SoundRegistry getSoundRegistry(List<SoundEvent> events) {
        Map<String, SoundEvent> eventMap = new LinkedHashMap<>();
        for (SoundEvent event : events) {
            eventMap.put(event.sounds().get(0).key().value(), event);
        }
        return SoundRegistry.of(Nightclub.NAMESPACE, eventMap);
    }

    public static Color translateBeatSaberColor(JsonArray colorArray) {
        float r = colorArray.get(0).getAsFloat();
        float g = colorArray.get(1).getAsFloat();
        float b = colorArray.get(2).getAsFloat();
        float a = 1.0F;
        if (colorArray.size() >= 4 && colorArray.get(3) != null) {
            a = colorArray.get(3).getAsFloat();
        }
        float divisor = Math.max(Math.max(r, g), Math.max(Math.max(b, a), 1F)); // Beat saber color system is utter fucking dogshit.
        return new Color(r/divisor,g/divisor,b/divisor,a/divisor);
    }

    public static void addResources() {
        Thread asyncLoadPack = new Thread(() -> {
            try {
                var soundRegistry = Nightclub.getSoundRegistry();

                ResourcePackHandler resourcePackHandler = Resources.getResourcePackHandler();

                resourcePackHandler.addResources(Util.getSoundFiles(new File(Nightclub.DATA_FOLDER.getAbsolutePath())), false);
                resourcePackHandler.addCredit("Nightclub - Hydrogen");
                resourcePackHandler.addResource(soundRegistry, true);
                Nightclub.getChameleon().getLogger().info("Done compiling pack");
            } catch (Exception e) {
                Nightclub.getChameleon().getLogger().error("Failed to compile pack", e);
            }
        });
        asyncLoadPack.start();
    }
}
