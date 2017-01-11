package space.klapeyron.mtracker;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by Klapeyron on 03.01.2017.
 */

public class MainActivity extends Activity {
    AudioSampler audioSampler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        Button buttonAudioReceiver = (Button) findViewById(R.id.button);
        buttonAudioReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioSampler.isRunning()) {
                    Log.i("TAG","buttonAudioReceiver.stop()");
                } else {
                    audioReceiver.start();
                }
            }
        });
    }
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
