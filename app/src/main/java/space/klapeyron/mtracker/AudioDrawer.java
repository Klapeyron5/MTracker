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
    private MainActivity mainActivity;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;
    private int screenWidth;
    private int screenHeight;
    private DrawThread drawThread;

    public AudioDrawer(Context context) {
        super(context);
        mainActivity = (MainActivity) context;

        drawConstructor();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i("TAG","onDraw");
        canvas.drawBitmap(bitmap,0,0,paint);
    }

    private void drawConstructor() {
        setBlackScreen();
        invalidate();
    }

    private void setBlackScreen() {
        //вычисляем размеры экрана
        DisplayMetrics metrics = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;
        Log.i("TAG","Height: "+screenHeight);
        Log.i("TAG","Width: "+screenWidth);

        bitmap = Bitmap.createBitmap(screenWidth,screenHeight,Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint(Paint.DITHER_FLAG);

        canvas.drawColor(PaintDictionary.black);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.i("TAG", "GameView.surfaceCreated");
        drawThread = new DrawThread();
        drawThread.setRunning(true);
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
            try {
                drawThread.setRunning(false);
                Log.i(MainActivity.commonTAG, "GameView.surfaceDestroyed setRunning()");
                drawThread.join();
                Log.i(MainActivity.commonTAG, "GameView.surfaceDestroyed join()");
                retry = false;
            } catch (InterruptedException e) {
                Log.i(MainActivity.commonTAG, "GameView.surfaceDestroyed.NOTjoin()");
            }
        }
    }


    public static class PaintDictionary {
        public static int black = 0xff000000;
        public static int blue = 0xff0022FF;
    }

    class DrawThread extends Thread {

        DrawThread() {

        }

        @Override
        public void run() {
            canvas = gameViewLink.getHolder().lockCanvas();
            onDraw();
            gameViewLink.getHolder().unlockCanvasAndPost(canvas);
        }

        private void onDraw() {

        }

        public void setBuffer(short[] paramArrayOfShort) {
            synchronized (buffer)
            {
                buffer = paramArrayOfShort;
                return;
            }
        }
    }
}
