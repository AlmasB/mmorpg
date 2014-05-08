package uk.ac.brighton.uni.ab607.mmorpg.client;

import uk.ac.brighton.uni.ab607.mmorpg.client.ui.GameGUI;
import uk.ac.brighton.uni.ab607.mmorpg.client.ui.LoginFXGUI;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.ObjectManager;
import uk.ac.brighton.uni.ab607.mmorpg.server.GameServer;

public class ClientMain {
    public static void main(String[] args) {
        boolean local = true;

        ObjectManager.load();

        if (local) {
            new GameServer();
            new GameGUI("127.0.0.1", "Almas");
        }
        else {
            LoginFXGUI.main(args);  // to avoid many issues with javafx use static calls
            // will only be called after previous gui finishes
            new GameGUI(LoginFXGUI.getIP(), LoginFXGUI.getUserName());
        }
    }
}