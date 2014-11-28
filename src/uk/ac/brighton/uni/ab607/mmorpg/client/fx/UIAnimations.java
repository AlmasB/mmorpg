package uk.ac.brighton.uni.ab607.mmorpg.client.fx;

import uk.ac.brighton.uni.ab607.mmorpg.common.Player;
import uk.ac.brighton.uni.ab607.mmorpg.common.math.GameMath;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class UIAnimations {

    private static Group root;
    private static Player player;

    public static void init(Group gameRoot, Player p) {
        root = gameRoot;
        player = p;
    }

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

    public static class BasicDamageAnimation {
        public BasicDamageAnimation(String dmgText, int x, int y) {
            Platform.runLater(() -> {
                Text text = new Text(dmgText);
                text.setFill(Color.CORNSILK);
                text.setFont(text.getFont().font(14));
                root.getChildren().add(text);
                TranslateTransition tt = new TranslateTransition(Duration.seconds(1.5), text);

                tt.setFromX(x);
                tt.setFromY(y);
                tt.setToX(x + GameMath.random(100) - 50);
                tt.setToY(y - GameMath.random(50));

                tt.setOnFinished(event -> {
                    root.getChildren().remove(text);
                });

                tt.play();
            });
        }
    }

    public static class SkillDamageAnimation {
        public SkillDamageAnimation(String dmgText, int x, int y) {
            Platform.runLater(() -> {
                Text text = new Text(dmgText);
                text.setFill(Color.BLUE);
                text.setFont(text.getFont().font(14));
                root.getChildren().add(text);
                TranslateTransition tt = new TranslateTransition(Duration.seconds(1.5), text);

                tt.setFromX(x);
                tt.setFromY(y);
                tt.setToX(x + GameMath.random(100) - 50);
                tt.setToY(y - GameMath.random(50));

                tt.setOnFinished(event -> {
                    root.getChildren().remove(text);
                });

                tt.play();
            });
        }
    }

    public static class MoneyGainAnimation {
        public MoneyGainAnimation(int money) {
            Platform.runLater(() -> {
                Text text = new Text(money + "G");
                text.setFill(Color.GOLD);
                text.setFont(UIConst.FONT);
                root.getChildren().add(text);
                TranslateTransition tt = new TranslateTransition(Duration.seconds(1.5), text);

                tt.setFromX(player.getX() + 100);
                tt.setFromY(player.getY());
                tt.setToX(player.getX() + 100);
                tt.setToY(player.getY() - 40);

                tt.setOnFinished(event -> {
                    root.getChildren().remove(text);
                });

                tt.play();
            });
        }
    }

    public static class XPGainAnimation {
        public XPGainAnimation(double xp) {
            Platform.runLater(() -> {
                Text text = new Text(String.format("%.2f%s", xp*100, "% XP"));
                text.setFill(Color.YELLOWGREEN);
                text.setFont(UIConst.FONT);
                root.getChildren().add(text);
                TranslateTransition tt = new TranslateTransition(Duration.seconds(1.5), text);

                tt.setFromX(player.getX() - 90);
                tt.setFromY(player.getY());
                tt.setToX(player.getX() - 90);
                tt.setToY(player.getY() - 25);

                tt.setOnFinished(event -> {
                    root.getChildren().remove(text);
                });

                tt.play();
            });
        }
    }
}
