package uk.ac.brighton.uni.ab607.mmorpg.client.fx;

import java.io.IOException;
import java.io.InputStream;

import uk.ac.brighton.uni.ab607.mmorpg.common.Sys;

import com.almasb.java.io.ResourceManager;

import javafx.scene.text.Font;

public final class UIConst {

    public static final int W = 1280;
    public static final int H = 720;

    public static Font FONT = null;

    // TODO: pre-load all resource and exit if not found
    static {
        try {
            InputStream is = ResourceManager.loadResourceAsStream("Vecna.otf").orElseThrow(IOException::new);
            FONT = Font.loadFont(is, 28);
            is.close();
        }
        catch (IOException e) {
            Sys.logExceptionAndExit(e);
        }
    }
}
