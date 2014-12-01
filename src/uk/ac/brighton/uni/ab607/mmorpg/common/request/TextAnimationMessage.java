package uk.ac.brighton.uni.ab607.mmorpg.common.request;

import java.util.Arrays;

import com.almasb.common.util.ByteStream;

public class TextAnimationMessage extends AnimationMessage {

    public static final int BYTE_STREAM_SIZE = 64;

    private AnimationMessageType type;
    private String text;

    public enum AnimationMessageType {
        DAMAGE_TO_PLAYER, BASIC_DAMAGE_TO_ENEMY, SKILL_DAMAGE_TO_ENEMY, TEXT, BUFF
    }

    public TextAnimationMessage(int x, int y, AnimationMessageType type, String text) {
        super(x, y);
        this.type = type;
        this.text = text;
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

    public String getText() {
        return text;
    }

    public AnimationMessageType getType() {
        return type;
    }
}
