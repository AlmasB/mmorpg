package uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation;

public class DamageTextAnimation extends TextAnimation {
    /**
     * 
     */
    private static final long serialVersionUID = -1450918967934562384L;

    public DamageTextAnimation(int x, int y, float duration, String text) {
        super(x, y, duration, text);
    }

    @Override
    public void updateImpl(float completed) {
        y -= 1;
    }
}
