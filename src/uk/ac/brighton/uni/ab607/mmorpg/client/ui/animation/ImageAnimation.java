package uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation;

import java.awt.Graphics2D;

import uk.ac.brighton.uni.ab607.libs.io.Resources;
import uk.ac.brighton.uni.ab607.mmorpg.client.ui.GraphicsContext;

public class ImageAnimation extends Animation {
    /**
     * 
     */
    private static final long serialVersionUID = 1496702577417839916L;
    
    private String imageFileName;
    private int spriteX = 0, spriteY = 0;

    public ImageAnimation(int x, int y, float maxDuration, String imageFileName) {
        super(x, y, maxDuration);
        this.imageFileName = imageFileName;
    }

    @Override
    protected void updateImpl(float completed) {
        // 64 images in levelUP.jpg
        int which = (int)(completed * 63);
        spriteX = which % 8;
        spriteY = which / 8;    // number of columns
               
    }

    @Override
    public void draw(GraphicsContext gContext) {
        Graphics2D g = gContext.getGraphics();
        int tmpX = x - gContext.getRenderX();
        int tmpY = y - gContext.getRenderY();
        g.drawImage(Resources.getImage(imageFileName), tmpX, tmpY, tmpX+40, tmpY+40,
                spriteX*128, spriteY*128, spriteX*128+128, spriteY*128+128, null);  // make general
        
    }
}
