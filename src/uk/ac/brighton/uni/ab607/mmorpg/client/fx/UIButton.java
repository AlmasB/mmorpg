package uk.ac.brighton.uni.ab607.mmorpg.client.fx;

import javafx.scene.paint.Color;

import com.almasb.java.ui.FXCustomButton;

public class UIButton extends FXCustomButton {

    public UIButton(String buttonText) {
        super(buttonText, Color.WHITESMOKE, UIConst.FONT, "ui_menu_button2.png", "ui_menu_button.png", "ui_menu_button3.png");
    }
}
