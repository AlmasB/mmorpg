package uk.ac.brighton.uni.ab607.mmorpg.client;

import com.almasb.java.io.Resources;
import com.almasb.common.util.Out;

import uk.ac.brighton.uni.ab607.mmorpg.R;
import uk.ac.brighton.uni.ab607.mmorpg.client.ui.GameGUI;
import uk.ac.brighton.uni.ab607.mmorpg.client.ui.LoginFXGUI;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.ObjectManager;
import uk.ac.brighton.uni.ab607.mmorpg.server.GameServer;

public class ClientMain {
    private static void usage() {
        Out.println("Usage:");
        Out.println("java -jar orionX.jar -option");
        Out.println("where X is the version");
        Out.println("Options:\t Description:");
        Out.println("-? -help -usage\t prints this info message");
        Out.println("-local \t\t starts the server and locally connects to it (Debug)");
        Out.println("-server\t\t starts headless server");
        Out.println("-client\t\t starts the client");
        Out.println("-version\t prints game version");
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            usage();
            return;
        }

        boolean local = false, server = false;

        switch (args[0]) {
            case "-version":
                Out.println("Version: " + Resources.getText("system/version.txt").get(0));
                return;
            case "-local":
                local = true;   // fallthru
            case "-server":     // fallthru
                server = true;
            case "-client":
                break;

            case "-?": case "-help": case "-usage": // fallthru
            default:
                usage();
                return;
        }

        // load game resources
        Resources.init(R.drawable.class, R.raw.class);

        // load game data
        ObjectManager.load();

        try {
            if (server || local)
                new GameServer();

            if (local) {
                new GameGUI("127.0.0.1", "Debug");
            }
            else {
                LoginFXGUI.main(args);  // to avoid many issues with javafx use static calls
                // will only be called after previous gui finishes
                new GameGUI(LoginFXGUI.getIP(), LoginFXGUI.getUserName());
            }
        }
        catch (Exception e) {
            Out.e(e);
            Out.println("Application will now close");
        }
    }
}