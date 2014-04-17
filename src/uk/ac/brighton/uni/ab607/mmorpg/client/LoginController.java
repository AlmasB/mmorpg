package uk.ac.brighton.uni.ab607.mmorpg.client;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import uk.ac.brighton.uni.ab607.libs.main.Out;
import uk.ac.brighton.uni.ab607.libs.net.DataPacket;
import uk.ac.brighton.uni.ab607.libs.net.DataPacketParser;
import uk.ac.brighton.uni.ab607.libs.net.UDPClient;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class LoginController extends AnchorPane implements Initializable {

    @FXML
    TextField userId;
    @FXML
    PasswordField password;
    @FXML
    TextField serverIP;
    @FXML
    Button login;
    @FXML
    Label errorMessage;
    @FXML
    ImageView img;

    private LoginFXGUI application;

    public void setApp(LoginFXGUI application){
        this.application = application;
    }

    private ProgressIndicator progress;

    public void setProgress(ProgressIndicator p) {
        progress = p;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FadeTransition ft = new FadeTransition(Duration.millis(3000), userId);
        ft.setFromValue(0.0);
        ft.setToValue(1);


        FadeTransition ft1 = new FadeTransition(Duration.millis(3000), password);
        ft1.setFromValue(0.0);
        ft1.setToValue(1);


        FadeTransition ft2 = new FadeTransition(Duration.millis(3000), serverIP);
        ft2.setFromValue(0.0);
        ft2.setToValue(1);

        FadeTransition ft3 = new FadeTransition(Duration.millis(800), img);
        ft3.setFromValue(0.4);
        ft3.setToValue(1);
        ft3.setCycleCount(Animation.INDEFINITE);
        ft3.setAutoReverse(true);

        ft.play();
        ft1.play();
        ft2.play();
        ft3.play();

        serverIP.setText("127.0.0.1");
    }

    private UDPClient client;
    private LoginDataParser loginParser = new LoginDataParser();
    private boolean serverFound = false;

    /**
     * Runs on UI thread
     *
     * Invoked when button is pressed
     *
     * @param event
     */
    public void processLogin(ActionEvent event) {
        //login.setDisable(true);
        enableButton(false);
        showProgressBar(true);

        new Thread(new LoginTask()).start();
    }

    class LoginTask implements Runnable {
        @Override
        public void run() {
            // test input here

            String ip = serverIP.getText();
            String user = userId.getText();
            String pass = password.getText();

            // input OK here
            if (!serverFound) {
                try {
                    client = new UDPClient(ip, 55555, loginParser);
                }
                catch (IOException e) {
                    setErrorMessage("Couldn't connect to server at: " + ip);
                    showProgressBar(false);
                    //login.setDisable(false);
                    enableButton(true);
                    return;
                }

                long startTime = System.currentTimeMillis();

                do {
                    if (client != null) {
                        if (client.isConnected()) {
                            serverFound = true;
                            break;
                        }
                        else {
                            delay(500);
                        }
                    }
                    else
                        break;
                } while (System.currentTimeMillis() - startTime < 2000);
            }

            if (!serverFound) {
                setErrorMessage("Couldn't connect to server at: " + ip);
                showProgressBar(false);
                //login.setDisable(false);
                enableButton(true);
                return;
            }

            // client is OK
            String data = user + "," + pass;
            DataPacket packet = new DataPacket("CHECK_PLAYER", data.getBytes());

            try {
                client.send(packet);
            }
            catch (IOException e) {
                Out.err(e);
            }

            long startTime = System.currentTimeMillis();

            do {
                if (loginParser.playerAccepted) {
                    //ClientMain.main(new String[] {getIP(), getUserName()});
                    LoginFXGUI.userData = new String[] {getIP(), getUserName()};
                    Platform.exit();
                }
                else {
                    delay(500);
                }

            } while (System.currentTimeMillis() - startTime < 2000);

            showProgressBar(false);
            //login.setDisable(false);
            enableButton(true);
        }
    }

    private void setErrorMessage(final String msg) {
        runOnUI(new Runnable() {
            @Override
            public void run() {
                errorMessage.setText(msg);
            }
        });
    }

    private void showProgressBar(final boolean b) {
        runOnUI(new Runnable() {
            @Override
            public void run() {
                progress.setVisible(b);
            }
        });
    }

    private void enableButton(final boolean b) {
        runOnUI(new Runnable() {
            @Override
            public void run() {
                login.setDisable(!b);
            }
        });
    }

    private void delay(long millis) {
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void runOnUI(Runnable r) {
        Platform.runLater(r);
    }

    class LoginDataParser implements DataPacketParser {

        boolean playerAccepted = false;

        @Override
        public void parseClientPacket(DataPacket arg0) {}

        @Override
        public void parseServerPacket(final DataPacket packet) {
            if (packet.stringData.equals("CHECK_PLAYER_GOOD")) {
                playerAccepted = true;
            }

            if (!packet.stringData.isEmpty()) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        errorMessage.setText(packet.stringData);
                    }
                });
            }
        }
    }

    public String getIP() {
        return serverIP.getText();
    }

    public String getUserName() {
        return userId.getText();
    }

    public String getPassword() {
        return password.getText();
    }
}

