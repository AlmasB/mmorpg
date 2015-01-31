package uk.ac.brighton.uni.ab607.mmorpg.test.cases;

import java.nio.ByteBuffer;

import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import uk.ac.brighton.uni.ab607.mmorpg.test.OrionTestBase;

public class BitPatternTest extends OrionTestBase {

    private byte[] byteStream, protoBuf, asn1, javaSerialization;

    @Override
    public Parent getResultsContent() {
        Text original = new Text(randomData[0].xy + "," + randomData[0].sprite + "," + randomData[0].placeDir + "," + randomData[0].ids);

        int size = 4 + 4 + 1 + 4;
        byte[] bytes = new byte[size];

        Text byteView = new Text(getString(
                ByteBuffer.allocate(size)
                .putInt(randomData[0].xy)
                .putInt(randomData[0].sprite)
                .put(randomData[0].placeDir)
                .putInt(randomData[0].ids).array()));

        VBox vbox = new VBox(20, original, byteView,
                new Text(getString(byteStream)),
                new Text(getString(protoBuf)),
                new Text(getString(asn1)),
                new Text(getString(javaSerialization)));
        return vbox;
    }

    @Override
    public Parent getTestControls() {
        return new Pane();
    }

    @Override
    protected void run() throws Exception {
        numPackets = 1;
        generateRandomData();

        randomData[0].xy = 10000;
        randomData[0].sprite = 20000;
        randomData[0].placeDir = 9;
        randomData[0].ids = 30000;

        byteStream = toByteStream(randomData[0]);
        protoBuf = toProtoBuf(randomData[0]);
        asn1 = toASN1(randomData[0]);
        javaSerialization = toJavaSerialization(randomData[0]);
    }

    private String getString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(b + ",");
        }
        return sb.toString();
    }
}
