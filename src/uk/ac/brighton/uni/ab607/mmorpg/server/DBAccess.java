package uk.ac.brighton.uni.ab607.mmorpg.server;

import java.io.IOException;
import java.util.TreeMap;

import com.almasb.common.util.Out;
import com.almasb.java.io.ResourceManager;

/**
 * Handles all DB operations
 *
 * @author Almas Baimagambetov (ab607@uni.brighton.ac.uk)
 * @version 1.0
 *
 */
public class DBAccess {

    private static final String RES_FOLDER = "res/";
    private static final String DB_FOLDER = "db/";
    private static final String DB_FILE = DB_FOLDER + "accounts.db";

    private static TreeMap<String, GameAccount> accounts;

    static {
        accounts = loadDB();
    }

    public static void saveDB() {
        try {
            ResourceManager.writeJavaObject(RES_FOLDER + DB_FILE, accounts);
        }
        catch (IOException e) {
            Out.i("Failed to save DB");
            Out.e(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static TreeMap<String, GameAccount> loadDB() {
        TreeMap<String, GameAccount> map = null;

        try {
            map = (TreeMap<String, GameAccount>) ResourceManager.loadJavaObject(RES_FOLDER + DB_FILE);
        }
        catch (IOException | ClassNotFoundException e) {
            Out.e(e);
        }

        if (map == null) {
            map = new TreeMap<String, GameAccount>();
            Out.i("Using new DB");
        }

        return map;
    }

    public static TreeMap<String, GameAccount> getAccounts() {
        return accounts;
    }
}
