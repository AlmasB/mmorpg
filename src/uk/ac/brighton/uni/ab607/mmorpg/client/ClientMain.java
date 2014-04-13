package uk.ac.brighton.uni.ab607.mmorpg.client;

import uk.ac.brighton.uni.ab607.mmorpg.server.GameServer;

public class ClientMain {

    public static void main(String[] args) {

        //String ip = args[0];
        //String name = args[1];

        new GameServer();

        boolean local = true;

        if (!local) {
            //LoginGUI login = new LoginGUI();
            //login.setVisible(false);
            //new GUI(login.getIP(), login.getPlayerName());
            //new GUI(ip, name);
        }
        else {
            new GUI("127.0.0.1", "Almas");
        }
    }
}