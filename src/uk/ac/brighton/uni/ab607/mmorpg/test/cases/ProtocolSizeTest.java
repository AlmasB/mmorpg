package uk.ac.brighton.uni.ab607.mmorpg.test.cases;

import java.util.ArrayList;
import java.util.Arrays;

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

public class ProtocolSizeTest extends OrionTestBase {

    private ArrayList<Result> results = new ArrayList<Result>();
    private TextField fieldPackets = new TextField("1");

    @Override
    public Parent getTestControls() {
        Label label = new Label("Enter number of packets");
        return new Pane(new HBox(10, label, fieldPackets));
    }

    @SuppressWarnings("unchecked")
    @Override
    public Parent getResultsContent() {
        NumberAxis xAxis = new NumberAxis();
        CategoryAxis yAxis = new CategoryAxis();
        BarChart<Number, String> chart = new BarChart<>(xAxis, yAxis);

        chart.setTitle("Data structure packet size based on number of packets");

        yAxis.setLabel("Number of Packets");
        yAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(new String[] {String.valueOf(numPackets)})));

        xAxis.setLabel("Size (in bytes). Lower is better");

        for (int i = 0; i < results.size(); i++) {
            XYChart.Series<Number, String> series = new XYChart.Series<Number, String>();
            series.setName(results.get(i).name);
            series.getData().addAll(
                    new XYChart.Data<Number, String>(results.get(i).size, String.valueOf(numPackets))
                    //                    new XYChart.Data<Number, String>(results1000.get(i).size, "1000"),
                    //                    new XYChart.Data<Number, String>(results10000.get(i).size, "10000")
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
    protected void run() throws Exception {
        results.clear();
        numPackets = Integer.parseInt(fieldPackets.getText());
        generateRandomData();

        int size = testByteStream();
        Result result = new Result();
        result.name = "ByteStream";
        result.size = size;
        results.add(result);

        size = testProtoBuf();
        result = new Result();
        result.name = "ProtoBuf";
        result.size = size;
        results.add(result);

        size = testASN1();
        result = new Result();
        result.name = "ASN1";
        result.size = size;
        results.add(result);

        size = testJavaSerialization();
        result = new Result();
        result.name = "JavaSerialization";
        result.size = size;
        results.add(result);
    }
}
