package uk.ac.brighton.uni.ab607.mmorpg.client.fx;

import java.io.IOException;
import java.util.Optional;

import uk.ac.brighton.uni.ab607.mmorpg.common.Player;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.GameItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.UsableItem;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Armor;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Weapon;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.ActionRequest;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.ActionRequest.Action;

import com.almasb.java.io.ResourceManager;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.util.Duration;

public class UIInventoryWindow extends UIFragmentWindow {

    private Player player;

    public UIInventoryWindow(Player player) {

        this.player = player;

        try {

            ImageView imgLeft = new ImageView(ResourceManager.loadFXImage("inventory_left.png"));

            StackPane stack = new StackPane();





            ImageView imgRight = new ImageView(ResourceManager.loadFXImage("inventory_right.png"));




            //stack.getChildren().add(imgRight)


            HBox hbox = new HBox();

            hbox.getChildren().addAll(imgLeft, imgRight);
            root.getChildren().add(hbox);




            // ITEMS
            int index = 0;
            for (int i = 0; i < 6; i++) {

                HBox box = new HBox(5);
                box.setTranslateX(205);
                box.setTranslateY(i*40);

                for (int j = 0; j < 5; j++) {
                    ItemView itemView = new ItemView(index);

                    box.getChildren().add(itemView);

                    index++;
                }

                root.getChildren().add(box);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        this.setScene(new Scene(root, 700, 400, Color.TRANSPARENT));
    }

    @Override
    public void minimize() {
        ScaleTransition st = new ScaleTransition(Duration.seconds(0.66), root);
        st.setFromX(1);
        st.setToX(0);
        st.play();

        FadeTransition ft = new FadeTransition(Duration.seconds(1.5), root);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.setOnFinished(evt -> this.hide());
        ft.play();
    }

    @Override
    public void restore() {
        this.show();
        ScaleTransition st = new ScaleTransition(Duration.seconds(0.66), root);
        st.setFromX(0);
        st.setToX(1);
        st.play();

        FadeTransition ft = new FadeTransition(Duration.seconds(1.5), root);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private class ItemView extends Parent {

        private ImageView imageView = new ImageView(UIConst.Images.SS_ITEMS);

        public ItemView(int pos) {
            imageView.viewportProperty().bind(player.itemSpriteProperties.get(pos));

            //            imageView.setFitWidth(45);
            //            imageView.setFitHeight(45);

            DropShadow drop = new DropShadow(1, Color.GOLD);
            drop.setInput(new Glow());

            final Popup popup = new Popup();


            Rectangle rect = new Rectangle(250, 150);
            rect.setArcWidth(30);
            rect.setArcHeight(30);
            rect.setFill(Color.AQUA);

            VBox box = new VBox();
            box.setPadding(new Insets(5, 5, 5, 5));
            box.setAlignment(Pos.CENTER);

            Text fullText = new Text();
            fullText.setFont(fullText.getFont().font(18));
            fullText.setWrappingWidth(200);
            fullText.textProperty().bind(player.itemDescProperties[pos]);
            box.getChildren().add(fullText);

            popup.getContent().addAll(rect, box);

            imageView.setOnMouseEntered(event -> {
                imageView.setEffect(drop);
                popup.setX(getX() + 400);
                popup.setY(getY());
                popup.show(UIInventoryWindow.this);
            });

            imageView.setOnMouseExited(event -> {
                imageView.setEffect(null);
                popup.hide();
            });

            imageView.setOnMouseClicked(event -> {
                Optional<GameItem> item = player.getInventory().getItem(pos);
                item.ifPresent(it -> {
                    // if weapon or armor
                    if (it instanceof Weapon || it instanceof Armor) {
                        addActionRequest(new ActionRequest(Action.EQUIP, player.name, pos));
                    }
                    else if (it instanceof UsableItem) {
                        addActionRequest(new ActionRequest(Action.USE_ITEM, player.name, pos));
                    }
                });
            });


            //            VBox vbox = new VBox();
            //            vbox.getChildren().addAll(imageView);

            getChildren().add(imageView);
        }
    }
}
