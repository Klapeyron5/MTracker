package space.klapeyron.mtracker;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by Klapeyron on 03.01.2017.
 */

public class MainActivity extends Activity {
    AudioSampler audioSampler;
    AudioDrawer audioDrawer;
    private AudioDrawer.DrawThread drawThread;
    private DFT dft;
    private final int DFTNumbers = 40000;
    private int DFTCounter = DFTNumbers;

    MainActivity link =this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    //    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        audioSampler = new AudioSampler(this);
    //    audioDrawer = new AudioDrawer(link);
        dft = new DFT();
    //    setContentView(audioDrawer);
        setContentView(R.layout.main);

        Button button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DFTCounter = 0;
            }
        });

        Button button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DFTCounter = 40000;
            }
        });

        startSampling();
    }

    private void initAndRunApp() {
    }

    public void setNextSoundBuffer(short[] buffer) {
    //    Log.i("TAG","setNextSoundBuffer 0 "+buffer[2]);
    //    short[] b = buffer;
     //   Log.i("TAG","setNextSoundBuffer 1");

//        drawThread = audioDrawer.getThread();
    //    Log.i("TAG","setNextSoundBuffer 2");
//        if(drawThread == null)
//            Log.i("TAG","drawThread == null");
//        drawThread.setBuffer(buffer);

        if (DFTCounter < DFTNumbers) {
            if (DFTCounter % 4 == 0)
                dft.setBuffer(buffer);
            DFTCounter++;
        }
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
