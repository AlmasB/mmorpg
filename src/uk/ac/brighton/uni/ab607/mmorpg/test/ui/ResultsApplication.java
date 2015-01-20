package uk.ac.brighton.uni.ab607.mmorpg.test.ui;

import uk.ac.brighton.uni.ab607.mmorpg.test.OrionTestBase;
import uk.ac.brighton.uni.ab607.mmorpg.test.cases.ProtocolSizeDependencyTest;
import uk.ac.brighton.uni.ab607.mmorpg.test.cases.ProtocolSizeTest;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class ResultsApplication extends Application {

    @FXML
    private Label labelMessage;

    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private ChoiceBox<OrionTestBase> cbTests;

    @FXML
    private Pane testControlsPane;

    public void onButtonPress(ActionEvent event) {
        progressIndicator.setVisible(true);

        OrionTestBase test = cbTests.getValue();
        TestTask task = new TestTask(test);
        task.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                Stage stage = new Stage();
                stage.setTitle(test.toString());
                Scene scene = new Scene(newValue);
                stage.setScene(scene);
                stage.show();
            }
        });
        labelMessage.textProperty().bind(task.messageProperty());
        progressIndicator.progressProperty().bind(task.progressProperty());

        Thread bgThread = new Thread(task);
        bgThread.setDaemon(true);
        bgThread.start();
    }

    private static class TestTask extends Task<Parent> {
        private final OrionTestBase test;

        public TestTask(OrionTestBase test) {
            this.test = test;
        }

        @Override
        protected Parent call() throws Exception {
            updateMessage("Test started");
            test.start();
            updateMessage(String.format("finished in %.5f seconds", test.getTimeTookSeconds()));
            this.updateProgress(1, 1);
            return test.getResultsContent();
        }
    }

    @Override
    public void init() throws Exception {

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ui.fxml"));
        loader.setController(this);
        Parent root = (Parent) loader.load();

        cbTests.getSelectionModel().selectedItemProperty().addListener((obs, old, newValue) -> {
            testControlsPane.getChildren().clear();
            testControlsPane.getChildren().add(newValue.getTestControls());
        });
        cbTests.getItems().addAll(new ProtocolSizeTest(),
                new ProtocolSizeDependencyTest());
        cbTests.getSelectionModel().selectFirst();

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Orion MMORPG Test Results Reporting Tool");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
