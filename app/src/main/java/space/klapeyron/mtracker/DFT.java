package space.klapeyron.mtracker;

import android.util.Log;

/**
 * Created by Klapeyron on 22.01.2017.
 */

public class DFT {
    private short[] xn;
    private double[] Xk;

    public DFT() {

    }

    public void setBuffer(short[] b) {
        xn = b;
        DFTThread dftThread = new DFTThread();
        dftThread.start();
    }

    private class DFTThread extends Thread {

        @Override
        public void run() {
            Log.i("TAG","DFT: "+this);
            int N = xn.length;
            Xk = new double[N];
            short re = 0;
            short im = 0;
            float pi2N = 2*(float)3.1415926535/(float) N;

            for(int j=0;j<N;j++) {
                for (int i = 0; i < N; i++) {
                    Xk[j] = xn[i]*Math.pow(Math.pow(Math.cos(pi2N*j*i),2)+Math.pow(Math.sin(pi2N*j*i),2),0.5);
                }
            }

            Log.i("TAG","DFT: "+this);
            this.interrupt();
        }
    }
}
