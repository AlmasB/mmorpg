package uk.ac.brighton.uni.ab607.mmorpg.client;

import java.awt.image.BufferedImage;

import com.almasb.java.io.AppResources;
import com.almasb.java.io.ResourceManager;

public class GameResources extends AppResources {

    public static final int MAP = 0;
    public static final int INVENTORY_LEFT = 1;
    public static final int INVENTORY_RIGHT = 2;
    public static final int SPRITESHEET = 3;
    public static final int CHEST = 4;
    public static final int ICON_SKILL = 5;
    public static final int LEVEL_UP = 6;
    public static final int CURSOR_WALK = 7;
    public static final int ENEMY_1 = 8;
    public static final int ENEMY_2 = 9;
    public static final int ENEMY_3 = 10;
    public static final int PLAYER_1 = 11;

    private static final BufferedImage[] cachedImages = new BufferedImage[30];

    static {
        cachedImages[MAP] = ResourceManager.loadImage("map1.png");
        cachedImages[INVENTORY_LEFT] = ResourceManager.loadImage("inv.png");
        cachedImages[INVENTORY_RIGHT] = ResourceManager.loadImage("inventory2.png");
        cachedImages[SPRITESHEET] = ResourceManager.loadImage("ss.png");
        cachedImages[CHEST] = ResourceManager.loadImage("chest.png");
        cachedImages[ICON_SKILL] = ResourceManager.loadImage("enemy.png");
        cachedImages[LEVEL_UP] = ResourceManager.loadImage("levelUP.png");
        cachedImages[CURSOR_WALK] = ResourceManager.loadImage("cursor_walk.png");
        cachedImages[ENEMY_1] = ResourceManager.loadImage("enemy1.png");
        cachedImages[ENEMY_2] = ResourceManager.loadImage("enemy2.png");
        cachedImages[ENEMY_3] = ResourceManager.loadImage("enemy3.png");
        cachedImages[PLAYER_1] = ResourceManager.loadImage("player1.png");
    }

    @Override
    public BufferedImage getImage(int resID) {
        if (resID < cachedImages.length)
            return cachedImages[resID];

        return null;
    }
}
