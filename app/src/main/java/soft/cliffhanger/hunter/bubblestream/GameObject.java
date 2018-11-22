package soft.cliffhanger.hunter.bubblestream;

/**
 * Created by Ahmet on 07.11.2018.
 */
import android.graphics.Bitmap;
import android.graphics.Rect;

public abstract class GameObject {

    protected Bitmap image;

    protected final int rowCount;
    protected final int colCount;

    protected final int WIDTH;
    protected final int HEIGHT;

    protected final int width;


    protected final int height;
    protected float x;
    protected float y;

    protected Rect hitbox, hitboxPipe;

    public GameObject(Bitmap image, int rowCount, int colCount, float x, float y, float scalex, float scaley)  {
        Bitmap bitmapscaled= Bitmap.createScaledBitmap(image,Math.round(image.getWidth()*scalex),Math.round(image.getHeight()*scaley),true);

        this.image = bitmapscaled;
        this.rowCount= rowCount;
        this.colCount= colCount;

        this.x= x;
        this.y= y;



        this.WIDTH = bitmapscaled.getWidth();
        this.HEIGHT = bitmapscaled.getHeight();

        this.width = this.WIDTH/ colCount;
        this.height= this.HEIGHT/ rowCount;

        this.x=this.x-width/2;
        this.hitbox= new Rect( (int) this.x, (int) this.y, (int)this.x+this.width, this.height);
        this.hitboxPipe= new Rect( (int) this.x-this.width, (int) this.y, (int)this.x+this.width*2, (int)this.y+this.height);
    }


    protected Bitmap createSubImageAt(int row, int col)  {
        // createBitmap(bitmap, x, y, width, height).
        //Bitmap subImage = Bitmap.createBitmap(image, col* width, row* height ,width,height);
        return image;
    }

    public float getX()  {
        return this.x;
    }

    public float getY()  {
        return this.y;
    }


    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public Rect getHitbox() {return this.hitbox;}
    public Rect getHitboxPipe() {return this.hitboxPipe;}

    public void setHitbox(){this.hitbox.set((int) this.x, (int) this.y, (int)this.x+this.width, this.height);}
    public void setHitboxPipe(){this.hitboxPipe.set((int) this.x-this.width, (int) this.y, (int)this.x+this.width*2, (int)this.y+this.height);}
}
