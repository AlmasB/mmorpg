package uk.ac.brighton.uni.ab607.mmorpg.server;

import java.io.IOException;
import java.util.TreeMap;

import uk.ac.brighton.uni.ab607.libs.encryption.Account;
import uk.ac.brighton.uni.ab607.libs.io.ResourceManager;
import uk.ac.brighton.uni.ab607.libs.main.Out;
import uk.ac.brighton.uni.ab607.libs.encryption.*;

public class GameAccount extends Account {

    /**
     *
     */
    private static final long serialVersionUID = 826478717098386944L;

    private static final String DB_FOLDER = "db/";
    private static final String DB_FILE = DB_FOLDER + "accounts.db";


    private static TreeMap<String, GameAccount> accounts;
    //private static int uniqueID;

    static {
        accounts = loadDB();
        //uniqueID = Integer.parseInt(accounts.lastKey());
    }

    private GameAccount(String username, String password, String key) {
        super(username, password, key);
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

    public static boolean addAccount(String username, String password, String email) {
        if (accounts.containsKey(username)) {
            Out.err("Can't create account - username exists");
            return false;
        }

        String passkey = Encryptor.generateKey(password.length());
        String encryptedPass = "";
        try {
            encryptedPass = Encryptor.encrypt(password, passkey);
        }
        catch (IllegalArgumentException e) {
            Out.err("Can't create account");
            Out.err(e);
            return false;
        }


        //accounts.put(++uniqueID + "", new GameAccount(username, encryptedPass, passkey));
        accounts.put(username, new GameAccount(username, encryptedPass, passkey));
        return true;
    }

    public static GameAccount getAccountByUserName(String username) {
        return accounts.get(username);
    }

    public static boolean validateLogin(final String username, final String pass) {
        Account acc = getAccountByUserName(username);
        if (acc == null)
            return false;
        return PasswordManager.isValid(acc, pass);
    }
}
