package uk.ac.brighton.uni.ab607.mmorpg.server;

import uk.ac.brighton.uni.ab607.mmorpg.common.object.ObjectManager;

public class ServerMain {
    public static void main(String[] args) {
        ObjectManager.load();
        new GameServer();
    }
}