package space.klapeyron.mtracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;

/**
 * Created by Klapeyron on 11.01.2017.
 */

public class AudioDrawer extends SurfaceView {
    private MainActivity mainActivity;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;
    private int screenWidth;
    private int screenHeight;

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

        bitmap = Bitmap.createBitmap(screenWidth,screenHeight,Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint(Paint.DITHER_FLAG);

        canvas.drawColor(PaintDictionary.black);
    }

    public static class PaintDictionary {
        public static int black = 0xff000000;
        public static int blue = 0xff0022FF;
    }
}
