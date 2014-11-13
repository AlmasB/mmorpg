package uk.ac.brighton.uni.ab607.mmorpg.client.fx;

import java.io.IOException;
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
import javafx.scene.Camera;
import javafx.scene.ParallelCamera;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
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
    private Camera camera = new ParallelCamera();

    public GameWindow(String ip, String playerName) {
        name = playerName;

        try {
            client = new UDPClient(ip, 55555, new ServerResponseParser());
            client.send(new DataPacket(new QueryRequest(Query.LOGIN, name)));
        }
        catch (IOException e) {
            Out.e(e);
        }

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
    }

    @Override
    protected void initScene(Scene scene) {
        this.scene = scene;
        //scene.cameraProperty().set(camera);

        scene.setCamera(camera);
        camera.translateXProperty().bind(message.translateXProperty().subtract(640));
        camera.translateYProperty().bind(message.translateYProperty().subtract(360));



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

                    //camera.

                    // manually trigger camera translate property to fire
                    //                    camera.setTranslateX(message.getTranslateX());
                    //                    camera.setTranslateY(message.getTranslateY());
                    //                    scene.setCamera(camera);

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
