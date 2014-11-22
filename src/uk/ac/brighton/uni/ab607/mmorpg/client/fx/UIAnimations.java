package uk.ac.brighton.uni.ab607.mmorpg.client.fx;

import uk.ac.brighton.uni.ab607.mmorpg.common.Player;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class UIAnimations {

    public static class LevelUpAnimation {
        public LevelUpAnimation(Group root, Player player) {
            Platform.runLater(() -> {
                Text text = new Text("Level UP!");
                text.setFill(Color.AQUAMARINE);
                text.setFont(UIConst.FONT);
                root.getChildren().add(text);
                TranslateTransition tt = new TranslateTransition(Duration.seconds(1.5), text);

                tt.setFromX(player.getX() - 30);
                tt.setFromY(player.getY());
                tt.setToX(player.getX() - 30);
                tt.setToY(player.getY() - 80);

                tt.setOnFinished(event -> {
                    root.getChildren().remove(text);
                });

                tt.play();
            });
        }
    }
}
