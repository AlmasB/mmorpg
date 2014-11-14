package uk.ac.brighton.uni.ab607.mmorpg.client.fx;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;















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
import javafx.scene.ParallelCamera;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

public class GameWindow extends FXWindow {

    private String name;
    private UDPClient client = null;

    private Text message = new Text();

    private Scene scene;
    private SubScene uiScene;
    private Camera camera = new ParallelCamera();

    private Pane uiRoot;

    private Stage menu, stats;

    private Font font;

    private Player player = new Player("Player Name", GameCharacterClass.KNIGHT, 0, 0, "", 0);

    public GameWindow(String ip, String playerName) {
        name = playerName;

        /*        try {
            client = new UDPClient(ip, 55555, new ServerResponseParser());
            client.send(new DataPacket(new QueryRequest(Query.LOGIN, name)));
        }
        catch (IOException e) {
            Out.e(e);
        }*/


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


        root.getChildren().add(message);

        uiRoot = new Pane();
        uiScene = new SubScene(uiRoot, 1280, 720);
        uiScene.translateXProperty().bind(camera.translateXProperty());

        try {
            font = Font.loadFont(Files.newInputStream(Paths.get("res/Vecna.otf")), 28);
        }
        catch (IOException e) {
            Out.e(e);
        }



        VBox vbox = new VBox(10);
        Button btnOptions2 = new Button("Options");
        if (font != null) {
            btnOptions2.setFont(font);
            Out.d("font", "added");
        }
        vbox.getChildren().add(btnOptions2);
        uiRoot.getChildren().addAll(vbox);


        root.getChildren().add(uiScene);

        // MENU

        menu = new Stage(StageStyle.TRANSPARENT);
        //menu.setHeight(375);
        menu.setAlwaysOnTop(true);

        Scene menuScene = new Scene(new Menu(), 450, 375, Color.TRANSPARENT);

        menu.setScene(menuScene);
        //menu.show();

        // STATS

        stats = new Stage(StageStyle.TRANSPARENT);
        //stats.setX(0);
        //stats.setY(0);
        stats.setAlwaysOnTop(true);


        Scene attrScene = new Scene(new StatsWindow(), 770, 620, Color.TRANSPARENT);

        stats.setScene(attrScene);
        stats.show();
    }

    private class StatsWindow extends Parent {

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
                HBox hLine = new HBox(10);
                hLine.setAlignment(Pos.CENTER_RIGHT);
                Text attr = new Text();
                attr.setFont(font);
                attr.textProperty().bind(
                        new SimpleStringProperty(Attribute.values()[i].name() + ": " )
                        .concat(player.attributeProperties[i]).concat("+")
                        .concat(player.bonusAttributeProperties[i]));

                Button btn = new Button("+");
                // TODO: btn impl
                //btn.setFont(font);

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
        }
    }

    private class Menu extends Parent {

        private double dx, dy;

        public Menu() {
            VBox menuBox = new VBox(15);
            menuBox.setAlignment(Pos.CENTER);

            StyledButton btnResume = new StyledButton("Resume");
            btnResume.setOnMouseClicked(event -> {
                FadeTransition ft = new FadeTransition(Duration.seconds(1.5), this);
                ft.setFromValue(1);
                ft.setToValue(0);
                ft.play();

                ScaleTransition st = new ScaleTransition(Duration.seconds(1), this);
                st.setFromY(1);
                st.setToY(0);
                st.play();
            });
            StyledButton btnOptions = new StyledButton("Options");
            StyledButton btnExit = new StyledButton("Exit");
            btnExit.setOnMouseClicked(event -> {
                System.exit(0);
            });

            StackPane stack = new StackPane();

            try {
                ImageView bg = new ImageView(ResourceManager.loadFXImage("ui_menu_bg3.png"));

                //bg.setFitWidth(300);
                //bg.setFitHeight(400);
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

    private class StyledButton extends Parent {

        private ImageView imgView;

        private Image entered, exited, pressed;

        public StyledButton(String name) {
            try {
                entered = ResourceManager.loadFXImage("ui_menu_button2.png");
                exited = ResourceManager.loadFXImage("ui_menu_button.png");
                pressed = ResourceManager.loadFXImage("ui_menu_button3.png");
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            try {
                font = Font.loadFont(Files.newInputStream(Paths.get("res/Vecna.otf")), 28);
            }
            catch (IOException e) {
                Out.e(e);
            }


            imgView = new ImageView(exited);

            StackPane stack = new StackPane();
            stack.setAlignment(Pos.CENTER);
            Text text = new Text(name);
            text.setFill(Color.WHITESMOKE);
            text.setFont(font);
            stack.getChildren().addAll(imgView, text);

            getChildren().add(stack);


            this.setOnMousePressed(event -> {
                imgView.setImage(pressed);
            });

            this.setOnMouseReleased(event -> {
                imgView.setImage(entered);
            });

            this.setOnMouseEntered(event -> {
                imgView.setImage(entered);
            });

            this.setOnMouseExited(event -> {
                imgView.setImage(exited);
            });
        }
    }

    @Override
    protected void initScene(Scene scene) {
        this.scene = scene;
        //scene.cameraProperty().set(camera);

        scene.setCamera(camera);
        camera.translateXProperty().bind(message.translateXProperty().subtract(640));
        camera.translateYProperty().bind(message.translateYProperty().subtract(360));



        scene.setOnMouseClicked(event -> {
            //            Out.d("clicked", (int)event.getX() + " " + (int)event.getY());
            //            addActionRequest(new ActionRequest(Action.MOVE, name, "map1.txt", (int)event.getX(), (int)event.getY()));
            //
            //            try {
            //                ActionRequest[] thisGUI = this.clearPendingActionRequests();
            //
            //                if (thisGUI.length > 0)
            //                    client.send(new DataPacket(thisGUI));
            //            }
            //            catch (IOException e) {
            //                Out.e("updateGameClient", "Failed to send a packet", this, e);
            //            }
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
                // raw data of players containing drawing data
                ByteArrayInputStream in = new ByteArrayInputStream(packet.byteData);

                // number of players
                int size = packet.byteData.length / 32;
            }

            //Out.d("packet recv", packet.objectData == null ? "null" : packet.objectData.getClass().getSimpleName());

            //Out.d("recv", new String(packet.byteData));
            if (packet.objectData instanceof ServerResponse) {
                ServerResponse res = (ServerResponse) packet.objectData;

                //                            map = ObjectManager.getMapByName(res.data);
                //
                //                            selX = res.value1;
                //                            selY = res.value2;
            }

            if (packet.objectData instanceof Player) {
                Player player = (Player) packet.objectData;

                // update client's player
                // this.player.update(player);

                Platform.runLater(() -> {
                    message.setTranslateX(player.getX());
                    message.setTranslateY(player.getY());


                    message.setText(player.name + " " + player.getX() + " " + player.getY() + " " + player.getHP());



                });

                //Out.d("update", message.getText());

                //                            // login complete, all set, we can now show GUI
                //                            // and start drawing
                //                            inv = new InventoryGUI(player);
                //                            st = new StatsGUI(player);
                //
                //                            setVisible(true);
                //                            requestFocusInWindow();
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
                message.setTranslateX(player.getX());
                message.setTranslateY(player.getY());

                //camera.

                // manually trigger camera translate property to fire
                //                    camera.setTranslateX(message.getTranslateX());
                //                    camera.setTranslateY(message.getTranslateY());
                //                    scene.setCamera(camera);

                message.setText(player.name + " " + player.getX() + " " + player.getY() + " " + player.getHP());



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
