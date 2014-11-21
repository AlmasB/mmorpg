package uk.ac.brighton.uni.ab607.mmorpg.client.fx;

import javafx.scene.Group;
import javafx.stage.StageStyle;

public abstract class UIFragmentWindow extends UIBaseWindow {

    protected double dragX, dragY;

    protected Group root = new Group();

    public UIFragmentWindow() {
        this.initStyle(StageStyle.TRANSPARENT);
        this.setAlwaysOnTop(true);

        root.setOnMousePressed(event -> {
            dragX = event.getSceneX();
            dragY = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            setX(event.getScreenX() - dragX);
            setY(event.getScreenY() - dragY);
        });
    }

    /**
     * When user hides the window fragment
     */
    public abstract void minimize();

    /**
     * When user makes the window fragment active
     * brings to front
     */
    public abstract void restore();
}
