package com.android.mcsin.lab10;

import android.annotation.SuppressLint;
import android.graphics.Canvas;

/**
 * Created by mcsin on 2015/12/2.
 */
public class GameThread extends Thread {
    private GameView view;
    private boolean running = false;

    public GameThread(GameView viewIn)
    {
        this.view = viewIn;
    }

    public void setRunning(boolean run)
    {
        running = run;
    }


    @SuppressLint("WrongCall")
    @Override
    public void run()
    {
        while (running)
        {
            Canvas c = null;
            try
            {
                c = view.getHolder().lockCanvas();
                synchronized (view.getHolder())
                {
                    view.onDraw(c);
                }
            }
            finally
            {
                if (c != null)
                {
                    view.getHolder().unlockCanvasAndPost(c);
                }
            }
        }
    }

}
