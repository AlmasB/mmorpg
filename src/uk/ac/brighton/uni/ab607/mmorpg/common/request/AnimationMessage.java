package uk.ac.brighton.uni.ab607.mmorpg.common.request;

import com.almasb.common.util.ByteStream;

public abstract class AnimationMessage implements ByteStream {

    protected int x, y;
    private boolean sent = false;

    public AnimationMessage(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setSent() {
        sent = true;
    }

    public boolean isSent() {
        return sent;
    }
}
