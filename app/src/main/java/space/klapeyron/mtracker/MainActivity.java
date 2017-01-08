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
    AudioReceiver audioReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        audioReceiver = new AudioReceiver();

        Button buttonAudioReciever = (Button) findViewById(R.id.button);
        buttonAudioReciever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioReceiver.isRunning()) {
                    Log.i("TAG","buttonAudioReciever.stop()");
                    audioReceiver.stop();
                } else {
                    audioReceiver.start();
                }
            }
        });
    }
}
