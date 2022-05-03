package exposed.hydrogen.nightclub.json;

import exposed.hydrogen.nightclub.Nightclub;
import exposed.hydrogen.nightclub.light.data.LightData;
import exposed.hydrogen.nightclub.light.data.RingMovementData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class JSONUtils {
    public static final File LIGHT_JSON = new File(Nightclub.DATA_FOLDER + "/" + Nightclub.JSON_FILE_NAME);

    public static FileReader getReader(File file) {
        try {
            return new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static LightData addNewDataIfNull(LightData lightData) {
        LightData data = lightData.clone();
        if(data.getRingMovementData() == null) {
            data.setRingMovementData(new RingMovementData());
        }
        return data;
    }
}
