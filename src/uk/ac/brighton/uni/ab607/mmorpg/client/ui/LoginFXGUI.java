package uk.ac.brighton.uni.ab607.mmorpg.client.ui;

import java.io.InputStream;

import uk.ac.brighton.uni.ab607.libs.io.ResourceManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginFXGUI extends Application {

    private LoginController login;

    private static String IP = "", userName = "";

    /**
     * LoginFXGUI.main(args); for calling login gui
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    public LoginFXGUI() {}

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
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setResizable(false);
        primaryStage.setTitle("Login GUI");

        InputStream in = ResourceManager.loadResourceAsStream("UI/Login.fxml");
        FXMLLoader loader = new FXMLLoader();
        loader.setBuilderFactory(new JavaFXBuilderFactory());

        loader.setLocation(ResourceManager.isRunningFromJar()
                ? getClass().getResource("/res/UI/Login.fxml")
                : ResourceManager.getLocalURL("UI/Login.fxml"));

        AnchorPane page;
        try {
            page = (AnchorPane) loader.load(in);
        }
        finally {
            in.close();
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
}
