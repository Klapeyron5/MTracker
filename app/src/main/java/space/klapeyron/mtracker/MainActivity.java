package space.klapeyron.mtracker;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by Klapeyron on 03.01.2017.
 */

public class MainActivity extends Activity {
    AudioSampler audioSampler;
    AudioDrawer audioDrawer;

    MainActivity link =this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        audioSampler = new AudioSampler(this);
        audioDrawer = new AudioDrawer(link);
        setContentView(audioDrawer);

        startSampling();
    }

    private void initAndRunApp() {
    }

    public void setNextSoundBuffer(short[] buffer) {
        Log.i("TAG","setNextSoundBuffer");

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
