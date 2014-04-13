package uk.ac.brighton.uni.ab607.mmorpg.client;

import java.io.InputStream;

import uk.ac.brighton.uni.ab607.libs.io.ResourceManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginFXGUI extends Application {

    private LoginController login;

    public static String[] userData;

    /**
     * LoginFXGUI.main(args); for calling login gui
     * 
     * @param args
     */
    public static void main(String[] args) {
        //new LoginFXGUI();
        launch(args);
    }

    public LoginFXGUI() {
        //Application.launch(LoginFXGUI.class, (String[]) null);
        // at this point we have working ip and server accepted player name
    }

    public void callFXApp() {
        Application.launch(LoginFXGUI.class, (String[]) null);
    }

    public String getUser() {
        return login.getUserName();
    }

    public String getIP() {
        return login.getIP();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setResizable(false);
        primaryStage.setTitle("Login GUI");

        //InputStream in = getClass().getResourceAsStream("/res/UI/Login.fxml");

        InputStream in = ResourceManager.loadResourceAsStream("UI/Login.fxml");
        FXMLLoader loader = new FXMLLoader();
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        //loader.setLocation(getClass().getResource("/res/UI/Login.fxml"));
        loader.setLocation(ResourceManager.getLocalURL("UI/Login.fxml"));

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

        //InputStream in = Main.class.getResourceAsStream(fxml);
        //InputStream in = ResourceManager.loadResourceAsStream(fxml);
        //loader.setLocation(ResourceManager.getLocalURL(fxml));

        primaryStage.show();
    }
}
