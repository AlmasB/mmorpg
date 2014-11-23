package uk.ac.brighton.uni.ab607.mmorpg.client.fx;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import uk.ac.brighton.uni.ab607.mmorpg.client.fx.UIAnimations.*;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacterClass;
import uk.ac.brighton.uni.ab607.mmorpg.common.Player;
import uk.ac.brighton.uni.ab607.mmorpg.common.Sys;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Skill;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.ActionRequest;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.ActionRequest.Action;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.QueryRequest;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.QueryRequest.Query;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.ServerResponse;

import com.almasb.common.net.DataPacket;
import com.almasb.common.net.ServerPacketParser;
import com.almasb.common.net.UDPClient;
import com.almasb.common.util.ByteStream;
import com.almasb.common.util.Out;
import com.almasb.java.ui.FXWindow;

public class GameWindow extends FXWindow {

    private String name, ip;
    private UDPClient client = null;

    /**
     * gameRoot - contains game related nodes
     * uiRoot - the static node group which overlays the game
     */
    private Group gameRoot = new Group(), uiRoot = new Group();

    private Player player;
    // currently also uses enemy sprites, maybe store in 1 group
    private Group playerSprites = new Group();
    private ArrayList<Player> playersList = new ArrayList<Player>();

    private int selX = 1000, selY = 600;
    private boolean selectingTarget = false;
    private int skillIndex = 0;

    private UIStatsWindow statsWindow;
    private UIInventoryWindow inventoryWindow;
    private UIMenuWindow menuWindow;

    public GameWindow(String ip, String playerName) {
        this.ip = ip;
        player = new Player(playerName, GameCharacterClass.NOVICE, 0, 0, "", 0);
        name = playerName;

        UIAnimations.init(gameRoot, player);
    }

    /**
     * Game layer
     */
    @Override
    protected void createContent(Pane root) {
        ImageView background = new ImageView(UIConst.Images.SS_MAP);
        gameRoot.getChildren().add(background);

        // add animation listeners
        player.baseLevelProperty.addListener((obs, old, newValue) -> {
            if (newValue.intValue() > old.intValue() && old.intValue() != 0)
                new LevelUpAnimation(gameRoot, player);
        });

        player.moneyProperty.addListener((obs, old, newValue) -> {
            if (old.intValue() != -1 && newValue.intValue() - old.intValue() > 0)
                new MoneyGainAnimation(newValue.intValue() - old.intValue());
        });

        player.baseXPProperty.addListener((obs, old, newValue) -> {
            if (newValue.doubleValue() != 0)
                new XPGainAnimation(newValue.doubleValue() - old.doubleValue());
        });

        playersList.add(player);
        playerSprites.getChildren().add(player.sprite);

        gameRoot.getChildren().addAll(playerSprites);

        // create peripheral windows
        menuWindow = new UIMenuWindow();
        statsWindow = new UIStatsWindow(player);
        inventoryWindow = new UIInventoryWindow(player);

        createUI();

        root.getChildren().addAll(gameRoot, uiRoot);
    }

    /**
     * UI layer
     */
    private void createUI() {
        ImageView hotbarImage = new ImageView(UIConst.Images.UI_HOTBAR);
        hotbarImage.setTranslateX(300);
        hotbarImage.setTranslateY(580);
        uiRoot.getChildren().add(hotbarImage);

        for (int i = 0; i < 9; i++) {
            SkillView skill = new SkillView(i);
            skill.setTranslateX(338 + i * 60);
            skill.setTranslateY(600);
            uiRoot.getChildren().add(skill);
        }

        ProgressBar xpBar = new ProgressBar(0);
        xpBar.setPrefWidth(600);
        xpBar.setTranslateX(350);
        xpBar.setStyle("-fx-accent: rgb(255, 215, 0)");
        xpBar.progressProperty().bind(player.baseXPProperty);

        uiRoot.getChildren().add(xpBar);

        ProgressBar hpBar = new ProgressBar(0);
        hpBar.setTranslateX(240);
        hpBar.setTranslateY(635);
        hpBar.setRotate(-90);
        hpBar.progressProperty().bind(player.hpProperty.divide(
                player.statProperties[Player.MAX_HP].add(player.bonusStatProperties[Player.MAX_HP]).multiply(1.0f)));
        hpBar.progressProperty().addListener((obs, old, newValue) -> {
            int r = 255 - (int) (147*newValue.doubleValue());
            int g = (int)(200 * newValue.doubleValue());
            int b = 10;
            hpBar.setStyle(String.format("-fx-accent: rgb(%d, %d, %d)", r, g, b));
        });

        uiRoot.getChildren().add(hpBar);

        ProgressBar spBar = new ProgressBar(0);
        spBar.setTranslateX(850);
        spBar.setTranslateY(635);
        spBar.setRotate(-90);
        spBar.progressProperty().bind(player.spProperty.divide(
                player.statProperties[Player.MAX_SP].add(player.bonusStatProperties[Player.MAX_SP]).multiply(1.0f)));
        spBar.progressProperty().addListener((obs, old, newValue) -> {
            int r = 173 - (int)(42*newValue.doubleValue());
            int g = 223 - (int)(154*newValue.doubleValue());
            int b = 255;
            spBar.setStyle(String.format("-fx-accent: rgb(%d, %d, %d)", r, g, b));
        });

        uiRoot.getChildren().add(spBar);

        Button btnMenu = new Button("Menu");
        btnMenu.setTranslateX(950);
        btnMenu.setTranslateY(640);
        btnMenu.setFont(UIConst.FONT);
        btnMenu.setOnAction(event -> {
            if (menuWindow.isShowing())
                menuWindow.minimize();
            else
                menuWindow.restore();
        });

        uiRoot.getChildren().add(btnMenu);

        Button btnStats = new Button("Stats");
        btnStats.setTranslateX(1070);
        btnStats.setTranslateY(640);
        btnStats.setFont(UIConst.FONT);
        btnStats.setOnAction(event -> {
            if (statsWindow.isShowing())
                statsWindow.minimize();
            else
                statsWindow.restore();
        });

        uiRoot.getChildren().add(btnStats);


        Button btnInventory = new Button("Inventory");
        btnInventory.setTranslateX(1180);
        btnInventory.setTranslateY(640);
        btnInventory.setFont(UIConst.FONT);
        btnInventory.setOnAction(event -> {
            if (inventoryWindow.isShowing())
                inventoryWindow.minimize();
            else
                inventoryWindow.restore();
        });

        uiRoot.getChildren().add(btnInventory);
    }

    @Override
    protected void initScene(Scene scene) {
        gameRoot.layoutXProperty().bind(player.xProperty.subtract(640).negate());
        gameRoot.layoutYProperty().bind(player.yProperty.subtract(360).negate());

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT) {

            }
            if (event.getCode() == KeyCode.LEFT) {

            }
            if (event.getCode() == KeyCode.UP) {

            }
            if (event.getCode() == KeyCode.DOWN) {

            }
            if (event.getCode() == KeyCode.I) {
                if (inventoryWindow.isShowing())
                    inventoryWindow.minimize();
                else
                    inventoryWindow.restore();
            }
            if (event.getCode() == KeyCode.S) {
                if (statsWindow.isShowing())
                    statsWindow.minimize();
                else
                    statsWindow.restore();
            }
            if (event.getCode() == KeyCode.ESCAPE) {
                if (menuWindow.isShowing())
                    menuWindow.minimize();
                else
                    menuWindow.restore();
            }

            if (event.getCode().isDigitKey()) {
                int digit = 0;
                try {
                    digit = Integer.parseInt(
                            event.getCode().toString().charAt(event.getCode().toString().length()-1) + "");
                }
                catch (NumberFormatException e) {
                }
                if (digit != 0) {
                    skillIndex = digit - 1;
                    if (skillIndex < player.getSkills().length) {
                        Skill skill = player.getSkills()[skillIndex];
                        if (skill != null && skill.active && skill.getLevel() > 0 && skill.getCurrentCooldown() == 0 &&
                                player.getSP() >= skill.getManaCost()) {
                            if (skill.isSelfTarget()) {
                                addActionRequest(new ActionRequest(Action.SKILL_USE, player.name, "map1.txt", skillIndex, 0));
                                UIConst.Audio.getSkillAudioByID(skill.id).play();
                            }
                            else {
                                // choose target
                                selectingTarget = true;
                            }
                        }
                    }
                }
            }
        });

        scene.setOnMouseClicked(event -> {
            if (!selectingTarget) {
                selX = (int)(event.getX() - gameRoot.getLayoutX()) / 40 * 40;
                selY = (int)(event.getY() - gameRoot.getLayoutY()) / 40 * 40;
            }
            else {
                addActionRequest(new ActionRequest(Action.SKILL_USE, player.name, "map1.txt,"
                        + (int)(event.getX() - gameRoot.getLayoutX()) / 40 * 40
                        + "," + (int)(event.getY() - gameRoot.getLayoutY()) / 40 * 40, skillIndex, 0));
                selectingTarget = false;
                UIConst.Audio.getSkillAudioByID(player.getSkills()[skillIndex].id).play();
            }
        });
    }

    @Override
    protected void initStage(Stage primaryStage) {
        primaryStage.setWidth(1280);
        primaryStage.setHeight(720);
        primaryStage.setTitle("Orion MMORPG");
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(event -> {
            // TODO: client.send(new DataPacket(new QueryRequest(Query.LOGOFF, name)));
            // TODO: addActionRequest(new ActionRequest(Action.CHAT, player.name, map.name + "," + chatText));
            System.exit(0);
        });
        primaryStage.show();

        try {
            client = new UDPClient(ip, 55555, new ServerResponseParser());
            client.send(new DataPacket(new QueryRequest(Query.LOGIN, name)));

            //Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::showTraffic, 0, 10, TimeUnit.SECONDS);
        }
        catch (IOException e) {
            Sys.logExceptionAndExit(e);
        }
    }

    private class SkillView extends Parent {

        private ImageView imageView = new ImageView();
        private Text text = new Text();

        public SkillView(int pos) {
            text.setFont(text.getFont().font(16));
            text.setFill(Color.DARKBLUE);

            text.textProperty().bind(new SimpleStringProperty("Lv ").concat(player.skillLevelProperties[pos]));
            imageView.imageProperty().bind(player.skillImageProperties.get(pos));
            imageView.visibleProperty().bind(player.skillReadyProperties[pos]);

            imageView.setFitWidth(45);
            imageView.setFitHeight(45);

            DropShadow drop = new DropShadow(14, Color.GOLD);
            drop.setInput(new Glow());

            final Popup popup = new Popup();


            Rectangle rect = new Rectangle(250, 150);
            rect.setArcWidth(30);
            rect.setArcHeight(30);
            rect.setFill(Color.AQUA);

            VBox box = new VBox();
            box.setPadding(new Insets(5, 5, 5, 5));
            box.setAlignment(Pos.CENTER);

            Text fullText = new Text();
            fullText.setFont(fullText.getFont().font(18));
            fullText.setWrappingWidth(200);
            fullText.textProperty().bind(player.skillDescProperties[pos]);
            box.getChildren().add(fullText);

            popup.getContent().addAll(rect, box);

            imageView.setOnMouseEntered(event -> {
                imageView.setEffect(drop);
                popup.setX(getTranslateX() + 200);
                popup.setY(getTranslateY() - 50);
                popup.show(getStage());
            });

            imageView.setOnMouseExited(event -> {
                imageView.setEffect(null);
                popup.hide();
            });

            Button btn = new Button("+");
            btn.visibleProperty().bind(player.skillPointsProperty.greaterThan(0)
                    .and(player.skillLevelProperties[pos].lessThan(Skill.MAX_LEVEL)));
            btn.setOnAction(event -> {
                addActionRequest(new ActionRequest(Action.SKILL_UP, player.name, pos));
            });

            VBox vbox = new VBox();
            vbox.getChildren().addAll(btn, imageView, text);

            getChildren().add(vbox);
        }
    }

    private void showTraffic() {
        // 10 seconds
        float kbs = client.resetAC() / 10240.0f;
        Out.d("showTraffic", kbs + " KB/s (IN). Required bandwidth: " + kbs * 10 + " kbit/s");

        kbs = client.resetACSent() / 10240.0f;
        Out.d("showTraffic", kbs + " KB/s (OUT). Required bandwidth: " + kbs * 10 + " kbit/s");
    }

    // TODO: add a single message sending from all UIs
    private ArrayList<ActionRequest> requests = new ArrayList<ActionRequest>();

    public void addActionRequest(ActionRequest action) {
        requests.add(action);
    }

    public ActionRequest[] clearPendingActionRequests() {
        ActionRequest[] res = new ActionRequest[requests.size()];
        requests.toArray(res);
        requests.clear();
        return res;
    }

    /**
     * Parses and updates the game client, including all windows
     * with the information taken from server packets
     *
     * @author Almas Baimagambetov (ab607@uni.brighton.ac.uk)
     * @version 1.0
     *
     */
    private class ServerResponseParser extends ServerPacketParser {
        private boolean clientReady = false;

        @Override
        public void parseServerPacket(DataPacket packet) {
            if (clientReady && packet.byteData != null && packet.byteData.length > 0) {

                // PLAYER, ENEMY
                if (packet.byteData[0] == -127) {
                    for (Player p : playersList)
                        p.sprite.setValid(false);


                    // raw data of players containing drawing data
                    ByteArrayInputStream in = new ByteArrayInputStream(packet.byteData);

                    // number of players
                    int size = packet.byteData.length / 36;
                    for (int i = 0; i < size; i++) {
                        byte[] data = new byte[16];
                        byte[] name = new byte[16];
                        byte[] id = new byte[4];


                        try {
                            in.read(data);
                            in.read(name);
                            in.read(id);

                            int runtimeID = ByteStream.byteArrayToInt(id, 0);

                            String playerName = new String(name).replace(new String(new byte[] {0}), "");

                            //Out.d("runtimeID", runtimeID + " " + playerName);

                            // search list of players for name
                            // if found update their data
                            // else create new player with data

                            boolean newPlayer = true;

                            for (Player p : playersList) {
                                if (p.name.equals(playerName) && p.getRuntimeID() == runtimeID) {
                                    newPlayer = false;
                                    p.loadFromByteArray(data);
                                    p.sprite.setValid(true);
                                    //Out.d(playerName, "true");
                                    break;
                                }
                            }

                            if (newPlayer) {
                                Player p = new Player(playerName, GameCharacterClass.NOVICE, 0, 0, "", 0);
                                p.loadFromByteArray(data);
                                p.setRuntimeID(runtimeID);
                                playersList.add(p);
                                Platform.runLater(() -> playerSprites.getChildren().add(p.sprite));
                            }
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    // END FOR


                    Platform.runLater(() -> {
                        playerSprites.getChildren().removeIf(node -> {
                            Sprite s = (Sprite)node;
                            return !s.isValid();
                        });
                    });

                    playersList.removeIf(player -> !player.sprite.isValid());

                    updateGameClient();
                }

                // ANIMATION
                if (packet.byteData[0] == -126) {

                    ByteBuffer buf = ByteBuffer.wrap(packet.byteData);

                    ByteArrayInputStream in = new ByteArrayInputStream(packet.byteData);

                    // number of players
                    int size = packet.byteData.length / 13;
                    for (int i = 0; i < size; i++) {

                        // skip first byte
                        buf.get();

                        int x = buf.getInt();
                        int y = buf.getInt();
                        int dmg = buf.getInt();
                        //                        int x = ByteStream.byteArrayToInt(packet.byteData, 1);
                        //                        int y = ByteStream.byteArrayToInt(packet.byteData, 5);
                        //
                        //                        int dmg = ByteStream.byteArrayToInt(packet.byteData, 9);

                        new BasicDamageAnimation(dmg, x, y);
                    }
                }
            }

            if (packet.objectData instanceof ServerResponse) {
                ServerResponse res = (ServerResponse) packet.objectData;

                //map = ObjectManager.getMapByName(res.data);

                selX = res.value1;
                selY = res.value2;
            }

            if (packet.objectData instanceof Player) {
                Player p = (Player) packet.objectData;

                // update client's player
                player.update(p);

                clientReady = true;
            }
        }

        private void updateGameClient() {
            if (player.getX() != selX || player.getY() != selY)
                addActionRequest(new ActionRequest(Action.MOVE, name, "map1.txt", selX, selY));

            try {
                ActionRequest[] thisGUI = clearPendingActionRequests();
                ActionRequest[] statsGUI = statsWindow.clearPendingActionRequests();
                ActionRequest[] invGUI = inventoryWindow.clearPendingActionRequests();

                if (thisGUI.length > 0)
                    client.send(new DataPacket(thisGUI));
                if (statsGUI.length > 0)
                    client.send(new DataPacket(statsGUI));
                if (invGUI.length > 0)
                    client.send(new DataPacket(invGUI));
            }
            catch (IOException e) {
                Out.e("updateGameClient", "Failed to send a packet", this, e);
            }
        }
    }
}
