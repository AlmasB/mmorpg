package uk.ac.brighton.uni.ab607.mmorpg.client.fx;

import java.io.IOException;

import com.almasb.java.io.ResourceManager;

import javafx.scene.Parent;
import javafx.scene.image.ImageView;

public class Sprite extends Parent {

    public ImageView imageView;
    private boolean valid = true;

    public Sprite(String fileName) {
        try {
            imageView = new ImageView(ResourceManager.loadFXImage(fileName));



            //imageView.setClip(value);

            getChildren().add(imageView);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean b) {
        valid = b;
    }
}
