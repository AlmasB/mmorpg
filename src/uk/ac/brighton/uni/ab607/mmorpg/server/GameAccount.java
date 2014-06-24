package uk.ac.brighton.uni.ab607.mmorpg.server;

import uk.ac.brighton.uni.ab607.libs.encryption.Account;
import uk.ac.brighton.uni.ab607.libs.main.Out;
import uk.ac.brighton.uni.ab607.libs.encryption.*;

/**
 * One client's account
 * 
 * Username is also the player's name in game
 * 
 * @author Almas Baimagambetov
 *
 */
public class GameAccount extends Account {
    /**
     *
     */
    private static final long serialVersionUID = 826478717098386944L;
    
    /**
     * name of the map where current account's player is last seen
     * and coordinates
     */
    private String mapName = "map1.txt";    // or .txt ?
    private int x = 1000, y = 600;

    /**
     * Hidden ctor
     * 
     * @param username
     * @param password
     * @param key
     */
    private GameAccount(String username, String password, String key) {
        super(username, password, key);
    }

    public static boolean addAccount(String username, String password, String email) {
        if (DBAccess.getAccounts().containsKey(username)) {
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

        DBAccess.getAccounts().put(username, new GameAccount(username, encryptedPass, passkey));
        return true;
    }

    public static GameAccount getAccountByUserName(String username) {
        return DBAccess.getAccounts().get(username);
    }

    public static boolean validateLogin(final String username, final String pass) {
        Account acc = getAccountByUserName(username);
        if (acc == null)
            return false;
        return PasswordManager.isValid(acc, pass);
    }
    
    public String getMapName() {
        return mapName;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setMapName(String map) {
        mapName = map;
    }
    
    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
