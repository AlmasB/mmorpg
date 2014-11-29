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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import uk.ac.brighton.uni.ab607.mmorpg.common.GameCharacter;
import uk.ac.brighton.uni.ab607.mmorpg.common.Player;
import uk.ac.brighton.uni.ab607.mmorpg.test.GameCharacterProtoBuf.GameCharProtoBuf;
import uk.ac.brighton.uni.ab607.mmorpg.test.asn1.AsnOutputStream;
import uk.ac.brighton.uni.ab607.mmorpg.test.asn1.Tag;

import com.almasb.common.compression.LZMACompressor;
import com.almasb.common.test.Test;
import com.almasb.common.util.Out;
import com.almasb.common.util.ZIPCompressor;

public class DataStructureTest {

    private static final int NUM_PACKETS = 10000;
    private static Data[] randomData = new Data[NUM_PACKETS];

    private static ArrayList<Result> results = new ArrayList<Result>();

    private static Random rand = new Random();

    private static class Data implements Serializable {
        private static final long serialVersionUID = 1L;

        public int xy;
        public int sprite;
        public byte placeDir;
        public int ids;
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

            results.add(new Result(getClass().getSimpleName(),
                    result.length, compressedZip.length, LZMACompressor.compress(result).length,
                    getTimeTookSeconds()));
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
            out.close();

            output.write(out.toByteArray());
            output.close();
        }
    }

    private static class ByteStreamTest extends DataTest {
        @Override
        protected void run() throws Exception {
            for (Data data : randomData) {
                GameCharacter player = new Player();
                player.setXY(data.xy);
                player.setSpriteID(data.sprite);
                player.setPlaceDir(data.placeDir);
                player.setIDs(data.ids);

                output.write(player.toByteArray());
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

            oos.close();
            output.close();
        }
    }

    private static void generateRandomData() {
        for (int i = 0; i < randomData.length; i++) {
            randomData[i] = new Data();
            randomData[i].xy = rand.nextInt();
            randomData[i].sprite = rand.nextInt();
            randomData[i].placeDir = (byte)rand.nextInt();
            randomData[i].ids = rand.nextInt();
        }
    }

    private static void showResults() {
        Collections.sort(results);
        Application.launch(ResultGUI.class, "");
    }

    public static class ResultGUI extends Application {

        private NumberAxis xAxis = new NumberAxis();
        private CategoryAxis yAxis = new CategoryAxis();

        private BarChart<Number, String> chart = new BarChart<>(xAxis, yAxis);

        @SuppressWarnings("unchecked")
        public Parent createContent() {
            chart.setTitle("Data structures test - " + NUM_PACKETS + " packets");

            yAxis.setLabel("Compression Method");
            yAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(new String[] {"NONE", "ZIP", "LZMA"})));

            xAxis.setLabel("Size (in bytes)");

            for (Result res : results) {
                XYChart.Series<Number, String> series = new XYChart.Series<Number, String>();
                series.setName(res.name);
                series.getData().addAll(
                        new XYChart.Data<Number, String>(res.size, "NONE"),
                        new XYChart.Data<Number, String>(res.sizeZip, "ZIP"),
                        new XYChart.Data<Number, String>(res.sizeLzma, "LZMA")
                        );

                chart.getData().add(series);
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
        generateRandomData();

        new ASNOneTest().start();
        new ProtoBufTest().start();
        new ByteStreamTest().start();
        new JavaSerializationTest().start();

        showResults();
    }
}
