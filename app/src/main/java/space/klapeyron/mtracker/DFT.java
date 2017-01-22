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
        //    Log.i("TAG","DFT: "+this);
            int N = xn.length;
            Xk = new double[N/2];
            double re = 0;
            double im = 0;
            float pi2N = 2*(float)3.1415926535/(float) N;
            double[] maxXk = {-1,-1,-1,-1,-1};
            int[] maxk = {-1,-1,-1,-1,-1};
            double xnwn = 0;

            for(int j=0;j<Xk.length;j++) {
                for (int i = 0; i < N; i++) {
                    xnwn = xn[i]*0.5*(1-Math.cos(2*(float)3.1415926535*i/(N-1)));
                    re += xnwn*Math.cos(pi2N*j*i);
                    im += xnwn*Math.sin(pi2N*j*i);
                }
                Xk[j] += Math.pow(Math.pow(re,2)+Math.pow(im,2),0.5);

                for(int p=0;p<5;p++) {
                    if (Xk[j]>maxXk[p]) {
                        for(int l=4;l>p;l--) {
                            maxXk[l]=maxXk[l-1];
                        }
                        maxXk[p] = Xk[j];
                        maxk[p] = j;
                        p = 5;
                    }
                }

            /*    if (Xk[j]>maxXk[0]) {
                    maxXk[0] = Xk[j];
                    maxk[0] = j;
                }*/
            }

            float helpN = (float)44100/(float)N;
            Log.i("TAG","DFT: "+this+" max freq: " + (float)maxk[0]*helpN+" " + (float)maxk[1]*helpN
                    +" " + (float)maxk[2]*helpN+" " + (float)maxk[3]*helpN+" " + (float)maxk[4]*helpN);
            this.interrupt();
        }
    }
}
