package soft.cliffhanger.hunter.bubblestream;

/**
 * Created by Ahmet on 07.11.2018.
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;

public class Bubble extends GameObject {
    private static final int ROW_TOP_TO_BOTTOM = 0;
    private static final int ROW_RIGHT_TO_LEFT = 1;
    private static final int ROW_LEFT_TO_RIGHT = 2;
    private static final int ROW_BOTTOM_TO_TOP = 3;

    // Row index of Image are being used.
    private int rowUsing = ROW_LEFT_TO_RIGHT;

    private int colUsing;

    private Bitmap[] leftToRights;
    private Bitmap[] rightToLefts;
    private Bitmap[] topToBottoms;
    private Bitmap[] bottomToTops;

    // Velocity of game character (pixel/millisecond)
    public static final float VELOCITY = 0.1f;

    private int movingVectorX = 10;
    private int movingVectorY = 5;

    private long lastDrawNanoTime =-1;

    private GameSurface gameSurface;

    private boolean touched=false;

    private float currentLane;
    private float goalLane;

    public Bubble(GameSurface gameSurface, Bitmap image, float x, float y, float currentLane, float goalLane) {
        //Bitmap bitmapscaled= Bitmap.createScaledBitmap(image, 100,100,true);
        super(image, 1, 1, x, y, 0.25f, 0.25f);

        this.gameSurface= gameSurface;
        this.currentLane=currentLane;
        this.goalLane=goalLane;

        this.topToBottoms = new Bitmap[colCount]; // 3
        this.rightToLefts = new Bitmap[colCount]; // 3
        this.leftToRights = new Bitmap[colCount]; // 3
        this.bottomToTops = new Bitmap[colCount]; // 3

        for(int col = 0; col< this.colCount; col++ ) {
            this.topToBottoms[col] = this.createSubImageAt(ROW_TOP_TO_BOTTOM, col);
            this.rightToLefts[col]  = this.createSubImageAt(ROW_RIGHT_TO_LEFT, col);
            this.leftToRights[col] = this.createSubImageAt(ROW_LEFT_TO_RIGHT, col);
            this.bottomToTops[col]  = this.createSubImageAt(ROW_BOTTOM_TO_TOP, col);
        }
    }

    public Bitmap[] getMoveBitmaps()  {
        switch (rowUsing)  {
            case ROW_BOTTOM_TO_TOP:
                return  this.bottomToTops;
            case ROW_LEFT_TO_RIGHT:
                return this.leftToRights;
            case ROW_RIGHT_TO_LEFT:
                return this.rightToLefts;
            case ROW_TOP_TO_BOTTOM:
                return this.topToBottoms;
            default:
                return null;
        }
    }

    public Bitmap getCurrentMoveBitmap()  {
        Bitmap[] bitmaps = this.getMoveBitmaps();
        return bitmaps[this.colUsing];
    }


    public void update()  {
        this.colUsing++;
        if(colUsing >= this.colCount)  {
            this.colUsing =0;
        }
        // Current time in nanoseconds
        long now = System.nanoTime();

        // Never once did draw.
        if(lastDrawNanoTime==-1) {
            lastDrawNanoTime= now;
        }
        // Change nanoseconds to milliseconds (1 nanosecond = 1000000 milliseconds).
        float deltaTime =  ((now - lastDrawNanoTime)/ 1000000 );


        // Distance moves
        float distance = VELOCITY * deltaTime;

        double movingVectorLength = Math.sqrt(movingVectorX* movingVectorX + movingVectorY*movingVectorY);

        // Calculate the new position of the game character.
        //this.x = x +  (int)(distance* movingVectorX / movingVectorLength);
        this.y = y -  (float) (distance* movingVectorY / movingVectorLength);
        //Log.d("bubblepos", Float.toString(this.y));


        // When the game's character touches the edge of the screen, then change direction

/*        if(this.x < 0 )  {
            this.x = 0;
            this.movingVectorX = - this.movingVectorX;
        } else if(this.x > this.gameSurface.getWidth() -width)  {
            this.x= this.gameSurface.getWidth()-width;
            this.movingVectorX = - this.movingVectorX;
        }*/

        if(this.y < 0 )  {
            //this.y = 0;
            //this.movingVectorY = - this.movingVectorY;
        } else if(this.y > this.gameSurface.getHeight()- height)  {
            //this.y= this.gameSurface.getHeight()- height;
            //this.movingVectorY = - this.movingVectorY ;
        }

        // rowUsing
        if( movingVectorX > 0 )  {
            if(movingVectorY > 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_TOP_TO_BOTTOM;
            }else if(movingVectorY < 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_BOTTOM_TO_TOP;
            }else  {
                this.rowUsing = ROW_LEFT_TO_RIGHT;
            }
        } else {
            if(movingVectorY > 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_TOP_TO_BOTTOM;
            }else if(movingVectorY < 0 && Math.abs(movingVectorX) < Math.abs(movingVectorY)) {
                this.rowUsing = ROW_BOTTOM_TO_TOP;
            }else  {
                this.rowUsing = ROW_RIGHT_TO_LEFT;
            }
        }

        this.hitbox.set((int) this.x, (int) this.y, (int)this.x+this.width, (int)this.y+this.height);
        //Log.d("Hitbox: Left:", Float.toString(getHitbox().left));
        //Log.d("Hitbox: Bottom:", Float.toString(getHitbox().bottom));
    }

    public void draw(Canvas canvas)  {
        Bitmap bitmap = this.getCurrentMoveBitmap();

        canvas.drawBitmap(bitmap,x, y, null);
        // Last draw time.
        this.lastDrawNanoTime= System.nanoTime();
    }

    public void setMovingVector(int movingVectorX, int movingVectorY)  {
        this.movingVectorX= movingVectorX;
        this.movingVectorY = movingVectorY;
    }

    public void swipeRight(float x){

        this.x+=x;
        this.currentLane+=x;

    }

    public void swipeLeft(float x){

        this.x-=x;
        this.currentLane-=x;
    }

    public boolean getTouched(){return this.touched;}

    public void setTouched(boolean touched){ this.touched=touched;}

    public float getCurrentLane(){return this.currentLane;}

    public float getGoalLane(){ return this.goalLane;}


}
