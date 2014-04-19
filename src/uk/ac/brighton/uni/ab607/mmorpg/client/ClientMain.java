package uk.ac.brighton.uni.ab607.mmorpg.client;

import uk.ac.brighton.uni.ab607.mmorpg.common.object.ObjectManager;
import uk.ac.brighton.uni.ab607.mmorpg.server.GameServer;

public class ClientMain {

    // TODO: pass "local" as argument to start server and autologin on same machine
    // TODO: when done with 1.7, rewrite parts to use lambda expressions
    public static void main(String[] args) {
        boolean local = true;

        ObjectManager.load();

        if (local) {
            new GameServer();
            new GUI("127.0.0.1", "Almas");
        }
        else {
            LoginFXGUI.main(args);  // to avoid many issues with javafx use static calls
            // will only be called after previous gui finishes
            new GUI(LoginFXGUI.getIP(), LoginFXGUI.getUserName());
        }
    }
}