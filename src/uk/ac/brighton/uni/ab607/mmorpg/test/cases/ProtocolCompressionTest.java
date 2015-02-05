package uk.ac.brighton.uni.ab607.mmorpg.test.cases;

import java.util.ArrayList;
import java.util.Arrays;

import com.almasb.common.compression.LZMACompressor;
import com.almasb.common.util.ZIPCompressor;

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

public class ProtocolCompressionTest extends OrionTestBase {

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

        xAxis.setLabel("Size (in bytes). Lower is better");

        for (int i = 0; i < results.size(); i++) {
            XYChart.Series<Number, String> series = new XYChart.Series<Number, String>();
            series.setName(results.get(i).name);
            series.getData().addAll(
                    new XYChart.Data<Number, String>(results.get(i).size, "NONE"),
                    new XYChart.Data<Number, String>(results.get(i).sizeCompressedZIP, "ZIP"),
                    new XYChart.Data<Number, String>(results.get(i).sizeCompressedLZMA, "LZMA")
                    );

            series.getData().forEach(data -> {
                Label l = new Label(data.getXValue().toString());
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

        byte[] data = testByteStream();

        int size = data.length;
        int sizeZIP = zip.compress(data).length;
        int sizeLZMA = lzma.compress(data).length;
        Result result = new Result();
        result.name = "ByteStream";
        result.size = size;
        result.sizeCompressedZIP = sizeZIP;
        result.sizeCompressedLZMA = sizeLZMA;
        results.add(result);

        data = testProtoBuf();
        size = data.length;
        sizeZIP = zip.compress(data).length;
        sizeLZMA = lzma.compress(data).length;
        result = new Result();
        result.name = "ProtoBuf";
        result.size = size;
        result.sizeCompressedZIP = sizeZIP;
        result.sizeCompressedLZMA = sizeLZMA;
        results.add(result);

        data = testASN1();
        size = data.length;
        sizeZIP = zip.compress(data).length;
        sizeLZMA = lzma.compress(data).length;
        result = new Result();
        result.name = "ASN1";
        result.size = size;
        result.sizeCompressedZIP = sizeZIP;
        result.sizeCompressedLZMA = sizeLZMA;
        results.add(result);

        data = testJavaSerialization();
        size = data.length;
        sizeZIP = zip.compress(data).length;
        sizeLZMA = lzma.compress(data).length;
        result = new Result();
        result.name = "JavaSerialization";
        result.size = size;
        result.sizeCompressedZIP = sizeZIP;
        result.sizeCompressedLZMA = sizeLZMA;
        results.add(result);
    }
}
