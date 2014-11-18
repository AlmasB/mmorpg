package uk.ac.brighton.uni.ab607.mmorpg.client.fx;

import java.io.IOException;

import com.almasb.java.io.ResourceManager;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class UIMenuWindow extends UIFragmentWindow {

    public UIMenuWindow() {
        VBox menuBox = new VBox(15);
        menuBox.setAlignment(Pos.CENTER);

        UIButton btnResume = new UIButton("Resume");
        btnResume.setOnMouseClicked(event -> {
            minimize();
        });
        UIButton btnOptions = new UIButton("Options");
        UIButton btnExit = new UIButton("Exit");
        btnExit.setOnMouseClicked(event -> {
            System.exit(0);
        });

        StackPane stack = new StackPane();

        try {
            ImageView bg = new ImageView(ResourceManager.loadFXImage("ui_menu_bg3.png"));
            stack.getChildren().add(bg);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        menuBox.getChildren().addAll(btnResume, btnOptions, btnExit);
        stack.getChildren().add(menuBox);

        root.getChildren().addAll(stack);

        this.setScene(new Scene(root, 450, 375, Color.TRANSPARENT));
    }

    @Override
    public void minimize() {
        FadeTransition ft = new FadeTransition(Duration.seconds(1.5), root);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.setOnFinished(evt -> this.hide());
        ft.play();


        ScaleTransition st = new ScaleTransition(Duration.seconds(1), root);
        st.setFromY(1);
        st.setToY(0);
        st.play();
    }

    @Override
    public void restore() {
        this.show();
        ScaleTransition st = new ScaleTransition(Duration.seconds(0.66), root);
        st.setFromY(0);
        st.setToY(1);
        st.play();

        FadeTransition ft = new FadeTransition(Duration.seconds(1.5), root);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }
}
