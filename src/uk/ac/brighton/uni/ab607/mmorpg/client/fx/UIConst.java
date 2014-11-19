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

    /*       int size = SocketConnection.calculatePacketSize(new DataPacket(player));

    int size2 = UDPConnection.toByteArray(player).length;

    Out.d("datapacket", size + "");
    Out.d("rawish", size2 + "");

    byte[] data = UDPConnection.toByteArray(new DataPacket(player));
    byte[] data2 = UDPConnection.toByteArray(player);

    Out.d("zip", new ZIPCompressor().compress(data).length + "");
    Out.d("zip2", new ZIPCompressor().compress(data2).length + "");

    Out.d("lzma", LZMACompressor.compress(data).length + "");
    Out.d("lzma2", LZMACompressor.compress(data2).length + "");*/

    //        HBox hbox = new HBox(10);
    //
    //        ProgressBar memoryUsageBar = new ProgressBar();
    //        memoryUsageBar.progressProperty().bind(RuntimeProperties.usedMemoryProperty().divide(RuntimeProperties.totalJVMMemoryProperty()));
    //        memoryUsageBar.progressProperty().addListener((obs, old, newValue) -> {
    //            int r = (int)(255*newValue.doubleValue());
    //            if (r > 255) r = 255;
    //            int g = (int)(255 - r);
    //            memoryUsageBar.setStyle(String.format("-fx-accent: rgb(%d, %d, 25)", r, g));
    //        });
    //
    //        Text memoryText = new Text();
    //        memoryText.textProperty().bind(RuntimeProperties.usedMemoryProperty().asString("%.0f")
    //                .concat(" / ").concat(RuntimeProperties.totalJVMMemoryProperty().asString("%.0f").concat(" MB")));
    //
    //        hbox.getChildren().addAll(new Text("Memory Usage: "), memoryUsageBar, memoryText);
    //        getChildren().add(hbox);
}
