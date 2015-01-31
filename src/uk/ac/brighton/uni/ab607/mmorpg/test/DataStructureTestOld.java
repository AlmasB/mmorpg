package uk.ac.brighton.uni.ab607.mmorpg.test;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacter;
import uk.ac.brighton.uni.ab607.mmorpg.common.Player;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.TextAnimationMessage;
import uk.ac.brighton.uni.ab607.mmorpg.test.GameCharacterProtoBuf.GameCharProtoBuf;
import uk.ac.brighton.uni.ab607.mmorpg.test.GameMessageProtoBuf.MessageProtoBuf;
import uk.ac.brighton.uni.ab607.mmorpg.test.asn1.AsnOutputStream;
import uk.ac.brighton.uni.ab607.mmorpg.test.asn1.Tag;

import com.almasb.common.compression.LZMACompressor;
import com.almasb.common.compression.LZWCompressor;
import com.almasb.common.test.Test;
import com.almasb.common.util.Out;
import com.almasb.common.util.ZIPCompressor;

public class DataStructureTestOld {

    enum TestType {
        PROTOCOL, NUM_PLAYERS
    }

    // default test to run
    private static TestType type = TestType.PROTOCOL;

    private static final int NUM_ENEMIES = 100;
    private static int NUM_PLAYERS = 10000;
    private static int NUM_PACKETS = NUM_PLAYERS + NUM_ENEMIES;

    private static Data[] randomData;
    private static DataMessage[] randomData2;

    private static ArrayList<Result> results = new ArrayList<Result>();

    private static ArrayList<Result> results1 = new ArrayList<Result>();
    private static ArrayList<Result> results1000 = new ArrayList<Result>();
    private static ArrayList<Result> results10000 = new ArrayList<Result>();

    private static Random rand = new Random();

    private static class Data implements Serializable {
        private static final long serialVersionUID = 1L;

        public int xy;
        public int sprite;
        public byte placeDir;
        public int ids;
    }

    private static class DataMessage implements Serializable {
        private static final long serialVersionUID = 2L;

        public int xy;
        public byte type;
        public String text;
    }

    private static class Result implements Comparable<Result> {
        public String name;
        public int size;
        public int sizeZip;
        public int sizeLzma;
        public double timeTook;

        public Result(String name, int size, int sizeZip, int sizeLzma, double timeTook) {
            this.name = name;
            this.size = size;
            this.sizeZip = sizeZip;
            this.sizeLzma = sizeLzma;
            this.timeTook = timeTook;
        }

        // TODO: maybe add coefficient of timeTook to determine best result
        @Override
        public int compareTo(Result o) {
            return o.size - size;
        }
    }

    private static abstract class DataTest extends Test {
        protected ByteArrayOutputStream output = new ByteArrayOutputStream();

        @Override
        protected void clean() throws Exception {
            output.close();

            byte[] result = output.toByteArray();
            Out.i("result size", result.length + " bytes");

            byte[] compressedZip = new ZIPCompressor().compress(result);
            Out.i("result compressed size", compressedZip.length + " bytes");

            if (type == TestType.NUM_PLAYERS) {
                switch (NUM_PLAYERS) {
                    case 1:
                        results1.add(new Result(getClass().getSimpleName(),
                                result.length, compressedZip.length, LZMACompressor.compress(result).length,
                                getTimeTookSeconds()));
                        break;
                    case 1000:
                        results1000.add(new Result(getClass().getSimpleName(),
                                result.length, compressedZip.length, LZMACompressor.compress(result).length,
                                getTimeTookSeconds()));
                        break;
                    case 10000:
                        results10000.add(new Result(getClass().getSimpleName(),
                                result.length, compressedZip.length, LZMACompressor.compress(result).length,
                                getTimeTookSeconds()));
                        break;
                }
            }
            else {
                results.add(new Result(getClass().getSimpleName(),
                        result.length, compressedZip.length, LZMACompressor.compress(result).length,
                        getTimeTookSeconds()));
            }
        }
    }

    private static class ProtoBufTest extends DataTest {
        @Override
        protected void run() throws Exception {
            for (Data data : randomData) {
                GameCharProtoBuf.Builder playerBuilder = GameCharProtoBuf.newBuilder();
                playerBuilder.setXy(data.xy);
                playerBuilder.setSpriteID(data.sprite);
                playerBuilder.setPlacedir(data.placeDir);
                playerBuilder.setIds(data.ids);

                GameCharProtoBuf player = playerBuilder.build();
                output.write(player.toByteArray());
            }

            for (DataMessage data : randomData2) {
                MessageProtoBuf.Builder messageBuilder = MessageProtoBuf.newBuilder();
                messageBuilder.setXy(data.xy);
                messageBuilder.setType(data.type);
                messageBuilder.setText(data.text);

                MessageProtoBuf message = messageBuilder.build();
                output.write(message.toByteArray());
            }
        }
    }

    private static class ASNOneTest extends DataTest {
        @Override
        protected void run() throws Exception {
            AsnOutputStream out = new AsnOutputStream();

            for (Data data : randomData) {
                out.writeInteger(Tag.CLASS_UNIVERSAL, Tag.INTEGER, data.xy);
                out.writeInteger(Tag.CLASS_UNIVERSAL, Tag.INTEGER, data.sprite);
                out.write(data.placeDir);
                out.writeInteger(Tag.CLASS_UNIVERSAL, Tag.INTEGER, data.ids);
            }

            for (DataMessage data : randomData2) {
                out.writeInteger(Tag.CLASS_UNIVERSAL, Tag.INTEGER, data.xy);
                out.write(data.type);
                out.writeStringUTF8(data.text);
            }
            out.close();

            output.write(out.toByteArray());
            output.close();
        }
    }

    private static class ByteStreamTest extends DataTest {
        @Override
        protected void run() throws Exception {
            for (Data data : randomData) {
                GameCharacterByteStream player = new GameCharacterByteStream();
                player.setXY(data.xy);
                player.setSpriteID(data.sprite);
                player.setPlaceDir(data.placeDir);
                player.setIDs(data.ids);

                output.write(player.toByteArray());
            }

            for (DataMessage data : randomData2) {
                GameMessageByteStream message = new GameMessageByteStream();
                message.setXY(data.xy);
                message.setText(data.text);
                message.setType(data.type);

                output.write(message.toByteArray());
            }
        }
    }

    private static class JavaSerializationTest extends DataTest {
        @Override
        protected void run() throws Exception {
            ObjectOutputStream oos = new ObjectOutputStream(output);
            for (Data data : randomData) {
                oos.writeObject(data);
            }
            for (DataMessage data : randomData2) {
                oos.writeObject(data);
            }

            oos.close();
            output.close();
        }
    }

    private static void generateRandomData() {
        randomData = new Data[NUM_PACKETS];
        randomData2 = new DataMessage[NUM_PACKETS];

        for (int i = 0; i < randomData.length; i++) {
            randomData[i] = new Data();
            randomData[i].xy = rand.nextInt();
            randomData[i].sprite = rand.nextInt();
            randomData[i].placeDir = (byte)rand.nextInt();
            randomData[i].ids = rand.nextInt();

            randomData2[i] = new DataMessage();
            randomData2[i].xy = rand.nextInt();
            randomData2[i].type = (byte) rand.nextInt();

            byte[] tmp = new byte[rand.nextInt(60)];
            //byte[] tmp = new byte[59];
            rand.nextBytes(tmp);
            randomData2[i].text = new String(tmp);
        }
    }

    private static void showResults() {
        //Collections.sort(results);
        Application.launch(ResultGUI.class, "");
    }

    public static class ResultGUI extends Application {

        private NumberAxis xAxis = new NumberAxis();
        private CategoryAxis yAxis = new CategoryAxis();

        private BarChart<Number, String> chart = new BarChart<>(xAxis, yAxis);

        @SuppressWarnings("unchecked")
        public Parent createContent() {

            if (type == TestType.PROTOCOL) {
                chart.setTitle("Data structures test - " + NUM_PACKETS + " packets (= " + NUM_PACKETS + " players)");

                yAxis.setLabel("Compression Method");
                yAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(new String[] {"NONE", "ZIP", "LZMA"})));

                xAxis.setLabel("Size (in bytes)");

                for (Result res : results) {
                    XYChart.Series<Number, String> series = new XYChart.Series<Number, String>();
                    series.setName(res.name + " " + String.format("(%.3f sec)", res.timeTook));
                    series.getData().addAll(
                            new XYChart.Data<Number, String>(res.size, "NONE"),
                            new XYChart.Data<Number, String>(res.sizeZip, "ZIP"),
                            new XYChart.Data<Number, String>(res.sizeLzma, "LZMA")
                            );

                    series.getData().forEach(data -> {
                        Label l = new Label(data.getXValue().toString());
                        l.setAlignment(Pos.CENTER_RIGHT);
                        data.setNode(l);
                    });

                    chart.getData().add(series);
                }
            }
            else {
                chart.setTitle("Data structure packet size based on number of players");

                yAxis.setLabel("Number of Players");
                yAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(new String[] {"1", "1000", "10000"})));

                xAxis.setLabel("Size (in bytes)");

                for (int i = 0; i < results1.size(); i++) {
                    XYChart.Series<Number, String> series = new XYChart.Series<Number, String>();
                    series.setName(results1.get(i).name);
                    series.getData().addAll(
                            new XYChart.Data<Number, String>(results1.get(i).size, "1"),
                            new XYChart.Data<Number, String>(results1000.get(i).size, "1000"),
                            new XYChart.Data<Number, String>(results10000.get(i).size, "10000")
                            );

                    series.getData().forEach(data -> {
                        Label l = new Label(data.getXValue().toString());
                        l.setAlignment(Pos.CENTER_RIGHT);
                        data.setNode(l);
                    });

                    chart.getData().add(series);
                }
            }

            return chart;
        }

        @Override
        public void start(Stage primaryStage) throws Exception {
            primaryStage.setScene(new Scene(createContent()));
            primaryStage.show();
        }
    }

    public static void main(String[] args) {

        switch (type) {
            case PROTOCOL:
                generateRandomData();
                new ASNOneTest().start();
                new ProtoBufTest().start();
                new ByteStreamTest().start();
                new JavaSerializationTest().start();

                showResults();
                break;
            case NUM_PLAYERS:
                NUM_PLAYERS = 1;
                NUM_PACKETS = NUM_PLAYERS + NUM_ENEMIES;
                generateRandomData();
                new ASNOneTest().start();
                new ProtoBufTest().start();
                new ByteStreamTest().start();
                new JavaSerializationTest().start();

                NUM_PLAYERS = 1000;
                NUM_PACKETS = NUM_PLAYERS + NUM_ENEMIES;
                generateRandomData();
                new ASNOneTest().start();
                new ProtoBufTest().start();
                new ByteStreamTest().start();
                new JavaSerializationTest().start();

                NUM_PLAYERS = 10000;
                NUM_PACKETS = NUM_PLAYERS + NUM_ENEMIES;
                generateRandomData();
                new ASNOneTest().start();
                new ProtoBufTest().start();
                new ByteStreamTest().start();
                new JavaSerializationTest().start();

                showResults();
                break;
            default:
                break;
        }
    }
}
