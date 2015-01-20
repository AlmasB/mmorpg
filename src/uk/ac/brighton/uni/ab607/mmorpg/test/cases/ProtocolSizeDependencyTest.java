package uk.ac.brighton.uni.ab607.mmorpg.test.cases;

import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;
import uk.ac.brighton.uni.ab607.mmorpg.test.OrionTestBase;
import uk.ac.brighton.uni.ab607.mmorpg.test.Result;

public class ProtocolSizeDependencyTest extends OrionTestBase {

    private ArrayList<Result> results = new ArrayList<Result>();

    @SuppressWarnings("unchecked")
    @Override
    public Parent getResultsContent() {

        LineChart chart;
        NumberAxis xAxis = new NumberAxis("Number of Packets", 1, 100000, 10000);
        NumberAxis yAxis = new NumberAxis("Size (in bytes)", 0, 16000000, 1000000);

        ObservableList<XYChart.Series<Double, Double> > lineChartData = FXCollections.observableArrayList();
        for (int i = 0; i < 4; i++) {
            lineChartData.add(new LineChart.Series<Double, Double>(results.get(i).name, FXCollections.observableArrayList(
                    new XYChart.Data<>(1.0, results.get(i).size),
                    new XYChart.Data<>(10000.0, results.get(i+4).size),
                    new XYChart.Data<>(30000.0, results.get(i+8).size),
                    new XYChart.Data<>(50000.0, results.get(i+12).size),
                    new XYChart.Data<>(90000.0, results.get(i+16).size)
                    )));
        }

        chart = new LineChart(xAxis, yAxis, lineChartData);
        return chart;
    }

    @Override
    public Parent getTestControls() {
        return new Pane();
    }

    private void runWith(int packets) throws Exception {
        numPackets = packets;
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

    @Override
    protected void run() throws Exception {
        results.clear();
        runWith(1);
        runWith(10000);
        runWith(30000);
        runWith(50000);
        runWith(90000);
    }
}
