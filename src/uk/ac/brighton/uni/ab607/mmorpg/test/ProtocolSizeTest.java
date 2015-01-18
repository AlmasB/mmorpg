package uk.ac.brighton.uni.ab607.mmorpg.test;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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
import uk.ac.brighton.uni.ab607.mmorpg.test.GameCharacterProtoBuf.GameCharProtoBuf;
import uk.ac.brighton.uni.ab607.mmorpg.test.GameMessageProtoBuf.MessageProtoBuf;
import uk.ac.brighton.uni.ab607.mmorpg.test.asn1.AsnOutputStream;
import uk.ac.brighton.uni.ab607.mmorpg.test.asn1.Tag;
import uk.ac.brighton.uni.ab607.mmorpg.test.data.DataCharacter;
import uk.ac.brighton.uni.ab607.mmorpg.test.data.DataMessage;

public class ProtocolSizeTest extends OrionTestBase {

    private static Random rand = new Random();

    private DataCharacter[] randomData;
    private DataMessage[] randomData2;

    private int numPackets = 0;

    private void generateRandomData() {
        randomData = new DataCharacter[numPackets];
        randomData2 = new DataMessage[numPackets];

        for (int i = 0; i < numPackets; i++) {
            randomData[i] = new DataCharacter();
            randomData[i].xy = rand.nextInt();
            randomData[i].sprite = rand.nextInt();
            randomData[i].placeDir = (byte)rand.nextInt();
            randomData[i].ids = rand.nextInt();

            randomData2[i] = new DataMessage();
            randomData2[i].xy = rand.nextInt();
            randomData2[i].type = (byte) rand.nextInt();

            byte[] tmp = new byte[59];
            rand.nextBytes(tmp);
            randomData2[i].text = new String(tmp);
        }
    }

    private int testProtoBuf() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        for (DataCharacter data : randomData) {
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

        return output.toByteArray().length;
    }

    private int testByteStream() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for (DataCharacter data : randomData) {
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

        return output.toByteArray().length;
    }

    private int testASN1() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        AsnOutputStream out = new AsnOutputStream();

        for (DataCharacter data : randomData) {
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

        return output.toByteArray().length;
    }

    private int testJavaSerialization() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(output);
        for (DataCharacter data : randomData) {
            oos.writeObject(data);
        }
        for (DataMessage data : randomData2) {
            oos.writeObject(data);
        }

        oos.close();
        return output.toByteArray().length;
    }

    private ArrayList<Result> results = new ArrayList<Result>();
    private TextField fieldPackets = new TextField();

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
