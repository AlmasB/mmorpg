package uk.ac.brighton.uni.ab607.mmorpg.client.fx;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import uk.ac.brighton.uni.ab607.mmorpg.client.ui.InventoryGUI;
import uk.ac.brighton.uni.ab607.mmorpg.client.ui.StatsGUI;
import uk.ac.brighton.uni.ab607.mmorpg.client.ui.animation.Animation;
import uk.ac.brighton.uni.ab607.mmorpg.common.Player;
import uk.ac.brighton.uni.ab607.mmorpg.common.item.Chest;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Enemy;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.ObjectManager;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.ActionRequest;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.QueryRequest;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.ServerResponse;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.ActionRequest.Action;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.QueryRequest.Query;
import javafx.application.Platform;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import com.almasb.common.net.DataPacket;
import com.almasb.common.net.ServerPacketParser;
import com.almasb.common.net.UDPClient;
import com.almasb.common.util.Out;
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

    public GameWindow(String ip, String playerName) {
        name = playerName;

        /*        try {
            client = new UDPClient(ip, 55555, new ServerResponseParser());
            client.send(new DataPacket(new QueryRequest(Query.LOGIN, name)));
        }
        catch (IOException e) {
            Out.e(e);
        }*/

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


        Font font = null;
        try {
            font = Font.loadFont(Files.newInputStream(Paths.get("res/Vecna.otf")), 28);
        }
        catch (IOException e) {
            Out.e(e);
        }



        VBox vbox = new VBox(10);
        Button btnOptions = new Button("Options");
        if (font != null) {
            btnOptions.setFont(font);
            Out.d("font", "added");
        }
        vbox.getChildren().add(btnOptions);
        uiRoot.getChildren().addAll(vbox);


        root.getChildren().add(uiScene);

        // MENU

        Stage menu = new Stage(StageStyle.UNDECORATED);
        menu.setWidth(300);
        menu.setHeight(400);
        menu.setAlwaysOnTop(true);

        VBox menuBox = new VBox(15);
        menuBox.setAlignment(Pos.CENTER);
        Scene menuScene = new Scene(menuBox);

        Button menuBtnBack = new Button("Back to game");

        ImageView img = null;
        ImageView img2 = null;
        try {
            img = new ImageView(ResourceManager.loadFXImage("ui_menu_button.png"));
            img2 = new ImageView(ResourceManager.loadFXImage("ui_menu_button.png"));
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        //if (img != null)
        menuBtnBack.setGraphic(img2);

        Rectangle clip = new Rectangle();
        clip.setX(5);
        clip.setY(5);
        clip.widthProperty().bind(menuBtnBack.widthProperty().subtract(10));
        clip.heightProperty().bind(menuBtnBack.heightProperty().subtract(10));
        clip.setArcWidth(30);
        clip.setArcHeight(30);

        img.fitWidthProperty().bind(menuBtnBack.widthProperty());
        img.fitHeightProperty().bind(menuBtnBack.heightProperty());


        //img2.fitWidthProperty().bind(menuBtnBack.widthProperty());
        //img2.fitHeightProperty().bind(menuBtnBack.heightProperty());


        menuBtnBack.setClip(img);
        //menuBtnBack.setAlignment(Pos.);


        if (font != null) {
            menuBtnBack.setFont(font);
            Out.d("font", "added");
        }

        Button menuBtnOptions = new Button("Options");
        if (font != null) {
            menuBtnOptions.setFont(font);
            Out.d("font", "added");
        }

        Button menuBtnExit = new Button("Exit");
        if (font != null) {
            menuBtnExit.setFont(font);
            Out.d("font", "added");
        }

        menuBox.getChildren().addAll(new StyledButton(), menuBtnOptions, menuBtnExit);

        menu.setScene(menuScene);
        menu.show();
    }

    class StyledButton extends Parent {

        private ImageView imgView;

        private Image entered, exited;

        public StyledButton() {
            try {
                entered = ResourceManager.loadFXImage("ui_menu_button2.png");
                exited = ResourceManager.loadFXImage("ui_menu_button.png");
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            Font font = null;
            try {
                font = Font.loadFont(Files.newInputStream(Paths.get("res/Vecna.otf")), 28);
            }
            catch (IOException e) {
                Out.e(e);
            }


            imgView = new ImageView(exited);

            StackPane stack = new StackPane();
            stack.setAlignment(Pos.CENTER);
            Text text = new Text("Resume");
            text.setFont(font);
            stack.getChildren().addAll(imgView, text);

            getChildren().add(stack);

            this.setOnMouseClicked(event -> {
                Out.d("mouse", "clicked");
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
