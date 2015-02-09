package uk.ac.brighton.uni.ab607.mmorpg.test.cases;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import uk.ac.brighton.uni.ab607.mmorpg.test.OrionTestBase;
import uk.ac.brighton.uni.ab607.mmorpg.test.Result;

import com.almasb.common.compression.LZMACompressor;
import com.almasb.common.util.ZIPCompressor;

public class ProtocolCompressionSpeedTest extends OrionTestBase {

    private ArrayList<Result> results = new ArrayList<Result>();
    private TextField fieldPackets = new TextField("1");

    @Override
    public Parent getResultsContent() {
        NumberAxis xAxis = new NumberAxis();
        CategoryAxis yAxis = new CategoryAxis();
        BarChart<Number, String> chart = new BarChart<>(xAxis, yAxis);

        chart.setTitle(String.valueOf(numPackets) + " packets");

        yAxis.setLabel("Compression");
        yAxis.setCategories(FXCollections.<String>observableArrayList("NONE", "ZIP", "LZMA"));

        xAxis.setLabel("Time (in seconds). Lower is better");

        for (int i = 0; i < results.size(); i++) {
            XYChart.Series<Number, String> series = new XYChart.Series<Number, String>();
            series.setName(results.get(i).name);
            series.getData().addAll(
                    new XYChart.Data<Number, String>(results.get(i).timeTook, "NONE"),
                    new XYChart.Data<Number, String>(results.get(i).timeTookZIP + results.get(i).timeTook, "ZIP"),
                    new XYChart.Data<Number, String>(results.get(i).timeTookLZMA + results.get(i).timeTook, "LZMA")
                    );

            series.getData().forEach(data -> {
                Label l = new Label(String.format("%.3f", data.getXValue().doubleValue()));
                l.setAlignment(Pos.CENTER_RIGHT);
                data.setNode(l);
            });

            chart.getData().add(series);
        }

        return chart;
    }

    @Override
    public Parent getTestControls() {
        Label label = new Label("Enter number of packets");
        return new Pane(new HBox(10, label, fieldPackets));
    }

    @Override
    protected void run() throws Exception {
        results.clear();
        numPackets = Integer.parseInt(fieldPackets.getText());
        generateRandomData();

        ZIPCompressor zip = new ZIPCompressor();
        LZMACompressor lzma = new LZMACompressor();



        Result result = new Result();
        long start = System.nanoTime();
        byte[] data = testByteStream();
        result.timeTook = (System.nanoTime() - start) / 1000000000.0;
        int size = data.length;
        start = System.nanoTime();
        int sizeZIP = zip.compress(data).length;
        result.timeTookZIP = (System.nanoTime() - start) / 1000000000.0;
        start = System.nanoTime();
        int sizeLZMA = lzma.compress(data).length;
        result.timeTookLZMA = (System.nanoTime() - start) / 1000000000.0;
        result.name = "ByteStream";
        result.size = size;
        result.sizeCompressedZIP = sizeZIP;
        result.sizeCompressedLZMA = sizeLZMA;
        results.add(result);

        result = new Result();
        start = System.nanoTime();
        data = testProtoBuf();
        result.timeTook = (System.nanoTime() - start) / 1000000000.0;
        size = data.length;
        start = System.nanoTime();
        sizeZIP = zip.compress(data).length;
        result.timeTookZIP = (System.nanoTime() - start) / 1000000000.0;
        start = System.nanoTime();
        sizeLZMA = lzma.compress(data).length;
        result.timeTookLZMA = (System.nanoTime() - start) / 1000000000.0;
        result.name = "ProtoBuf";
        result.size = size;
        result.sizeCompressedZIP = sizeZIP;
        result.sizeCompressedLZMA = sizeLZMA;
        results.add(result);

        result = new Result();
        start = System.nanoTime();
        data = testASN1();
        result.timeTook = (System.nanoTime() - start) / 1000000000.0;
        size = data.length;
        start = System.nanoTime();
        sizeZIP = zip.compress(data).length;
        result.timeTookZIP = (System.nanoTime() - start) / 1000000000.0;
        start = System.nanoTime();
        sizeLZMA = lzma.compress(data).length;
        result.timeTookLZMA = (System.nanoTime() - start) / 1000000000.0;
        result.name = "ASN1";
        result.size = size;
        result.sizeCompressedZIP = sizeZIP;
        result.sizeCompressedLZMA = sizeLZMA;
        results.add(result);

        result = new Result();
        start = System.nanoTime();
        data = testJavaSerialization();
        result.timeTook = (System.nanoTime() - start) / 1000000000.0;
        size = data.length;
        start = System.nanoTime();
        sizeZIP = zip.compress(data).length;
        result.timeTookZIP = (System.nanoTime() - start) / 1000000000.0;
        start = System.nanoTime();
        sizeLZMA = lzma.compress(data).length;
        result.timeTookLZMA = (System.nanoTime() - start) / 1000000000.0;
        result.name = "JavaSerialization";
        result.size = size;
        result.sizeCompressedZIP = sizeZIP;
        result.sizeCompressedLZMA = sizeLZMA;
        results.add(result);
    }
}
