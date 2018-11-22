package soft.cliffhanger.hunter.bubblestream;

/**
 * Created by Ahmet on 07.11.2018.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {


    private GameThread gameThread;
    private Timer timer = new Timer();
    Bitmap bubbleBitmap1, greenB, redB, yellowB,pipeB;

    private Pipe pipe1;

    private Rect redlane, bluelane, greenlane;
    private Paint paintr, paintb, paintg, debugp;

    private ArrayList<Bubble> bubbles = new ArrayList<Bubble>();

    private Bubble bubble;
    private float line1,line2,line3;
    private boolean bubbletouched=false;

    private float touchX,touchY, swipeX;



    static final int MIN_DISTANCE=150;

    public GameSurface(Context context)  {
        super(context);

        // Make Game Surface focusable so it can handle events. .
        this.setFocusable(true);

        // Sét callback.
        this.getHolder().addCallback(this);
    }

    public void update()  {

        for(Bubble bubbletemp: bubbles){
            bubbletemp.update();
            if(bubbletemp.getY()<-200){
                if(bubbletemp.getCurrentLane()== bubbletemp.getGoalLane()){
                    Log.d("Points: ", "+1");
                }else{
                    Log.d("Points: ", "-1");
                }
                bubbles.remove(bubbletemp);
                //Log.d("Bubble Count", Integer.toString(bubbles.size()));
            }
        }

        //this.bubble.update();
    }



    @Override
    public void draw(Canvas canvas)  {
        super.draw(canvas);
        canvas.drawRect(redlane, paintr );
        canvas.drawRect(bluelane, paintb );
        canvas.drawRect(greenlane, paintg );
        pipe1.draw(canvas);
        //canvas.drawRect(pipe1.getHitboxPipe(), debugp);

        for(Bubble bubbletemp: bubbles){
            bubbletemp.draw(canvas);
            //canvas.drawRect(bubbletemp.getHitbox(), debugp);

        }

        //this.bubble.draw(canvas);
    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        bubbleBitmap1 = BitmapFactory.decodeResource(this.getResources(),R.drawable.bubble);
        greenB = BitmapFactory.decodeResource(this.getResources(),R.drawable.greenb);
        redB = BitmapFactory.decodeResource(this.getResources(),R.drawable.redb);
        yellowB = BitmapFactory.decodeResource(this.getResources(),R.drawable.yellowb);
        pipeB= BitmapFactory.decodeResource(this.getResources(),R.drawable.pipe);
        //this.bubble = new Bubble(this,bubbleBitmap1,141,this.getHeight());


        line1= this.getWidth()*0.25f;
        line2= this.getWidth()*0.50f;
        line3= this.getWidth()*0.75f;

        pipe1= new Pipe(this, pipeB, this.getWidth()*0.375f, this.getHeight()*0.5f);
        Log.d("Width", Integer.toString(this.getWidth()));
        Log.d("Lines", Float.toString(line1) + " | "+ Float.toString(line2) +" | "+Float.toString(line3));

        redlane = new Rect(((int)line1)-70, 0, ((int)line1)+70, this.getHeight());
        bluelane = new Rect(((int)line2)-70, 0, ((int)line2)+70, this.getHeight());
        greenlane = new Rect(((int)line3)-70, 0, ((int)line3)+70, this.getHeight());

        paintr= new Paint();
        paintr.setColor(Color.RED);

        paintb= new Paint();
        paintb.setColor(Color.YELLOW);

        paintg= new Paint();
        paintg.setColor(Color.GREEN);

        debugp= new Paint();
        debugp.setColor(Color.GRAY);

        timer.schedule(new TimerTask(){

            public void run(){
                float spawnx= 10;
                Random rand=new Random();
                int randint=rand.nextInt(3);
                switch(randint){
                    case 1: spawnx=line1;break;
                    case 2: spawnx=line2;break;
                    case 0: spawnx=line3;break;

                }
                int randgoal= rand.nextInt(3);
                Bitmap goalBitmap= bubbleBitmap1;
                float goalLane=line1;
                switch(randgoal){
                    case 0: goalBitmap=yellowB;
                            goalLane=line2;break;
                    case 1: goalBitmap=redB;
                            goalLane=line1;break;
                    case 2: goalBitmap=greenB;
                            goalLane=line3;break;
                }



                Bubble bbletemp= new Bubble(GameSurface.this, goalBitmap,spawnx,GameSurface.this.getHeight(), spawnx, goalLane);

                bubbles.add(bbletemp);
            }
        }, 0, 10000);

        this.gameThread = new GameThread(this,holder);
        this.gameThread.setRunning(true);
        this.gameThread.start();


    }


    private long mLastTime = 0;
    private int fps = 0, ifps = 0;
    @Override
    protected void onDraw(Canvas canvas) {
        //canvas.drawRect(redlane, paint );
        long now = System.currentTimeMillis();

        // perform other operations

        //Log.d("FPS", Integer.toString(fps));

        ifps++;
        if(now > (mLastTime + 1000)) {
            mLastTime = now;
            fps = ifps;
            ifps = 0;
        }
    }


    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry= true;
        while(retry) {
            try {
                this.gameThread.setRunning(false);

                // Parent thread must wait until the end of GameThread.
                this.gameThread.join();
            }catch(InterruptedException e)  {
                e.printStackTrace();
            }
            retry= true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(pipe1.getTouched()){
            pipe1.setY(event.getY());
            pipe1.setHitboxPipe();
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                float posX = event.getX();
                float posY = event.getY();
                //Log.d("PosX", Float.toString(posX));
                //Log.d("PosY", Float.toString(posY));
                Log.d("Hitbox Pipe Bottom:", Integer.toString(pipe1.getHitbox().bottom));
                Log.d("Hitbox Pipe Left:", Integer.toString(pipe1.getHitbox().left));
                if(pipe1.getHitboxPipe().contains((int)posX, (int) posY)==true && pipe1.getTouched()!=true && !bubbletouched){
                    pipe1.setTouched(true);
                    Log.d("Pipe", "touched");
                }

                for(Bubble bubbletemp: bubbles){
                    if(bubbletemp.getHitbox().contains((int)posX, (int) posY)==true){
                        bubbletouched=true;
                        touchX= event.getX();
                        touchY= event.getX();
                        bubbletemp.setTouched(true);

                        Log.d("TOUCHED: ", "TRUE");
                        Log.d("Hitbox Bottom: ", Integer.toString(bubbletemp.getHitbox().bottom));
                        Log.d("Hitbox Left: ", Integer.toString(bubbletemp.getHitbox().left));
                    }

                }


                break;
            }
            case MotionEvent.ACTION_UP: {
                swipeX= event.getX();
                float deltaX= swipeX-touchX;

                bubbletouched=false;

                if(pipe1.getTouched()){
                    pipe1.setTouched(false);
                }
                //Log.d("DeltaX: ", Float.toString(deltaX));
                if(Math.abs(deltaX)> MIN_DISTANCE){
                    //swipe nach rechts
                    for(Bubble bubbletemp: bubbles){
                        if(bubbletemp.getTouched()){
                            if(bubbletemp.getHitbox().intersect(pipe1.getHitboxPipe())){
                                if(deltaX>0){
                                    if(bubbletemp.getCurrentLane()<this.line3){
                                        bubbletemp.swipeRight(this.getWidth()*0.25f);
                                    }

                                }else if(deltaX<0){
                                    if(bubbletemp.getCurrentLane()-this.getWidth()*0.25f>0){
                                        bubbletemp.swipeLeft(this.getWidth()*0.25f);
                                    }

                                }

                            }


                            bubbletemp.setTouched(false);

                        }else{
                            bubbletemp.setTouched(false);
                        }

                    }

                }else{
                    for(Bubble bubbletemp: bubbles) {
                        bubbletemp.setTouched(false);
                    }
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE: {

                break;
            }
        }

        return true;
    }

}