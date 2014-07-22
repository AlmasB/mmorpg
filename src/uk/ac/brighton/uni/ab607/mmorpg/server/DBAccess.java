package uk.ac.brighton.uni.ab607.mmorpg.server;

import java.io.IOException;
import java.util.TreeMap;

import com.almasb.java.io.ResourceManager;
import com.almasb.java.main.Out;

/**
 * Handles all DB operations
 *
 * @author Almas Baimagambetov (ab607@uni.brighton.ac.uk)
 * @version 1.0
 *
 */
public class DBAccess {

    private static final String DB_FOLDER = "db/";
    private static final String DB_FILE = DB_FOLDER + "accounts.db";

    private static TreeMap<String, GameAccount> accounts;

    static {
        accounts = loadDB();
    }

    public static void saveDB() {
        try {
            ResourceManager.writeJavaObject(DB_FILE, accounts);
        }
        catch (IOException e) {
            Out.err("Failed to save DB");
            Out.err(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static TreeMap<String, GameAccount> loadDB() {
        TreeMap<String, GameAccount> map = null;

        try {
            map = (TreeMap<String, GameAccount>) ResourceManager.loadJavaObject(DB_FILE);
        }
        catch (IOException | ClassNotFoundException e) {
            Out.err(e);
        }

        if (map == null) {
            map = new TreeMap<String, GameAccount>();
            Out.println("Using new DB");
        }

        return map;
    }

    public static TreeMap<String, GameAccount> getAccounts() {
        return accounts;
    }
}
