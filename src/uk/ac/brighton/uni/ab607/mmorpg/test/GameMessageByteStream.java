package uk.ac.brighton.uni.ab607.mmorpg.test;

import java.util.Arrays;

import com.almasb.java.util.ByteStream;

public class GameMessageByteStream implements ByteStream {

    private int x, y;
    private AnimationMessageType type;
    private String text;

    public enum AnimationMessageType {
        DAMAGE_TO_PLAYER, BASIC_DAMAGE_TO_ENEMY, SKILL_DAMAGE_TO_ENEMY, TEXT, BUFF
    }

    @Override
    public void loadFromByteArray(byte[] data) {
        int xy = ByteStream.byteArrayToInt(data, 0);

        x = xy >> 16 & 0xFFFF;
        y = xy & 0xFFFF;

        type = AnimationMessageType.values()[data[4]];

        text = new String(Arrays.copyOfRange(data, 5, 64)).replace(new String(new byte[] {0}), "");
    }

    @Override
    public byte[] toByteArray() {
        byte[] data = new byte[64];

        int xy = x << 16 | y;
        ByteStream.intToByteArray(data, 0, xy);

        data[4] = (byte)type.ordinal();

        byte[] tmp = text.getBytes();
        for (int i = 0; i < Math.min(tmp.length, 59); i++) {
            data[5 + i] = tmp[i];
        }

        return data;
    }

    public void setXY(int xy) {
        x = xy >> 16 & 0xFFFF;
        y = xy & 0xFFFF;
    }

    public void setType(byte b) {
        type = AnimationMessageType.values()[b & 0b11];
    }

    public void setText(String text) {
        this.text = text;
    }
}
