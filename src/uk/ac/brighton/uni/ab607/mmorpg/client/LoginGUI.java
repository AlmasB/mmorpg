package uk.ac.brighton.uni.ab607.mmorpg.client;

import java.awt.Graphics2D;
import java.io.IOException;

import uk.ac.brighton.uni.ab607.libs.main.Out;
import uk.ac.brighton.uni.ab607.libs.net.DataPacket;
import uk.ac.brighton.uni.ab607.libs.net.DataPacketParser;
import uk.ac.brighton.uni.ab607.libs.net.UDPClient;
import uk.ac.brighton.uni.ab607.libs.ui.DoubleBufferWindow;

public class LoginGUI extends DoubleBufferWindow {

    /**
     * 
     */
    private static final long serialVersionUID = 3623204462300650040L;

    private UDPClient client;
    private LoginDataParser login = new LoginDataParser();

    private String ip = "", player = "";

    public LoginGUI() {
        super(400, 300, "Login screen", true);
        run();  // this is infinite loop, cancelled by player exiting or entering valid details
        // at this point we have working ip and server accepted player name
    }

    public void run() {
        showServerIPDialog();
        showPlayerNameDialog();
    }

    private void showServerIPDialog() {
        boolean serverFound = false;
        while (!serverFound) {
            ip = (String) this.showInputDialog("Enter server IP", "IP");
            if (ip == null)
                System.exit(0);

            try {
                client = new UDPClient(ip, 55555, login);
            }
            catch (IOException e) {
                Out.err("Couldn't connect to server at: " + ip);
                continue;
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
                else {
                    break;
                }
            } while (System.currentTimeMillis() - startTime < 5000);
        }
    }

    private void showPlayerNameDialog() {
        while (!login.playerAccepted) {
            player = (String) this.showInputDialog("Enter Player name", "player name");

            if (player == null)
                System.exit(0);

            if (player.isEmpty())
                continue;

            if (client != null) {   // just in case
                try {
                    client.send(new DataPacket("CHECK_PLAYER", player.getBytes()));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }

            long startTime = System.currentTimeMillis();

            do {
                delay(500);
            } while (!login.playerAccepted && System.currentTimeMillis() - startTime < 5000);
        }
    }

    private void delay(long millis) {
        try {
            Thread.sleep(millis);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getIP() {
        return ip;
    }

    public String getPlayerName() {
        return player;
    }

    @Override
    protected void createPicture(Graphics2D g) {
        // show login screen image if necessary
    }

    class LoginDataParser implements DataPacketParser {

        boolean playerAccepted = false;

        @Override
        public void parseClientPacket(DataPacket arg0) {}

        @Override
        public void parseServerPacket(DataPacket packet) {
            if (packet.stringData.equals("CHECK_PLAYER_GOOD")) {
                playerAccepted = true;
            }
        }
    }
}
