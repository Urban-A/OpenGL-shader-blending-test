package palette.test.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;


public class PaletteHitbox extends GameObject {
    private Rectangle bounds;
    public float position;

    public PaletteHitbox(float x, float y, float width, float height, float position) {
        bounds= new Rectangle();
        this.x=x;
        this.y=y;
        bounds.x=this.x;
        bounds.y=this.y;
        bounds.height=height;
        bounds.width=width;
        this.position=position;
    }

    public Boolean overlaps(float x, float y){
        if(x>=bounds.x&&x<=bounds.x+bounds.width&&y>=bounds.y&&y<=bounds.y+bounds.height){
            return true;
        }else{
            return false;
        }
    }

    public Boolean overlaps(Rectangle rect){
        return(bounds.overlaps(rect));
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }




}
