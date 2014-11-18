package uk.ac.brighton.uni.ab607.mmorpg.client.fx;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;






















import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import uk.ac.brighton.uni.ab607.mmorpg.client.ui.InventoryGUI;
import uk.ac.brighton.uni.ab607.mmorpg.client.ui.StatsGUI;
import uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation.Animation;
import uk.ac.brighton.uni.ab607.mmorpg.common.Attribute;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacter;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacterClass;
import uk.ac.brighton.uni.ab607.mmorpg.common.Player;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.Chest;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Enemy;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.ObjectManager;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.ActionRequest;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.QueryRequest;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.ServerResponse;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.ActionRequest.Action;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.QueryRequest.Query;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.ParallelCamera;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import com.almasb.common.compression.LZMACompressor;
import com.almasb.common.net.DataPacket;
import com.almasb.common.net.ServerPacketParser;
import com.almasb.common.net.SocketConnection;
import com.almasb.common.net.UDPClient;
import com.almasb.common.net.UDPConnection;
import com.almasb.common.util.Out;
import com.almasb.common.util.ZIPCompressor;
import com.almasb.java.io.ResourceManager;
import com.almasb.java.ui.FXWindow;
import com.almasb.java.util.RuntimeProperties;

public class GameWindow extends FXWindow {

    private String name;
    private UDPClient client = null;

    private Scene scene;
    private SubScene uiScene;
    private Camera camera = new ParallelCamera();

    private Group uiRoot;

    private Stage menu, stats;

    private Font font;

    private Player player;

    private Group players = new Group();

    private String ip;

    private ArrayList<Player> playersList = new ArrayList<Player>();

    public GameWindow(String ip, String playerName) {
        this.ip = ip;

        player = new Player(playerName, GameCharacterClass.NOVICE, 0, 0, "", 0);

        name = playerName;


        /*       int size = SocketConnection.calculatePacketSize(new DataPacket(player));

        int size2 = UDPConnection.toByteArray(player).length;

        Out.d("datapacket", size + "");
        Out.d("rawish", size2 + "");

        byte[] data = UDPConnection.toByteArray(new DataPacket(player));
        byte[] data2 = UDPConnection.toByteArray(player);

        Out.d("zip", new ZIPCompressor().compress(data).length + "");
        Out.d("zip2", new ZIPCompressor().compress(data2).length + "");

        Out.d("lzma", LZMACompressor.compress(data).length + "");
        Out.d("lzma2", LZMACompressor.compress(data2).length + "");*/
    }

    @Override
    protected void createContent(Pane root) {
        try {
            ImageView background = new ImageView(ResourceManager.loadFXImage("map1.png"));
            root.getChildren().add(background);
        }
        catch (Exception e) {
            Out.e(e);
        }


        uiRoot = new Group();
        uiScene = new SubScene(uiRoot, 1280, 720);

        uiScene.setFill(Color.TRANSPARENT);


        uiScene.translateXProperty().bind(camera.translateXProperty());
        uiScene.translateYProperty().bind(camera.translateYProperty());

        try {
            InputStream is = ResourceManager.loadResourceAsStream("Vecna.otf").get();
            font = Font.loadFont(is, 28);
            is.close();
        }
        catch (IOException e) {
            Out.e(e);
        }


        playersList.add(player);
        players.getChildren().add(player.sprite);

        root.getChildren().add(players);

        root.getChildren().add(uiScene);

        try {
            ImageView img = new ImageView(ResourceManager.loadFXImage("ui_hotbar.png"));

            img.setTranslateX(300);
            img.setTranslateY(580);

            uiRoot.getChildren().add(img);
        }
        catch (IOException e) {
            e.printStackTrace();
        }






        // MENU

        menu = new Stage(StageStyle.TRANSPARENT);
        menu.setAlwaysOnTop(true);

        Menu menuRoot = new Menu();
        Scene menuScene = new Scene(menuRoot, 450, 375, Color.TRANSPARENT);

        menu.setScene(menuScene);

        // STATS

        stats = new Stage(StageStyle.TRANSPARENT);
        stats.setAlwaysOnTop(true);

        StatsWindow statsRoot = new StatsWindow();
        Scene attrScene = new Scene(statsRoot, 770, 620, Color.TRANSPARENT);

        stats.setScene(attrScene);


        // UI elements

        Button btnOptions2 = new Button("Menu");
        btnOptions2.setTranslateX(900);
        btnOptions2.setTranslateY(640);
        btnOptions2.setFont(font);
        btnOptions2.setOnAction(event -> {
            menu.show();
            ScaleTransition st = new ScaleTransition(Duration.seconds(0.66), menuRoot);
            st.setFromY(0);
            st.setToY(1);
            st.play();

            FadeTransition ft = new FadeTransition(Duration.seconds(1.5), menuRoot);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();
        });

        uiRoot.getChildren().add(btnOptions2);



        ProgressBar xpBar = new ProgressBar(0);
        xpBar.setPrefWidth(600);
        xpBar.setTranslateX(350);
        xpBar.setStyle("-fx-accent: rgb(255, 215, 0)");
        xpBar.progressProperty().bind(player.baseXPProperty);

        uiRoot.getChildren().add(xpBar);


        Button btnStats = new Button("Stats");
        btnStats.setTranslateX(1000);
        btnStats.setTranslateY(640);
        btnStats.setFont(font);
        btnStats.setOnAction(event -> {
            if (!stats.isShowing()) {
                stats.show();
                ScaleTransition st = new ScaleTransition(Duration.seconds(0.66), statsRoot);
                st.setFromX(0);
                st.setToX(1);
                st.play();

                FadeTransition ft = new FadeTransition(Duration.seconds(1.5), statsRoot);
                ft.setFromValue(0);
                ft.setToValue(1);
                ft.play();
            }
            else {
                ScaleTransition st = new ScaleTransition(Duration.seconds(0.66), statsRoot);
                st.setFromX(1);
                st.setToX(0);
                st.play();

                FadeTransition ft = new FadeTransition(Duration.seconds(1.5), statsRoot);
                ft.setFromValue(1);
                ft.setToValue(0);
                ft.setOnFinished(evt -> stats.hide());
                ft.play();
            }
        });

        uiRoot.getChildren().add(btnStats);

        //        HBox hbox = new HBox(10);
        //
        //        ProgressBar memoryUsageBar = new ProgressBar();
        //        memoryUsageBar.progressProperty().bind(RuntimeProperties.usedMemoryProperty().divide(RuntimeProperties.totalJVMMemoryProperty()));
        //        memoryUsageBar.progressProperty().addListener((obs, old, newValue) -> {
        //            int r = (int)(255*newValue.doubleValue());
        //            if (r > 255) r = 255;
        //            int g = (int)(255 - r);
        //            memoryUsageBar.setStyle(String.format("-fx-accent: rgb(%d, %d, 25)", r, g));
        //        });
        //
        //        Text memoryText = new Text();
        //        memoryText.textProperty().bind(RuntimeProperties.usedMemoryProperty().asString("%.0f")
        //                .concat(" / ").concat(RuntimeProperties.totalJVMMemoryProperty().asString("%.0f").concat(" MB")));
        //
        //        hbox.getChildren().addAll(new Text("Memory Usage: "), memoryUsageBar, memoryText);
        //        getChildren().add(hbox);


    }

    private class StatsWindow extends Parent {

        private double dx, dy;

        public StatsWindow() {

            StackPane stack = new StackPane();
            stack.setAlignment(Pos.TOP_LEFT);

            try {
                ImageView img = new ImageView(ResourceManager.loadFXImage("ui_stats_bg.png"));
                stack.getChildren().add(img);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            HBox hbox = new HBox(50);

            // ATTRS

            VBox attrBox = new VBox(10);
            attrBox.setRotate(1);
            attrBox.setPadding(new Insets(60, 0, 0, 100));

            for (int i = GameCharacter.STR; i <= GameCharacter.LUC; i++) {

                final int attrNum = i;

                HBox hLine = new HBox(10);
                hLine.setAlignment(Pos.CENTER_RIGHT);
                Text attr = new Text();
                attr.setFont(font);
                attr.textProperty().bind(
                        new SimpleStringProperty(Attribute.values()[i].name() + ": " )
                        .concat(player.attributeProperties[i]).concat("+")
                        .concat(player.bonusAttributeProperties[i]));

                Button btn = new Button("+");
                btn.setOnAction(event -> {
                    addActionRequest(new ActionRequest(Action.ATTR_UP, player.name, attrNum));
                });
                btn.visibleProperty().bind(player.attributePointsProperty.greaterThan(0)
                        .and(player.attributeProperties[i].lessThan(100)));

                hLine.getChildren().addAll(attr, btn);
                attrBox.getChildren().add(hLine);
            }


            // STATS

            VBox statBox = new VBox(10);
            statBox.setRotate(1);
            statBox.setPadding(new Insets(20, 0, 0, 70));

            Text statName = new Text(player.name);

            Text statClass = new Text();
            statClass.textProperty().bind(new SimpleStringProperty("Class: ").concat(player.classProperty));

            Text statLevel = new Text();
            statLevel.textProperty().bind(new SimpleStringProperty("Level: ").concat(player.baseLevelProperty)
                    .concat("/").concat(player.jobLevelProperty)
                    .concat("/").concat(player.statLevelProperty));

            Text statHPSP = new Text();
            statHPSP.textProperty().bind(new SimpleStringProperty("HP: ").concat(player.hpProperty)
                    .concat(" SP: ").concat(player.spProperty));

            Text statATK = new Text();
            statATK.textProperty().bind(new SimpleStringProperty("ATK: ").concat(player.statProperties[Player.ATK]));

            Text statMATK = new Text();
            statMATK.textProperty().bind(new SimpleStringProperty("MATK: ").concat(player.statProperties[Player.MATK]));

            Text statDEF = new Text();
            statDEF.textProperty().bind(new SimpleStringProperty("DEF: ").concat(player.statProperties[Player.DEF]));

            Text statMDEF = new Text();
            statMDEF.textProperty().bind(new SimpleStringProperty("MDEF: ").concat(player.statProperties[Player.MDEF]));

            Text statARM = new Text();
            statARM.textProperty().bind(new SimpleStringProperty("ARM: ").concat(player.statProperties[Player.ARM]));

            Text statMARM = new Text();
            statMARM.textProperty().bind(new SimpleStringProperty("MARM: ").concat(player.statProperties[Player.MARM]));

            Text statCrit = new Text();
            statCrit.textProperty().bind(new SimpleStringProperty("CRIT: ").concat(player.statProperties[Player.CRIT_CHANCE]).concat("%"));

            statBox.getChildren().addAll(statName, statClass, statLevel, statHPSP, statATK, statMATK, statDEF, statMDEF, statARM, statMARM, statCrit);
            statBox.getChildren().forEach(child -> ((Text)child).setFont(font));

            hbox.getChildren().addAll(attrBox, statBox);
            stack.getChildren().add(hbox);
            getChildren().add(stack);

            this.setOnMousePressed(event -> {
                dx = event.getSceneX();
                dy = event.getSceneY();
            });

            this.setOnMouseDragged(event -> {
                stats.setX(event.getScreenX() - dx);
                stats.setY(event.getScreenY() - dy);
            });
        }
    }

    private class Menu extends Parent {

        private double dx, dy;

        public Menu() {
            VBox menuBox = new VBox(15);
            menuBox.setAlignment(Pos.CENTER);

            UIButton btnResume = new UIButton("Resume");
            btnResume.setOnMouseClicked(event -> {
                FadeTransition ft = new FadeTransition(Duration.seconds(1.5), this);
                ft.setFromValue(1);
                ft.setToValue(0);
                ft.setOnFinished(evt -> {
                    menu.hide();
                });
                ft.play();


                ScaleTransition st = new ScaleTransition(Duration.seconds(1), this);
                st.setFromY(1);
                st.setToY(0);
                st.play();
            });
            UIButton btnOptions = new UIButton("Options");
            UIButton btnExit = new UIButton("Exit");
            btnExit.setOnMouseClicked(event -> {
                System.exit(0);
            });

            StackPane stack = new StackPane();

            try {
                ImageView bg = new ImageView(ResourceManager.loadFXImage("ui_menu_bg3.png"));
                stack.getChildren().add(bg);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            menuBox.getChildren().addAll(btnResume, btnOptions, btnExit);
            stack.getChildren().add(menuBox);

            getChildren().addAll(stack);

            this.setOnMousePressed(event -> {
                dx = event.getSceneX();
                dy = event.getSceneY();
            });

            this.setOnMouseDragged(event -> {
                menu.setX(event.getScreenX() - dx);
                menu.setY(event.getScreenY() - dy);
            });
        }
    }

    @Override
    protected void initScene(Scene scene) {
        this.scene = scene;
        scene.setCamera(camera);
        camera.translateXProperty().bind(player.xProperty.subtract(640));
        camera.translateYProperty().bind(player.yProperty.subtract(360));

        player.xProperty.addListener((obs, old, newValue) -> {
            Out.d("val", newValue.intValue() + "");
        });


        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.RIGHT) {
                //message.setTranslateX(message.getTranslateX() + 15);
            }
            if (event.getCode() == KeyCode.LEFT) {
                //message.setTranslateX(message.getTranslateX() - 15);
            }
            if (event.getCode() == KeyCode.UP) {
                //message.setTranslateY(message.getTranslateY() - 15);
            }
            if (event.getCode() == KeyCode.DOWN) {
                //message.setTranslateY(message.getTranslateY() + 15);
            }
        });

        scene.setOnMouseClicked(event -> {
            Out.d("clicked", (int)event.getX() + " " + (int)event.getY());
            addActionRequest(new ActionRequest(Action.MOVE, name, "map1.txt", (int)event.getX(), (int)event.getY()));

            try {
                ActionRequest[] thisGUI = this.clearPendingActionRequests();

                if (thisGUI.length > 0)
                    client.send(new DataPacket(thisGUI));
            }
            catch (IOException e) {
                Out.e("updateGameClient", "Failed to send a packet", this, e);
            }
        });
    }

    @Override
    protected void initStage(Stage primaryStage) {
        primaryStage.setWidth(1280);
        primaryStage.setHeight(720);
        primaryStage.setTitle("Orion MMORPG");
        primaryStage.setResizable(false);
        //primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });

        primaryStage.show();

        try {
            client = new UDPClient(ip, 55555, new ServerResponseParser());
            client.send(new DataPacket(new QueryRequest(Query.LOGIN, name)));

            //Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::showTraffic, 0, 10, TimeUnit.SECONDS);
        }
        catch (IOException e) {
            Out.e(e);
        }
    }

    private void showTraffic() {
        // 10 seconds
        float kbs = client.resetAC() / 10240.0f;
        Out.d("showTraffic", kbs + " KB/s (IN). Required bandwidth: " + kbs * 10 + " kbit/s");

        kbs = client.resetACSent() / 10240.0f;
        Out.d("showTraffic", kbs + " KB/s (OUT). Required bandwidth: " + kbs * 10 + " kbit/s");
    }

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
    class ServerResponseParser extends ServerPacketParser {
        @Override
        public void parseServerPacket(DataPacket packet) {
            if (packet.byteData != null && packet.byteData.length > 0 && packet.byteData[0] == -127) {

                for (Player p : playersList)
                    p.sprite.setValid(false);


                // raw data of players containing drawing data
                ByteArrayInputStream in = new ByteArrayInputStream(packet.byteData);

                // number of players
                int size = packet.byteData.length / 32;
                for (int i = 0; i < size; i++) {
                    byte[] data = new byte[16];
                    byte[] name = new byte[16];

                    try {
                        in.read(data);
                        in.read(name);

                        String playerName = new String(name).replace(new String(new byte[] {0}), "");

                        // search list of players for name
                        // if found update their data
                        // else create new player with data

                        boolean newPlayer = true;

                        for (Player p : playersList) {
                            if (p.name.equals(playerName)) {
                                newPlayer = false;
                                p.loadFromByteArray(data);
                                p.sprite.setValid(true);
                                break;
                            }
                        }

                        if (newPlayer) {

                            Out.d("player", "new");

                            Player p = new Player(playerName, GameCharacterClass.NOVICE, 0, 0, "", 0);
                            p.loadFromByteArray(data);
                            playersList.add(p);
                            players.getChildren().add(p.sprite);
                        }
                    }
                    catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                // END FOR


                // TODO: test
                players.getChildren().removeIf(node -> {
                    Sprite s = (Sprite)node;
                    return !s.isValid();
                });

                playersList.removeIf(player -> !player.sprite.isValid());

            }

            if (packet.objectData instanceof ServerResponse) {
                ServerResponse res = (ServerResponse) packet.objectData;

                //                            map = ObjectManager.getMapByName(res.data);
                //
                //                            selX = res.value1;
                //                            selY = res.value2;
            }

            if (packet.objectData instanceof Player) {
                Player p = (Player) packet.objectData;

                // update client's player
                player.update(p);

            }

            if (packet.multipleObjectData instanceof Player[]) {
                update((Player[]) packet.multipleObjectData);
            }

            if (packet.multipleObjectData instanceof Chest[]) {
                update((Chest[]) packet.multipleObjectData);
            }

            if (packet.multipleObjectData instanceof Enemy[]) {
                update((Enemy[]) packet.multipleObjectData);
            }

            if (packet.multipleObjectData instanceof Animation[]) {
                update((Animation[]) packet.multipleObjectData);
            }
        }

        /**
         * Update info about players
         *
         * @param sPlayers
         *                 players from server
         */
        private void update(Player[] sPlayers) {

            Player player = sPlayers[0];

            Platform.runLater(() -> {
                //message.setTranslateX(player.getX());
                //message.setTranslateY(player.getY());

                // manually trigger camera translate property to fire
                //                    camera.setTranslateX(message.getTranslateX());
                //                    camera.setTranslateY(message.getTranslateY());
                //                    scene.setCamera(camera);

                //message.setText(player.name + " " + player.getX() + " " + player.getY() + " " + player.getHP());



            });


            //                    for (Player p : sPlayers) {
            //                        if (p.name.equals(name)) {
            //                            player = p;
            //                            break;
            //                        }
            //                    }
            //
            //                    gameObjects.set(INDEX_PLAYERS, sPlayers);
            //                    // clear others in case they don't get updated
            //                    gameObjects.set(INDEX_ANIMATIONS, new Drawable[]{ });
            //                    gameObjects.set(INDEX_CHESTS, new Drawable[]{ });
            //                    gameObjects.set(INDEX_ENEMIES, new Drawable[]{ });
            //
            //                    // update main window
            //                    updateGameClient();
            //
            //                    // update other windows
            //                    inv.update(player);
            //                    st.update(player);
        }

        /**
         * Updates info about chests
         *
         * @param sChests
         *                  chests from server
         */
        private void update(Chest[] sChests) {
            //gameObjects.set(INDEX_CHESTS, sChests);
        }

        /**
         * Updates info about enemies
         *
         * @param sChests
         *                  enemies from server
         */
        private void update(Enemy[] sEnemies) {
            //                    enemies.clear();
            //                    for (Enemy e : sEnemies) {
            //                        enemies.add(e);
            //                    }
            //
            //                    gameObjects.set(INDEX_ENEMIES, sEnemies);
            //
            //                    tmpEnemies = new ArrayList<Enemy>(enemies);
        }

        private void update(Animation[] sAnimations) {
            //gameObjects.set(INDEX_ANIMATIONS, sAnimations);
        }
    }
}
