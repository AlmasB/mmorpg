package uk.ac.brighton.uni.ab607.mmorpg.test.cases;

import java.nio.ByteBuffer;

import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import uk.ac.brighton.uni.ab607.mmorpg.test.OrionTestBase;

public class BitPatternTest extends OrionTestBase {

    private byte[] byteStream, protoBuf, asn1, javaSerialization;

    @Override
    public Parent getResultsContent() {
        Font font = Font.font("Courier New", 20);

        Text original = new Text("Dec: " + randomData[0].xy + "," + randomData[0].sprite + "," + randomData[0].placeDir + "," + randomData[0].ids);
        original.setFont(font);

        int size = 4 + 4 + 1 + 4;

        Text byteView = new Text("Bytes:      " + getString(
                ByteBuffer.allocate(size)
                .putInt(randomData[0].xy)
                .putInt(randomData[0].sprite)
                .put(randomData[0].placeDir)
                .putInt(randomData[0].ids).array()));
        byteView.setFont(font);

        Text byteStreamT = new Text("ByteStream: " + getString(byteStream));
        byteStreamT.setFont(font);

        Text proto = new Text("ProtoBuf:   " + getString(protoBuf));
        proto.setFont(font);

        Text asn = new Text("ASN.1:      " + getString(asn1));
        asn.setFont(font);

        Text js = new Text("JavaSerial: " + getString(javaSerialization));
        js.setWrappingWidth(400);
        js.setFont(font);

        Text jss = new Text("JavaSerial(String): " + new String(javaSerialization));
        jss.setWrappingWidth(400);
        jss.setFont(font);

        HBox hbox = new HBox(20, js, jss);

        VBox vbox = new VBox(20, original, byteView,
                byteStreamT,
                proto,
                asn,
                hbox);
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

        randomData[0].xy = 1;
        randomData[0].sprite = 2;
        randomData[0].placeDir = 9;
        randomData[0].ids = 3;

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
