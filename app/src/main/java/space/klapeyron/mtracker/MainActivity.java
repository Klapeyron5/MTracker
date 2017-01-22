package space.klapeyron.mtracker;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

/**
 * Created by Klapeyron on 03.01.2017.
 */

public class MainActivity extends Activity {
    AudioSampler audioSampler;
    AudioDrawer audioDrawer;
    private AudioDrawer.DrawThread drawThread;
    private DFT dft;

    MainActivity link =this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        audioSampler = new AudioSampler(this);
    //    audioDrawer = new AudioDrawer(link);
        dft = new DFT();
    //    setContentView(audioDrawer);
        setContentView(R.layout.main);

        startSampling();
    }

    private void initAndRunApp() {
    }

    public void setNextSoundBuffer(short[] buffer) {
    //    Log.i("TAG","setNextSoundBuffer 0 "+buffer[2]);
        short[] b = buffer;
     //   Log.i("TAG","setNextSoundBuffer 1");

//        drawThread = audioDrawer.getThread();
    //    Log.i("TAG","setNextSoundBuffer 2");
//        if(drawThread == null)
//            Log.i("TAG","drawThread == null");
//        drawThread.setBuffer(buffer);
        dft.setBuffer(buffer);
    //    Log.i("TAG","setNextSoundBuffer 3");
    }

    private void startSampling() {
        if (audioSampler != null) {
            while (true) {
                audioSampler.start();
                return;
            }
        }
    }

    private void stopSampling() {
        if (audioSampler != null) {
            while(true) {
                audioSampler.stop();
                return;
            }
        }
    }
}
