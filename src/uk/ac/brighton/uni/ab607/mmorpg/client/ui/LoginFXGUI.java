package uk.ac.brighton.uni.ab607.mmorpg.client.ui;

import java.io.IOException;
import java.io.InputStream;

import com.almasb.java.io.ResourceManager;
import com.almasb.java.ui.FXWindow;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class LoginFXGUI extends FXWindow {

    private LoginController login;

    private static String IP = "", userName = "";

    public static void setUserName(String name) {
        userName = name;
    }

    public static void setIP(String ip) {
        IP = ip;
    }

    public static String getUserName() {
        return userName;
    }

    public static String getIP() {
        return IP;
    }

    @Override
    public void initStage(Stage primaryStage) {
        primaryStage.setResizable(false);
        primaryStage.setTitle("Login GUI");

        InputStream in = ResourceManager.loadResourceAsStream("UI/Login.fxml").get();
        FXMLLoader loader = new FXMLLoader();
        loader.setBuilderFactory(new JavaFXBuilderFactory());

        loader.setLocation(ResourceManager.isRunningFromJar()
                ? getClass().getResource("/res/UI/Login.fxml")
                        : ResourceManager.getLocalURL("UI/Login.fxml"));

        AnchorPane page = null;
        try {
            try {
                page = (AnchorPane) loader.load(in);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        finally {
            try {
                in.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        ProgressIndicator p = new ProgressIndicator();
        p.setLayoutX(140);
        p.setLayoutY(150);
        p.setVisible(false);

        page.getChildren().add(p);
        Scene scene = new Scene(page, 300, 330);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();

        login = (LoginController) loader.getController();
        login.setApp(this);
        login.setProgress(p);

        primaryStage.show();
    }

    @Override
    protected void createContent(Pane root) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void initScene(Scene scene) {
        // TODO Auto-generated method stub

    }
}
