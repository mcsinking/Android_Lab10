package com.android.mcsin.lab10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by mcsin on 2015/12/2.
 */
public class GameView extends SurfaceView {

    private SurfaceHolder holder;
    private Bitmap red_target;
    private Bitmap bullet;
    private GameThread gthread = null;

    private float red_targetX = -150.0f;
    private float red_targetY = 100.0f;
    private float bulletX = -50.0f;
    private float bulletY = -101.0f;
    private float moveSpeed=3.0f;
    private int lvNo=0;
    private boolean lvUpActive=false;
    private boolean bulletYActive = false;
    private long airTimer;
    private long fireTimer=0;

    private int score = 0;
    private Paint scorePaint;


    public GameView(Context context) {
        super(context);

        holder = getHolder();
        holder.addCallback( new SurfaceHolder.Callback(){
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                red_target = BitmapFactory.decodeResource(getResources(), R.drawable.target_bullseye);
                bullet = BitmapFactory.decodeResource(getResources(),R.drawable.bullet );
                airTimer=System.currentTimeMillis();
                moveEnemy();
                scorePaint = new Paint();
                scorePaint.setColor(Color.BLACK);
                scorePaint.setTextSize(50.0f);

                makeThread();

                gthread.setRunning(true);
                gthread.start();


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        } );
    }

    public void makeThread()
    {
        gthread = new GameThread(this);

    }

    public void killThread()
    {
        boolean retry = true;
        gthread.setRunning(false);
        while(retry)
        {
            try
            {
                gthread.join();
                retry = false;
            } catch (InterruptedException e)
            {
            }
        }
    }


    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public int getLvNo() {
        return lvNo;
    }

    public void setLvNo(int lvNo) {
        this.lvNo = lvNo;
    }

    public long getFireTimer() {
        return fireTimer;
    }

    public void setFireTimer(long fireTimer) {
        this.fireTimer = fireTimer;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.drawColor(Color.WHITE);

        canvas.drawText("Lv:" + lvNo + " Score: " + String.valueOf(score), 10.0f, 50.0f, scorePaint);

        if (score%5==0&&score!=0&&lvUpActive==false){
            lvUpActive= true;
            lvNo++;
            if (moveSpeed>0){
                moveSpeed=moveSpeed+5.0f;
            }else {
                moveSpeed=moveSpeed-5.0f;
            }


        }
        if (score%5!=0){
            lvUpActive=false;
        }

        if(bulletYActive)
        {
            bulletY = bulletY - 10;

            if ( bulletY < 50 )
            {
                bulletX = -50.0f;
                bulletY = -101.0f;
                bulletYActive = false;

            }
            else
            {

                canvas.drawBitmap(bullet, bulletX, bulletY, null);
            }
        }
           moveEnemy();



        canvas.drawBitmap(red_target, red_targetX, red_targetY, null);

        if ( bulletX >= red_targetX && bulletX <= red_targetX + red_target.getWidth()
                && bulletY <= red_targetY + red_target.getHeight() && bulletY >= red_targetY
                )
        {
            red_target = BitmapFactory.decodeResource(getResources(), R.drawable.boom);
            score++;
            bulletYActive = false;
            bulletX = -50.0f;
            bulletY = -101.0f;

        }



    }
    public void moveEnemy(){
        double temp=Math.random()*10;
        int shiftTime=(int)temp*500;

        if (temp>5&&(System.currentTimeMillis()>airTimer+shiftTime)){
            moveSpeed=-moveSpeed;
            airTimer=System.currentTimeMillis();
        }


        red_targetX = red_targetX + moveSpeed;
        red_targetY=red_targetY+1.0f;

        if(red_targetX > getWidth()) {
            // red_targetX = -205.0f;
            moveSpeed=-moveSpeed;
            red_target = BitmapFactory.decodeResource(getResources(), R.drawable.target_bullseye);
        }

        if(red_targetX < -205.0f) {
            //red_targetX = -205.0f;
            moveSpeed=-moveSpeed;
            red_target = BitmapFactory.decodeResource(getResources(), R.drawable.target_bullseye);
        }
        if(red_targetY > getHeight()) {
            red_targetY = 100.0f;

            red_target = BitmapFactory.decodeResource(getResources(), R.drawable.target_bullseye);
        }

    }

    public void fire(float gunX,float gunY,int gunWidth,int gunHeight)
    {
        bulletYActive = true;
        bulletX = gunX +150.0f;
        bulletY = gunY-30.0f;

        fireTimer=System.currentTimeMillis();


    }

    public boolean isBulletYActive() {
        return bulletYActive;
    }


    public void onDestroy()
    {
        red_target.recycle();
        red_target = null;
        System.gc();
    }
}
