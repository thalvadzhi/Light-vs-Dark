package com.bearenterprises.shadowseeker;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Transformation;

import com.bearenterprises.Entities.LightSeeker;
import com.bearenterprises.Entities.ShadowSeeker;
import com.bearenterprises.Game.Game;
import com.bearenterprises.light.LightSource;
import com.bearenterprises.light.Point;

import java.util.ArrayList;


public class GameView extends View implements ScaleGestureDetector.OnScaleGestureListener{

    private ScaleGestureDetector mScaleDetector;


    private Paint rectPaint;
    private Paint lightPaint;

    private String levelName;
    private Game game;
    private boolean wasInit;
    private int mActivePointerId = MotionEvent.INVALID_POINTER_ID;
    private float mLastTouchX;
    private float mLastTouchY;
    private Bitmap endGame;
    private int lightPosition;
    private Paint endGamePaint;
    private Bitmap happyLightSeeker;

    private Bitmap happyLightSeekerScaled;
    private Bitmap unHappyLightSeekerScaled;
    private Bitmap unhappyLightSeeker;
    private Bitmap unHappyShadowSeeker;
    private Bitmap happyShadowSeeker;
    private Transformation transformation;
    private AlphaAnimation animation;
    private Paint endGamePaint1;
    private Context context;
    private int times;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        times = 0;
        this.context = context;
        endGamePaint1 = new Paint();
        endGamePaint1.setAlpha(0);
        transformation = new Transformation();
        animation = new AlphaAnimation(0f, 1f);
        animation.setDuration(750);

        mScaleDetector = new ScaleGestureDetector(context, this);

        endGamePaint = new Paint();
        endGamePaint.setTextSize(getResources().getDimension(R.dimen.end_screen_text_size));
        endGamePaint.setTextAlign(Paint.Align.CENTER);
        endGamePaint.setAntiAlias(true);
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "avantgarde.ttf");
        endGamePaint.setTypeface(tf);

        endGame = this.makeEndGameBitmap(context);
        wasInit = false;


        happyLightSeeker = BitmapFactory.decodeResource(context.getResources(), R.drawable.light_seeker_happy2);
        happyLightSeekerScaled = Bitmap.createScaledBitmap(happyLightSeeker, 30, 30, false);

        unhappyLightSeeker = BitmapFactory.decodeResource(context.getResources(), R.drawable.light_seeker_unhappy2);
        unHappyLightSeekerScaled = Bitmap.createScaledBitmap(unhappyLightSeeker, 30, 30, false);

        Bitmap shadowSeekerH = BitmapFactory.decodeResource(context.getResources(), R.drawable.shadow_seeker_happy2);
        happyShadowSeeker = Bitmap.createScaledBitmap(shadowSeekerH, 30, 30, false);

        Bitmap shadowSeekerUh = BitmapFactory.decodeResource(context.getResources(), R.drawable.shadow_seeker_unhappy2);
        unHappyShadowSeeker = Bitmap.createScaledBitmap(shadowSeekerUh, 30, 30, false);


        rectPaint = new Paint();
        rectPaint.setColor(Color.BLACK);
        rectPaint.setStyle(Paint.Style.FILL);

        lightPaint = new Paint();
        lightPaint.setColor(Color.YELLOW);
        lightPaint.setStyle(Paint.Style.FILL);
        lightPaint.setAntiAlias(true);


    }

    @SuppressLint("NewApi")
    private Bitmap makeEndGameBitmap(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        android.graphics.Point point = new android.graphics.Point();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN){
            display.getRealSize(point);
        } else{
            display.getSize(point);
        }

        int width = point.x;
        int height = point.y;
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(width, height, conf);
        Canvas canvas = new Canvas(bmp);
        canvas.drawColor(0xC8FFFFFF);
        String congratulations = "CONGRATULATIONS";
        String youWin = "YOU WIN";
        float textSize = getResources().getDimension(R.dimen.end_screen_text_size);
        canvas.drawText(congratulations, width / 2 , height / 2, this.endGamePaint );
        canvas.drawText(youWin, width / 2 , height / 2 + textSize + 10, this.endGamePaint );
        return bmp;
    }

    public void setLevelName(String levelName){
        this.levelName = levelName;
    }

    public void initGame(Context context){
        this.game = new Game(context, levelName);
        wasInit = true;
    }


@Override
public boolean onTouchEvent(MotionEvent ev) {
    mScaleDetector.onTouchEvent(ev);

    final int action = MotionEventCompat.getActionMasked(ev);

    switch (action) {
        case MotionEvent.ACTION_DOWN: {

            final int pointerIndex = MotionEventCompat.getActionIndex(ev);
            final float x = MotionEventCompat.getX(ev, pointerIndex);
            final float y = MotionEventCompat.getY(ev, pointerIndex);

            lightPosition = this.game.selectLight(x, y);
            if(lightPosition == -1) return false;

            mLastTouchX = x;
            mLastTouchY = y;

            mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
            break;
        }

        case MotionEvent.ACTION_MOVE: {
            final int pointerIndex =
                    MotionEventCompat.findPointerIndex(ev, mActivePointerId);

            final float x = MotionEventCompat.getX(ev, pointerIndex);
            final float y = MotionEventCompat.getY(ev, pointerIndex);


            final float dx = x - mLastTouchX;
            final float dy = y - mLastTouchY;

            this.game.update(dx, dy, this.lightPosition);
            invalidate();


            mLastTouchX = x;
            mLastTouchY = y;

            break;
        }

        case MotionEvent.ACTION_UP: {
            mActivePointerId = MotionEvent.INVALID_POINTER_ID;
            break;
        }

        case MotionEvent.ACTION_CANCEL: {
            mActivePointerId = MotionEvent.INVALID_POINTER_ID;
            break;
        }

        case MotionEvent.ACTION_POINTER_UP: {

            final int pointerIndex = MotionEventCompat.getActionIndex(ev);
            final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);

            if (pointerId == mActivePointerId) {

                final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                mLastTouchX = MotionEventCompat.getX(ev, newPointerIndex);
                mLastTouchY = MotionEventCompat.getY(ev, newPointerIndex);
                mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
            }
            break;
        }
    }
    return true;
}



    private void drawPaths(Canvas canvas){
        ArrayList<Path> paths = this.game.getPaths();
        ArrayList<Paint> paints = this.game.getVisibilityPaints();
        for (int i = 0; i < paths.size(); i++){
            canvas.drawPath(paths.get(i), paints.get(i));
        }
        for(LightSource light : this.game.getLights()){
            canvas.drawCircle((float)light.getX(),(float)light.getY(), (float)light.getRadius(), this.lightPaint);
        }


    }

    private void drawObstacles(Canvas canvas){
        ArrayList<ArrayList<Point>> obstacles = this.game.getObstacles();
        //last obstacle is screen size
        for(int i = 0; i < obstacles.size() - 1; i++){
            ArrayList<Point> obstacle = obstacles.get(i);
            Point topLeft = obstacle.get(0);
            Point bottomRight = obstacle.get(2);
            canvas.drawRect((float)topLeft.getX(), (float)topLeft.getY(), (float)bottomRight.getX(), (float)bottomRight.getY(), rectPaint);
        }
    }

    private void drawSeekers(Canvas canvas){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        ArrayList<ShadowSeeker> shadowSeekers = this.game.getShadowSeekers();
        ArrayList<LightSeeker> lightSeekers = this.game.getLightSeekers();
        for(LightSeeker lightSeeker : lightSeekers){
            if (lightSeeker.isHappy()){
                canvas.drawBitmap(happyLightSeekerScaled, (float) lightSeeker.getX() - 15, (float) lightSeeker.getY() - 15, paint);
            }else{
                canvas.drawBitmap(unHappyLightSeekerScaled, (float) lightSeeker.getX() - 15, (float) lightSeeker.getY() - 15, paint);
            }
        }
        for(ShadowSeeker shadowSeeker : shadowSeekers){
            if(shadowSeeker.isHappy()){
                canvas.drawBitmap(happyShadowSeeker, (float) shadowSeeker.getX() - 15, (float) shadowSeeker.getY() - 15, paint);
            }else{
                canvas.drawBitmap(unHappyShadowSeeker, (float) shadowSeeker.getX() - 15, (float) shadowSeeker.getY() - 15, paint);
            }
        }
    }

    private void drawEndGameScreen(Canvas canvas){
        canvas.drawBitmap(this.endGame, 0, 0, endGamePaint1);
        if(animation.hasStarted() && !animation.hasEnded()) {
            animation.getTransformation(System.currentTimeMillis(), transformation);
            endGamePaint1.setAlpha((int) (255 * transformation.getAlpha()));
            invalidate();
        }else{
            endGamePaint1.setAlpha(255);
            //invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(wasInit){
            canvas.drawColor(0xFF323232);
            this.drawPaths(canvas);
            this.drawObstacles(canvas);
            this.drawSeekers(canvas);
            if(this.game.hasEnded()){
                if(times == 0){
                    animation.start();
                    animation.getTransformation(System.currentTimeMillis(), transformation);
                    times++;
                }
               drawEndGameScreen(canvas);
            }
        }
    }


    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return false;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }


}
