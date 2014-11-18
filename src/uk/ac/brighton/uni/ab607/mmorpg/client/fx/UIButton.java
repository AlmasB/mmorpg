package uk.ac.brighton.uni.ab607.mmorpg.client.fx;

import java.io.IOException;
import java.io.InputStream;

import uk.ac.brighton.uni.ab607.mmorpg.common.Sys;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import com.almasb.java.io.ResourceManager;
import com.almasb.java.ui.FXCustomButton;

public class UIButton extends FXCustomButton {

    private static Font font;

    static {
        try {
            InputStream is = ResourceManager.loadResourceAsStream("Vecna.otf").orElseThrow(IOException::new);
            font = Font.loadFont(is, 28);
            is.close();
        }
        catch (IOException e) {
            Sys.logExceptionAndExit(e);
        }
    }

    public UIButton(String buttonText) {
        super(buttonText, Color.WHITESMOKE, font, "ui_menu_button2.png", "ui_menu_button.png", "ui_menu_button3.png");
    }
}
