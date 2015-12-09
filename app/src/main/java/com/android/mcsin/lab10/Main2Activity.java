package com.android.mcsin.lab10;


import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Main2Activity extends Activity implements View.OnClickListener {

    private GameView gv;
    private Button shootButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);  //gets rid of the title at the top of the app


        gv = new GameView(this);
        shootButton = new Button(this);
        shootButton.setWidth(350);
        shootButton.setHeight(100);
        shootButton.setBackgroundColor(Color.LTGRAY);
        shootButton.setTextColor(Color.RED);
        shootButton.setTextSize(20);
        shootButton.setBackgroundResource(R.drawable.aircraft);
        shootButton.setText("Shoot it!");



        DisplayMetrics dm=getResources().getDisplayMetrics();
        final int screenWidth=dm.widthPixels;
        final int screenHeight=dm.heightPixels-50;

        shootButton.setOnTouchListener(new View.OnTouchListener() {

            int lastX, lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                int ea = event.getAction();
                Log.i("TAG", "Touch:" + ea);

                switch (ea) {
                    case MotionEvent.ACTION_DOWN:

                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;

                        int l = v.getLeft() + dx;
                        int b = v.getBottom() + dy;
                        int r = v.getRight() + dx;
                        int t = v.getTop() + dy;


                        if (l < 0) {
                            l = 0;
                            r = l + v.getWidth();
                        }

                        if (t < 0) {
                            t = 0;
                            b = t + v.getHeight();
                        }

                        if (r > screenWidth) {
                            r = screenWidth;
                            l = r - v.getWidth();
                        }

                        if (b > screenHeight) {
                            b = screenHeight;
                            t = b - v.getHeight();
                        }
                        v.layout(l, t, r, b);

                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        Toast.makeText(Main2Activity.this,
                                "Currently Position：" + l + "," + t + "," + r + "," + b,
                                Toast.LENGTH_SHORT).show();
                        v.postInvalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }


                return false;
            }
        });



        shootButton.setOnClickListener(this);
        shootButton.setGravity(Gravity.CENTER);

        FrameLayout GameLayout = new FrameLayout(this);
        LinearLayout ButtonLayout = new LinearLayout(this);
        ButtonLayout.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);

        ButtonLayout.addView(shootButton);

        GameLayout.addView(gv);
        GameLayout.addView(ButtonLayout);

        setContentView(GameLayout);


    }
    @Override
    protected void onPause()
    {
        SharedPreferences prefs = getSharedPreferences("AirBattleData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String tempscore=Integer.toString(gv.getScore());
        String tempmovespeed=String.valueOf(gv.getMoveSpeed());
        String templv=Integer.toString(gv.getLvNo());
        editor.putString("AirBattleScore", tempscore);
        editor.putString("AirBattleMoveSpeed", tempmovespeed);
        editor.putString("AirBattlelv", templv);

        editor.commit();


        super.onPause();
        gv.killThread(); //Notice this reaches into the GameView object and runs the killThread mehtod.
    }

    @Override
    protected void onResume()
    {

        SharedPreferences prefs = getSharedPreferences("AirBattleData",MODE_PRIVATE);
        String retrievedHighScore = prefs.getString("AirBattleScore", "0");
        String retrievedMoveSpeed = prefs.getString("AirBattleMoveSpeed", "3.0f");
        String retrievedlv = prefs.getString("AirBattlelv", "0");

        gv.setScore(Integer.valueOf(retrievedHighScore));
        gv.setMoveSpeed(Float.parseFloat(retrievedMoveSpeed));
        gv.setLvNo(Integer.valueOf(retrievedlv));

        super.onResume();
    }



    @Override
    protected void onDestroy() {

//        SharedPreferences prefs = getSharedPreferences("AirBattleData", MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//
//        String tempscore=Integer.toString(0);
//        String tempmovespeed=String.valueOf(0);
//        String templv=Integer.toString(0);
//        editor.putString("AirBattleScore", tempscore);
//        editor.putString("AirBattleMoveSpeed", tempmovespeed);
//        editor.putString("AirBattlelv", templv);
//
//        editor.commit();

        super.onDestroy();
        gv.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (gv.isBulletYActive()==false){
        gv.fire(shootButton.getX(), shootButton.getY(), shootButton.getWidth(), shootButton.getHeight());
        }
    }



    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        float touchedX = e.getX();
        float touchedY = e.getY();
        if (touchedX>shootButton.getX())
        shootButton.setX(shootButton.getX()+5.0f);
        if (touchedX<shootButton.getX())
            shootButton.setX(shootButton.getX() - 5.0f);

        if (touchedY>shootButton.getY())
            shootButton.setY(shootButton.getY() + 5.0f);
        if (touchedY<shootButton.getY())
            shootButton.setY(shootButton.getY() - 5.0f);



       if (gv.isBulletYActive()==false){
            //if ((System.currentTimeMillis()>(gv.getFireTimer()+1000))||gv.getFireTimer()==0)

            gv.fire(shootButton.getX(), shootButton.getY(), shootButton.getWidth(), shootButton.getHeight());
        }

        return true;
    }
}
