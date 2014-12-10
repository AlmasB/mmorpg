package uk.ac.brighton.uni.ab607.mmorpg.test;

import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import uk.ac.brighton.uni.ab607.mmorpg.client.fx.Sprite;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacter.Dir;

import com.almasb.java.util.ByteStream;

public class GameCharacterByteStream implements ByteStream {

    protected int x, y;
    public byte place = 0;

    protected int spriteID;
    protected int runtimeID, id;

    public enum Dir {
        UP, DOWN, LEFT, RIGHT
    }

    public Dir direction = Dir.DOWN;

    @Override
    public void loadFromByteArray(byte[] data) {
        int xy = ByteStream.byteArrayToInt(data, 0);

        x = xy >> 16 & 0xFFFF;
        y = xy & 0xFFFF;

        spriteID = ByteStream.byteArrayToInt(data, 4);

        place = (byte)(data[8] >> 2 & 0b11);
        direction = Dir.values()[(byte)(data[8] & 0b11)];
    }

    @Override
    public byte[] toByteArray() {
        byte[] data = new byte[13];

        int xy = x << 16 | y;

        ByteStream.intToByteArray(data, 0, xy);
        ByteStream.intToByteArray(data, 4, spriteID);

        data[8] = (byte)((place << 2 | direction.ordinal()) & 0xFF);

        int ids = id << 16 | runtimeID;

        ByteStream.intToByteArray(data, 9, ids);

        return data;
    }

    // test methods
    public void setXY(int xy) {
        x = xy >> 16 & 0xFFFF;
        y = xy & 0xFFFF;
    }

    public void setPlaceDir(byte b) {
        place = (byte)(b >> 2 & 0b11);
        direction = Dir.values()[(byte)(b & 0b11)];
    }

    public void setSpriteID(int id) {
        spriteID = id;
    }

    public void setIDs(int ids) {
        runtimeID = ids & 0xFFFF;
        id = ids >> 16 & 0xFFFF;
    }
}
