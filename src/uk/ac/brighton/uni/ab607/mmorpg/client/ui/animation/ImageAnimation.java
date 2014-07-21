package uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation;

import uk.ac.brighton.uni.ab607.libs.graphics.GraphicsContext;
import uk.ac.brighton.uni.ab607.libs.io.Resources;
import uk.ac.brighton.uni.ab607.mmorpg.client.R;

public class ImageAnimation extends Animation {
    /**
     *
     */
    private static final long serialVersionUID = 1496702577417839916L;

    private String imageFileName;
    private int spriteX = 0, spriteY = 0;
    private int endX = 0, endY = 0;
    private int dx = 0, dy = 0;

    public ImageAnimation(int x, int y, float maxDuration, String imageFileName) {
        /*super(x, y, maxDuration);
        this.imageFileName = imageFileName;
        endX = x;
        endY = y;*/
        this(x, y, x, y, maxDuration, imageFileName);
    }

    public ImageAnimation(int x, int y, int endX, int endY, float maxDuration, String imageFileName) {
        super(x, y, maxDuration);
        this.imageFileName = imageFileName;
        this.endX = endX;
        this.endY = endY;
    }

    @Override
    protected void updateImpl(float completed) {
        // 64 images in levelUP.jpg
        int which = (int)(completed * 63);
        spriteX = which % 8;
        spriteY = which / 8;    // number of columns

        dx = (int)(completed * (endX - x));
        dy = (int)(completed * (endY - y));
    }

    @Override
    public void draw(GraphicsContext g) {
        int tmpX = x - g.getRenderX() + dx;
        int tmpY = y - g.getRenderY() + dy;
        g.drawImage(R.IMAGE.LEVEL_UP, tmpX, tmpY, tmpX+40, tmpY+40,
                spriteX*128, spriteY*128, spriteX*128+128, spriteY*128+128);  // make general

    }
}
