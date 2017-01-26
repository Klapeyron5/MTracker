package space.klapeyron.mtracker;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import android.os.Process;

/**
 * Created by Klapeyron on 03.01.2017.
 */

public class AudioReceiver {
    private ReceiveThread receiveThread;
    private boolean isRunning;
    private AudioRecord audioRecord;
    private byte[] buffer;
    private short[][] buffers;

    int sampleRate = 44100;


    public AudioReceiver() {
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

        Thread thread = new Thread() {
            @Override
            public void run() {
                FileWorker fileWorker = new FileWorker();
                double k = 0.00;
                for (int i=0;i<buffers.length;i++) {
                    for(int j=0;j<buffers[i].length;j++){
                        String str = new String();
                        str = str + k + ';' + buffers[i][j] + '\n';
                        fileWorker.writeToFile(str);
                        k+=0.01;
                    }
                    //     Log.i("TAG","length "+buffers[i].length+" "+str);
                }
                Log.i("TAG","File writing finished");
            }
        };
    //    thread.start();


      //  for (short[] i: buffers) {
      //      Log.i("TAG","length "+i.length+" "+i[0]);
      //  }
    }

    public boolean isRunning() {
        return isRunning;
    }

    class ReceiveThread extends Thread {
        AudioReceiver audioReceiver;

        ReceiveThread(AudioReceiver l) {
            audioReceiver = l;
        }

        @Override
        public void run() {
            Log.i("TAG", "AudioReceiver.run()1");
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


            int i = 0;
            int emptyLoopCounter = 200000000;
            int loopCounter1     = 200;
            buffers = new short[1][minBufferSize];
            long shortsRead = 0;
            long shortsRead1 = 0;
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
            StopTimingThread stopTimingThread = new StopTimingThread(audioReceiver);
            stopTimingThread.start();
            while (isRunning) {
             //   if (i == loopCounter1) {
             //       Log.i("TAG", "AudioReceiver.run()");
             //       i = 0;
             //   }
            //    if (i==9999) {
            //        audioReceiver.stop();
             //   }

            //    buffer = new byte[1300];
                int sampleRead = audioRecord.read(buffers[0],0,buffers[0].length);
                shortsRead += sampleRead;
                shortsRead1 += buffers[0].length;

                if(sampleRead == AudioRecord.ERROR_INVALID_OPERATION) {
                    Log.i("TAG","audioRecord.read() returned ERROR_INVALID_OPERATION");
                    return;
                }
                if(sampleRead == AudioRecord.ERROR_BAD_VALUE) {
                    Log.i("TAG","audioRecord.read() returned ERROR_BAD_VALUE");
                    return;
                }

                i++;
            //    Log.i("TAG", "AudioReceiver.run() "+i);
            }

            try {
                try {
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
            Log.i("TAG", "AudioReceiver.run()2"+" ShortsRead: "+shortsRead+"; seconds: "+shortsRead*1.0/(1.0*sampleRate)+
                    "; seconds1: "+shortsRead1*1.0/(1.0*sampleRate));
        }

        public void toStop() {
            isRunning = false;
            Log.i("TAG", "AudioReceiver.stop()");
        }

        public boolean isRunning() {
            return isRunning;
        }



        class StopTimingThread extends Thread {
            AudioReceiver audioReceiver;
            int soundTime = 10;

            StopTimingThread (AudioReceiver l) {
                audioReceiver = l;
            }

            @Override
            public void run() {
                try {
                    sleep(soundTime*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                audioReceiver.stop();
            }
        }
    }
}
