package space.klapeyron.mtracker;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Klapeyron on 04.01.2017.
 */

public class FileWorker {

    public void writeToFile(String str) {
        //FILE
        File fileName = null;
        FileOutputStream os = null;
        if (isExternalStorageWritable()) {
            File sdDir = android.os.Environment.getExternalStorageDirectory();
            File dir = new File(sdDir.getAbsolutePath() + "/MTracker/");
            dir.mkdir();
            fileName = new File(dir, "example.txt");
            try {
                os = new FileOutputStream(fileName, true);
          //      data = "";
          //      if (measure_counter == 0) data += "coords," + X + "," + Y + "\n";
          //      data += D + ",";
           //     for (i = 0; i < MAC.size(); i++) {
            //        data += MAC.get(i) + "," + averpower.get(i).toString() + ",";
            //    }
           //     data += "\n";
                os.write(str.getBytes());
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("TAG", "Write_exception");
            }
        } else {
            Log.i("TAG", "SD_not_available");
        }
        //FILE
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
