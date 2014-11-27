package uk.ac.brighton.uni.ab607.mmorpg.common.request;

import com.almasb.common.util.ByteStream;

public class AnimationMessage implements ByteStream {

    private int x, y;
    private AnimationMessageType type;
    private String text;

    public enum AnimationMessageType {
        DAMAGE_TO_PLAYER, BASIC_DAMAGE_TO_ENEMY, SKILL_DAMAGE_TO_ENEMY, TEXT
    }

    public AnimationMessage(int x, int y, AnimationMessageType type, String text) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.text = text;
    }

    @Override
    public void loadFromByteArray(byte[] data) {
        // TODO Auto-generated method stub

    }

    @Override
    public byte[] toByteArray() {
        byte[] data = new byte[9];

        ByteStream.intToByteArray(data, 0, x);
        ByteStream.intToByteArray(data, 4, y);
        data[8] = (byte)type.ordinal();

        return data;
    }

}
