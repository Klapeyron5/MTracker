package space.klapeyron.mtracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
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
    private int screenWidth;
    private int screenHeight;
    private int buttonNumber = 0;
    private int buttonWidth;
    private int buttonHeight;
    public int drawMode = 1;

    public AudioDrawer(Context context) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
        mainActivity = (MainActivity) context;

        DisplayMetrics metrics = new DisplayMetrics();
        mainActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;

        buttonHeight = screenHeight/7;
        buttonWidth = screenHeight/5;

        drawThread = new DrawThread(mHolder);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                whatButton((int)e.getX(),(int)e.getY());
                break;
            case MotionEvent.ACTION_UP:
                int bt = buttonNumber;
                whatButton((int)e.getX(),(int)e.getY());
                if (bt==buttonNumber) {
                    switch (bt) {
                        case 1:
                            Log.i("TAG","Button"+bt);
                            mainActivity.startDFT();
                            break;
                        case 2:
                            Log.i("TAG","Button"+bt);
                            mainActivity.stopDFT();
                            break;
                        case 3:
                            Log.i("TAG","Button"+bt);
                            switch (drawMode) {
                                case 1:
                                    drawMode = 2;
                                    break;
                                case 2:
                                    drawMode = 1;
                                    break;
                            }
                            break;
                    }
                }
                break;
        }
        return true;
    }

    private void whatButton(int x,int y) {
        if (y < buttonHeight) {
            if (x < buttonWidth) {
                buttonNumber = 1;
            } else {
                if (x < buttonWidth*2+1) {
                    buttonNumber = 2;
                } else {
                    if (x < buttonWidth*3+2) {
                        buttonNumber = 3;
                    }
                }
            }
        } else {
            buttonNumber = 0;
        }
    }

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
        public static int lightBlue = 0xff00FFFF;
        public static int red = 0xffFF0000;
    }

    public DrawThread getThread() {
        return drawThread;
    }

    class DrawThread extends Thread {
        private short[] buffer;
        private short[] fullBuffer;
        private short[] averageFullBuffer;
        private float scale = 1; //number of seconds visualized on screen
        private SurfaceHolder mSurfaceHolder;
        private int minBufferSize;
        private int sampleRate;
        private Paint paintForEnvelope;
        private Paint paintForMaxes;
        private Paint paintForButtons;
        private Paint paintForButtonsText;
        private int textSize;
        private int ypos;
        private int numberOfBuffToAverage;
        private float numberOfBuffToAverageRest;

        private short[] averageFreqSpectrBuffer;
        double[] maxXk = {-1,-1,-1};
        int[] maxk = {-1,-1,-1};

        DrawThread(SurfaceHolder surfaceHolder) {
            mSurfaceHolder = surfaceHolder;
            Log.i("TAG","DrawThread: mainActivity.audioSampler.minBufferSize"+mainActivity.audioSampler.minBufferSize);
            minBufferSize = mainActivity.audioSampler.minBufferSize;
            sampleRate = mainActivity.audioSampler.sampleRate;

            ypos = screenHeight/2;
            textSize = buttonHeight/3;

            averageFullBuffer = new short[screenWidth];

            numberOfBuffToAverage = sampleRate/screenWidth; //integer part of number
            numberOfBuffToAverageRest = (float)sampleRate/(float)screenWidth%1;
            Log.i("TAG","DrawThread: numberOfBuffToAverage: "+numberOfBuffToAverage);
            Log.i("TAG","DrawThread: numberOfBuffToAverageRest: "+numberOfBuffToAverageRest);

            paintForEnvelope = new Paint();
            paintForEnvelope.setAntiAlias(true);
            paintForEnvelope.setDither(true);
            paintForEnvelope.setColor(PaintDictionary.blue); //0xAA-HTML, AA - прозрачность
            paintForEnvelope.setStrokeWidth(1);
            paintForEnvelope.setStyle(Paint.Style.STROKE);
            paintForEnvelope.setStrokeJoin(Paint.Join.ROUND);
            paintForEnvelope.setStrokeCap(Paint.Cap.ROUND);

            paintForButtons = new Paint();
            paintForButtons.setAntiAlias(true);
            paintForButtons.setDither(true);
            paintForButtons.setColor(PaintDictionary.lightBlue); //0xAA-HTML, AA - прозрачность
            paintForButtons.setStrokeWidth(1);
            paintForButtons.setStyle(Paint.Style.STROKE);
            paintForButtons.setStrokeJoin(Paint.Join.ROUND);
            paintForButtons.setStrokeCap(Paint.Cap.ROUND);

            paintForButtonsText = new Paint();
            paintForButtonsText.setAntiAlias(true);
            paintForButtonsText.setDither(true);
            paintForButtonsText.setColor(PaintDictionary.lightBlue); //0xAA-HTML, AA - прозрачность
            paintForButtonsText.setTextSize(textSize);
            paintForButtonsText.setStyle(Paint.Style.STROKE);
            paintForButtonsText.setStrokeJoin(Paint.Join.ROUND);
            paintForButtonsText.setStrokeCap(Paint.Cap.ROUND);

            paintForMaxes = new Paint();
            paintForMaxes.setAntiAlias(true);
            paintForMaxes.setDither(true);
            paintForMaxes.setColor(PaintDictionary.red); //0xAA-HTML, AA - прозрачность
            paintForMaxes.setStrokeWidth(1);
            paintForMaxes.setStyle(Paint.Style.STROKE);
            paintForMaxes.setStrokeJoin(Paint.Join.ROUND);
            paintForMaxes.setStrokeCap(Paint.Cap.ROUND);
        }

        @Override
        public void run() {
            while(true) {
                Canvas localCanvas = null;
                try {
                    localCanvas = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        if (localCanvas != null) {
                            switch (drawMode) {
                                case 1:
                                    onDrawEnvelope(localCanvas);
                                    break;
                                case 2:
                                    onDrawFrequenceSpectrum(localCanvas);
                                    break;
                            }
                            onDrawButtons(localCanvas);
                        }
                    }
                } finally {
                    mSurfaceHolder.unlockCanvasAndPost(localCanvas);
                }
            }
        }

        private void onDrawButtons(Canvas lcanvas) {
            lcanvas.drawRect(0,0,buttonWidth,buttonHeight,paintForButtons);
            lcanvas.drawText("Start",(int)(buttonWidth/2-2.5*textSize/2),buttonHeight/2+textSize/2,paintForButtonsText);
            lcanvas.drawRect(buttonWidth+2,0,buttonWidth*2+2,buttonHeight,paintForButtons);
            lcanvas.drawText("Stop",(int)(buttonWidth+2+buttonWidth/2-2*textSize/2),buttonHeight/2+textSize/2,paintForButtonsText);
            lcanvas.drawRect(buttonWidth*2+4,0,buttonWidth*3+4,buttonHeight,paintForButtons);
            lcanvas.drawText("Change",(int)(buttonWidth*2+4+buttonWidth/2-3*textSize/2),buttonHeight/2+textSize/2,paintForButtonsText);

        }

        private void onDrawEnvelope(Canvas lcanvas) {
            lcanvas.drawColor(PaintDictionary.black);

        //    if(buffer != null)
        //        lcanvas.drawLine(screenWidth/2-buffer[0],screenHeight/2,screenWidth/2+buffer[0],screenHeight/2,paintForEnvelope);

            for(int i=0;i<screenWidth;i++) {
                lcanvas.drawLine(i,ypos,i,ypos+1+Math.round((float)averageFullBuffer[i]/(float)2500*(float)screenHeight/(float)2),paintForEnvelope);
            }
        }

        private void onDrawFrequenceSpectrum(Canvas lcanvas) {
            lcanvas.drawColor(PaintDictionary.black);

            if(averageFreqSpectrBuffer != null)
                for(int i=0;i<screenWidth;i++) {
                    if((i==maxk[0])||(i==maxk[1])||(i==maxk[2]))
                        lcanvas.drawLine(i,screenHeight,i,screenHeight-averageFreqSpectrBuffer[i],paintForMaxes);
                    else
                        lcanvas.drawLine(i,screenHeight,i,screenHeight-averageFreqSpectrBuffer[i],paintForEnvelope);
                }

            float helpN = (float)44100/(float)minBufferSize;
            lcanvas.drawText(Integer.toString((int)(maxk[0]*helpN)),screenWidth-3*buttonWidth,buttonHeight,paintForButtonsText);
            lcanvas.drawText(Integer.toString((int)(maxk[1]*helpN)),screenWidth-2*buttonWidth,buttonHeight,paintForButtonsText);
            lcanvas.drawText(Integer.toString((int)(maxk[2]*helpN)),screenWidth-1*buttonWidth,buttonHeight,paintForButtonsText);
        }

    /*    public void setBuffer(short[] b) {
            switch (drawMode) {
                case 1:
                    setEnvelopeBuffer(b);
                    break;
                case 2:
                    setFrequenceSpectrumBuffer(b);
                    break;
            }
        }*/

        public void setEnvelopeBuffer(short[] b) {
            if(fullBuffer == null)
                fullBuffer = new short[sampleRate];
            shiftFullBuffer(b);
            averageFullBuffer = new short[screenWidth];
            synchronized (averageFullBuffer) {
                toAverageFullBuffer();
            //    Log.i("TAG","BUFFERSIZE "+buffer.length);
                return;
            }
        }

        public void setFrequencySpectrumBuffer(double[] Xk,double[] XkMax,int[] kmax) {
            averageFreqSpectrBuffer = new short[screenWidth];
            maxXk = XkMax;
            maxk = kmax;
            synchronized (averageFreqSpectrBuffer) {
                int j=0;
                for(;j<Math.min(screenWidth,Xk.length);j++) {
                averageFreqSpectrBuffer[j] = (short)(Xk[j]/XkMax[0]*screenHeight);//((Xk[2*j]+Xk[2*j+1])/2);
                }
                for(;j<screenWidth;j++) {
                    averageFreqSpectrBuffer[j] = 0;
                }
                return;
            }
        }

        private void shiftFullBuffer(short[] b) {
            for(int i=0;i<sampleRate-minBufferSize;i++) { //sampleRate-minBufferSize =def= fullBuffer.length-b.length
                fullBuffer[i] = fullBuffer[i+b.length];
            }
            int j = 0;
            for(int i=sampleRate-minBufferSize;i<sampleRate;i++) {
                fullBuffer[i] = b[j];
                j++;
            }
        }

        private void toAverageFullBuffer() {
            int S = 0;
            int k = 0;
            int numbToAverage = numberOfBuffToAverage;
            float restBuffer = numberOfBuffToAverageRest;
            int j = 0;
            for(int i=0;i<sampleRate;i++) {
                if (k<numbToAverage) {
                    S = S + fullBuffer[i];
                    k++;
                } else {
                    restBuffer += numberOfBuffToAverageRest;
                    if (j<screenWidth) {
                        averageFullBuffer[j] = (short) (S / numbToAverage); //without rounding
                        j++;
                        S = 0;
                        k = 0;
                    }
                    if (restBuffer >= 1) {
                        numbToAverage = numberOfBuffToAverage + 1;
                        restBuffer = restBuffer % 1;
                    } else
                        numbToAverage = numberOfBuffToAverage;
                }
            }
        }

        private void toAverageFreqSpectrBuffer(double[] Xk) {
        /*    int freqBufferNumber = 0;
            int frewBufferCounter = 0;
            while (freqBufferNumber < 2000) {
                freqBufferNumber += screenWidth;
                frewBufferCounter++;
            }
            int j = 0;
            for(int i=0;i<freqBufferNumber;i++) {
                averageFreqSpectrBuffer[j] = (short)((Xk[i]+Xk[i+1])/2);
                i++;
                j++;
            }*/

            for(int j=0;j<screenWidth;j++) {
                averageFreqSpectrBuffer[j] = (short)Xk[j];//((Xk[2*j]+Xk[2*j+1])/2);
            }
        }
    }
}
