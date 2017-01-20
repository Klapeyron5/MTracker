package space.klapeyron.mtracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Klapeyron on 11.01.2017.
 */

public class AudioDrawer extends SurfaceView implements SurfaceHolder.Callback {
    private AudioDrawer viewLink = this;
    private MainActivity mainActivity;
    private Bitmap bitmap;
    private Canvas canvas;
    public DrawThread drawThread;
    private SurfaceHolder mHolder;

    public AudioDrawer(Context context) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
        mainActivity = (MainActivity) context;
        drawThread = new DrawThread(mHolder);


    //    drawConstructor();
    }

    /*@Override
    protected void onDraw(Canvas canvas) {
        Log.i("TAG","onDraw");
        canvas.drawBitmap(bitmap,0,0,paint);
    }

    private void drawConstructor() {
        setBlackScreen();
        invalidate();
    }*/

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.i("TAG", "GameView.surfaceCreated");
        drawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.i("TAG", "GameView.surfaceDestroyed()");
        boolean retry = true;
        while (retry) {
            if (drawThread != null) {
                drawThread.interrupt();
                drawThread = null;}
            retry = false;
        }
    }

    public static class PaintDictionary {
        public static int black = 0xff000000;
        public static int blue = 0xff0022FF;
        public static int red = 0xffFF0000;
    }

    public DrawThread getThread() {
        return drawThread;
    }

    class DrawThread extends Thread {
        private short[] buffer;
        private SurfaceHolder mSurfaceHolder;
        private int minBufferSize;
        private int sampleRate;
        private Paint paintForEnvelope;
        private int screenWidth;
        private int screenHeight;
        private int numberOfBuffToAverage;
        private float numberOfBuffToAverageRest;

        DrawThread(SurfaceHolder surfaceHolder) {
            mSurfaceHolder = surfaceHolder;
            Log.i("TAG","DrawThread: mainActivity.audioSampler.minBufferSize"+mainActivity.audioSampler.minBufferSize);
            minBufferSize = mainActivity.audioSampler.minBufferSize;
            sampleRate = mainActivity.audioSampler.sampleRate;

            DisplayMetrics metrics = new DisplayMetrics();
            mainActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            screenHeight = metrics.heightPixels;
            screenWidth = metrics.widthPixels;

            numberOfBuffToAverage = sampleRate/screenWidth; //integer part of number
            numberOfBuffToAverageRest = (float)sampleRate/(float)screenWidth%1;
            Log.i("TAG","DrawThread: numberOfBuffToAverage: "+numberOfBuffToAverage);
            Log.i("TAG","DrawThread: numberOfBuffToAverageRest: "+numberOfBuffToAverageRest);

            paintForEnvelope = new Paint();
            paintForEnvelope.setAntiAlias(true);
            paintForEnvelope.setDither(true);
            paintForEnvelope.setColor(PaintDictionary.red); //0xAA-HTML, AA - прозрачность
            paintForEnvelope.setStrokeWidth(6);
            paintForEnvelope.setStyle(Paint.Style.STROKE);
            paintForEnvelope.setStrokeJoin(Paint.Join.ROUND);
            paintForEnvelope.setStrokeCap(Paint.Cap.ROUND);
        }

        @Override
        public void run() {
            while(true) {
                Canvas localCanvas = null;
                try {
                    localCanvas = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        if (localCanvas != null)
                            onDraw(localCanvas);

                    }
                } finally {
                    mSurfaceHolder.unlockCanvasAndPost(localCanvas);
                }
            }
        }

        private void onDraw(Canvas lcanvas) {
            lcanvas.drawColor(PaintDictionary.black);

            if(buffer != null)
                lcanvas.drawLine(screenWidth/2-buffer[0],screenHeight/2,screenWidth/2+buffer[0],screenHeight/2,paintForEnvelope);


        }

        public void setBuffer(short[] b) {
            if(buffer == null)
                buffer = new short[0];
            synchronized (buffer) {
                buffer = b;
            //    Log.i("TAG","BUFFERSIZE "+buffer.length);
                return;
            }
        }
    }
}
