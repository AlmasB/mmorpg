package uk.ac.brighton.uni.ab607.mmorpg.client.fx;

import java.io.IOException;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import uk.ac.brighton.uni.ab607.mmorpg.common.Attribute;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacter;
import uk.ac.brighton.uni.ab607.mmorpg.common.Player;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.ActionRequest;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.ActionRequest.Action;

import com.almasb.java.io.ResourceManager;

public class UIStatsWindow extends UIFragmentWindow {

    public UIStatsWindow(Player player) {
        StackPane stack = new StackPane();
        stack.setAlignment(Pos.TOP_LEFT);

        try {
            ImageView img = new ImageView(ResourceManager.loadFXImage("ui_stats_bg.png"));
            stack.getChildren().add(img);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        HBox hbox = new HBox(50);

        // ATTRS

        VBox attrBox = new VBox(10);
        attrBox.setRotate(1);
        attrBox.setPadding(new Insets(60, 0, 0, 100));

        for (int i = GameCharacter.STR; i <= GameCharacter.LUC; i++) {

            final int attrNum = i;

            HBox hLine = new HBox(10);
            hLine.setAlignment(Pos.CENTER_RIGHT);
            Text attr = new Text();
            attr.setFont(UIConst.FONT);
            attr.textProperty().bind(
                    new SimpleStringProperty(Attribute.values()[i].name() + ": " )
                    .concat(player.attributeProperties[i]).concat("+")
                    .concat(player.bonusAttributeProperties[i]));

            Button btn = new Button("+");
            btn.setOnAction(event -> {
                addActionRequest(new ActionRequest(Action.ATTR_UP, player.name, attrNum));
            });
            btn.visibleProperty().bind(player.attributePointsProperty.greaterThan(0)
                    .and(player.attributeProperties[i].lessThan(100)));

            hLine.getChildren().addAll(attr, btn);
            attrBox.getChildren().add(hLine);
        }


        // STATS

        VBox statBox = new VBox(10);
        statBox.setRotate(1);
        statBox.setPadding(new Insets(20, 0, 0, 40));

        Text statName = new Text(player.name);

        Text statClass = new Text();
        statClass.textProperty().bind(new SimpleStringProperty("Class: ").concat(player.classProperty));

        Text statLevel = new Text();
        statLevel.textProperty().bind(new SimpleStringProperty("Level: ").concat(player.baseLevelProperty)
                .concat("/").concat(player.jobLevelProperty)
                .concat("/").concat(player.statLevelProperty));

        Text statHPSP = new Text();
        statHPSP.textProperty().bind(new SimpleStringProperty("HP: ").concat(player.hpProperty).concat("/")
                .concat(player.statProperties[Player.MAX_HP].add(player.bonusStatProperties[Player.MAX_HP]))
                .concat(" SP: ").concat(player.spProperty).concat("/")
                .concat(player.statProperties[Player.MAX_SP].add(player.bonusStatProperties[Player.MAX_SP])));

        Text statATK = new Text();
        statATK.textProperty().bind(
                new SimpleStringProperty("ATK: ").concat(player.statProperties[Player.ATK].add(player.bonusStatProperties[Player.ATK]))
                .concat(" (").concat(player.statProperties[Player.ATK]).concat("+").concat(player.bonusStatProperties[Player.ATK]).concat(")"));

        Text statMATK = new Text();
        statMATK.textProperty().bind(
                new SimpleStringProperty("MATK: ").concat(player.statProperties[Player.MATK].add(player.bonusStatProperties[Player.MATK]))
                .concat(" (").concat(player.statProperties[Player.MATK]).concat("+").concat(player.bonusStatProperties[Player.MATK]).concat(")"));

        Text statDEF = new Text();
        statDEF.textProperty().bind(new SimpleStringProperty("DEF: ").concat(player.statProperties[Player.DEF])
                .concat("+").concat(player.bonusStatProperties[Player.DEF]));

        Text statMDEF = new Text();
        statMDEF.textProperty().bind(new SimpleStringProperty("MDEF: ").concat(player.statProperties[Player.MDEF])
                .concat("+").concat(player.bonusStatProperties[Player.MDEF]));

        Text statARM = new Text();
        statARM.textProperty().bind(new SimpleStringProperty("ARM: ").concat(player.statProperties[Player.ARM])
                .concat("+").concat(player.bonusStatProperties[Player.ARM]));

        Text statMARM = new Text();
        statMARM.textProperty().bind(new SimpleStringProperty("MARM: ").concat(player.statProperties[Player.MARM])
                .concat("+").concat(player.bonusStatProperties[Player.MARM]));

        Text statCrit = new Text();
        statCrit.textProperty().bind(new SimpleStringProperty("CRIT: ").concat(player.statProperties[Player.CRIT_CHANCE])
                .concat("+").concat(player.bonusStatProperties[Player.CRIT_CHANCE]).concat("%"));

        statBox.getChildren().addAll(statName, statClass, statLevel, statHPSP, statATK, statMATK, statDEF, statMDEF, statARM, statMARM, statCrit);
        statBox.getChildren().forEach(child -> ((Text)child).setFont(UIConst.FONT));

        hbox.getChildren().addAll(attrBox, statBox);
        stack.getChildren().add(hbox);
        root.getChildren().add(stack);

        this.setScene(new Scene(root, 770, 620, Color.TRANSPARENT));
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
}
