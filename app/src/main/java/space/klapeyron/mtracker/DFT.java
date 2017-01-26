package space.klapeyron.mtracker;

import android.util.Log;

/**
 * Created by Klapeyron on 22.01.2017.
 */

public class DFT {
    private short[] xn;
    private double[] Xk;
    private double[] Xk2;
    double[] maxXk = {-1,-1,-1,-1,-1};
    int[] maxk = {-1,-1,-1,-1,-1};
    double[] maxXk2 = {-1,-1,-1,-1,-1};
    int[] maxk2 = {-1,-1,-1,-1,-1};
    public static double highk;

    public DFT() {

    }

    public void setBuffer(short[] b) {
        xn = b;
        DFTThread dftThread = new DFTThread();
        dftThread.start();
    }

    public double[] getXk() {
        return Xk;
    }

    public double[] getMaxXk() {
        return maxXk;
    }

    public int[] getMaxk() {
        return maxk;
    }

    private class DFTThread extends Thread {

        @Override
        public void run() {
        //    Log.i("TAG","DFT: "+this);
            int N = xn.length;
            DFT.highk = 2000.0/44100.0*N;
        //    Log.i("TAG","HIGHK " + highk);
            Xk = new double[N/2];
            Xk2 = new double[N/2];
            maxXk = new double[] {0,0,0,0,0};
            maxk = new int[] {0,0,0,0,0};
            maxXk2 = new double[] {0,0,0,0,0};
            maxk2 = new int[] {0,0,0,0,0};
            double re = 0;
            double im = 0;
            double re2 = 0;
            double im2 = 0;
            float pi2N = 2*(float)3.1415926535/(float) N;
            double xnwn = 0;

            for(int j=0;j<DFT.highk;j++) {
                for (int i = 0; i < N; i++) {
                    xnwn = xn[i]*0.5*(1-Math.cos(2*(float)3.1415926535*i/(N-1)));
                    re += xnwn*Math.cos(pi2N*j*i);
                    im += xnwn*Math.sin(pi2N*j*i);
                    xnwn = xn[i]*0.5*(1-Math.cos(2*(float)3.1415926535*i/(N-1)));
                    re2 += xnwn*Math.cos(pi2N*j*(i+1));
                    im2 += xnwn*Math.sin(pi2N*j*(i+1));
                }
                Xk[j] += Math.pow(Math.pow(re,2)+Math.pow(im,2),0.5);
                Xk2[j] += Math.pow(Math.pow(re,2)+Math.pow(im,2),0.5);
                if(Xk[j]!=Xk2[j])
                    Log.i("TAG","false");

            /*    for(int p=0;p<5;p++) {
                    if (Xk[j]>maxXk[p]) {
                        for(int l=4;l>p;l--) {
                            maxXk[l]=maxXk[l-1];
                        }
                        maxXk[p] = Xk[j];
                        maxk[p] = j;
                        p = 5;
                    }
                }

                for(int p=0;p<5;p++) {
                    if (Xk2[j]>maxXk2[p]) {
                        for(int l=4;l>p;l--) {
                            maxXk2[l]=maxXk2[l-1];
                        }
                        maxXk2[p] = Xk2[j];
                        maxk2[p] = j;
                        p = 5;
                    }
                }*/

            /*    if (Xk[j]>maxXk[0]) {
                    maxXk[0] = Xk[j];
                    maxk[0] = j;
                }*/
            }

        /*    float helpN = (float)44100/(float)N;
            Log.i("TAG","DFT: "+this+" max freq: " + (float)maxk[0]*helpN+" " + (float)maxk[1]*helpN
                    +" " + (float)maxk[2]*helpN);
            Log.i("TAG","DFT: "+this+" max freq: " + (float)maxk2[0]*helpN+" " + (float)maxk2[1]*helpN
                    +" " + (float)maxk2[2]*helpN);
            if (!isArraysEqual(maxk,maxk2))
                Log.i("TAG","EQUAL false");*/
            this.interrupt();
        }
    }

    private Boolean isArraysEqual(int[] a1, int[] a2) {
        Boolean f = true;
        if (a1.length == a2.length) {
            for (int i=0;i<a1.length;i++) {
                if(a1[i] != a2[i]) {
                    f = false;
                }
            }
        }
        return f;
    }
}
