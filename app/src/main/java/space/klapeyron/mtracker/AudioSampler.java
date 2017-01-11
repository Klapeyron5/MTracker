package space.klapeyron.mtracker;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import android.os.Process;

/**
 * Created by Klapeyron on 03.01.2017.
 */

public class AudioSampler {
    private MainActivity mainActivity;
    private ReceiveThread receiveThread;
    private boolean isRunning;
    private AudioRecord audioRecord;
    private short[] buffer;
    int sampleRate = 44100;


    public AudioSampler(MainActivity m) {
        MainActivity mainActivity = m;
        audioRecord = null;
    }

    public void start() {
        receiveThread = new ReceiveThread(this);
        receiveThread.start();
    }

    public void stop() {
        if (receiveThread != null) {
            receiveThread.toStop();
            receiveThread.interrupt();
            receiveThread = null;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    class ReceiveThread extends Thread {
        AudioSampler audioSampler;

        ReceiveThread(AudioSampler l) {
            audioSampler = l;
        }

        @Override
        public void run() {
            Log.i("TAG", "AudioSampler.run()1");
            isRunning = true;
            int minBufferSize = AudioRecord.getMinBufferSize(sampleRate,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
            Log.i("TAG","MinBufferSize "+minBufferSize);

            if(minBufferSize == AudioRecord.ERROR) {
                Log.i("TAG","getMinBufferSize returned ERROR");
                return;
            }
            if(minBufferSize == AudioRecord.ERROR_BAD_VALUE) {
                Log.i("TAG","getMinBufferSize returned ERROR_BAD_VALUE");
                return;
            }

            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    sampleRate,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT,44100);

            if(audioRecord.getState() != AudioRecord.STATE_INITIALIZED)
            {
                Log.i("TAG","audioRecord.getState() != STATE_INITIALIZED");
                return;
            }

            try {
                audioRecord.startRecording();
            } catch(IllegalStateException e) {
                Log.i("TAG","audioRecord.startRecording() ERROR");
                e.printStackTrace();
                return;
            }

            buffer = new short[minBufferSize];
            long shortsRead = 0;//TODO
            long shortsRead1 = 0;//TODO
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
         //   StopTimingThread stopTimingThread = new StopTimingThread(audioSampler);
        //    stopTimingThread.start();
            while (isRunning) {
                int sampleRead = audioRecord.read(buffer,0,buffer.length);
                mainActivity.setNextSoundBuffer(buffer);
                shortsRead += sampleRead;//TODO
                shortsRead1 += buffer.length;//TODO

                if(sampleRead == AudioRecord.ERROR_INVALID_OPERATION) {
                    Log.i("TAG","audioRecord.read() returned ERROR_INVALID_OPERATION");
                    return;
                }
                if(sampleRead == AudioRecord.ERROR_BAD_VALUE) {
                    Log.i("TAG","audioRecord.read() returned ERROR_BAD_VALUE");
                    return;
                }
            }

            try {
                try {
                    Log.i("TAG","audioRecord.stop()");
                    audioRecord.stop();
                } catch(IllegalStateException e) {
                    Log.i("TAG","audioRecord.stop() ERROR");
                    e.printStackTrace();
                    return;
                }
            } finally {
                // освобождаем ресурсы
                audioRecord.release();
                audioRecord = null;
            }
            Log.i("TAG", "AudioSampler.run()2"+" ShortsRead: "+shortsRead+"; seconds: "+shortsRead*1.0/(1.0*sampleRate)+
                    "; seconds1: "+shortsRead1*1.0/(1.0*sampleRate));
        }

        public void toStop() {
            isRunning = false;
            Log.i("TAG", "AudioSampler.stop()");
        }

        public boolean isRunning() {
            return isRunning;
        }



        class StopTimingThread extends Thread {
            AudioSampler audioSampler;
            int soundTime = 10;//seconds

            StopTimingThread (AudioSampler l) {
                audioSampler = l;
            }

            @Override
            public void run() {
                try {
                    sleep(soundTime*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                audioSampler.stop();
            }
        }
    }
}
