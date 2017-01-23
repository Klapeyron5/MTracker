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
    private final int DFTNumbers = 4000;
    private int DFTCounter = DFTNumbers;

    MainActivity link =this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        audioSampler = new AudioSampler(this);
        audioDrawer = new AudioDrawer(link);
        dft = new DFT();
        setContentView(audioDrawer);

        startSampling();
    }

    public void setNextSoundBuffer(short[] buffer) {
        drawThread = audioDrawer.getThread();
    //    if(drawThread == null)
     //       Log.i("TAG","drawThread == null");

        if (DFTCounter < DFTNumbers) {
            if (DFTCounter % 4 == 0)
                dft.setBuffer(buffer);
            DFTCounter++;
        }

        switch (audioDrawer.drawMode) {
            case 1:
                drawThread.setEnvelopeBuffer(buffer);
                break;
            case 2:
                if(dft.getXk() != null)
                    drawThread.setFrequencySpectrumBuffer(dft.getXk(),dft.getMaxXk(),dft.getMaxk());
                break;
        }

    //   drawThread.setBuffer(buffer);
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

    public void startDFT() {
        DFTCounter = 0;
    }

    public void stopDFT() {
        DFTCounter = 40000;
    }
}
