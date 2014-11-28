package uk.ac.brighton.uni.ab607.mmorpg.common.request;

import com.almasb.common.util.ByteStream;

public class ImageAnimationMessage extends AnimationMessage {

    public static final int BYTE_STREAM_SIZE = 12;

    private int endX, endY;
    private int spriteID;

    public ImageAnimationMessage(int x, int y, int endX, int endY, int spriteID) {
        super(x, y);
        this.endX = endX;
        this.endY = endY;
        this.spriteID = spriteID;
    }

    @Override
    public void loadFromByteArray(byte[] data) {
        int xy = ByteStream.byteArrayToInt(data, 0);

        x = xy >> 16 & 0xFFFF;
        y = xy & 0xFFFF;

        int endXY = ByteStream.byteArrayToInt(data, 4);

        endX = endXY >> 16 & 0xFFFF;
        endY = endXY & 0xFFFF;

        spriteID = ByteStream.byteArrayToInt(data, 8);
    }

    @Override
    public byte[] toByteArray() {
        byte[] data = new byte[12];

        int xy = x << 16 | y;
        ByteStream.intToByteArray(data, 0, xy);

        int endXY = endX << 16 | endY;
        ByteStream.intToByteArray(data, 4, endXY);
        ByteStream.intToByteArray(data, 8, spriteID);

        return data;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndY() {
        return endY;
    }

    public int getSpriteID() {
        return spriteID;
    }
}
