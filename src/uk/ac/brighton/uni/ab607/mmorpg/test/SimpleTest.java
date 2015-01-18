package uk.ac.brighton.uni.ab607.mmorpg.test;

import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

public class SimpleTest extends OrionTestBase {

    @Override
    public Parent getResultsContent() {
        return new Pane(new Rectangle(300, 22));
    }

    @Override
    protected void run() throws Exception {
        // TODO Auto-generated method stub

    }
}
