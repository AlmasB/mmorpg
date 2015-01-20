package uk.ac.brighton.uni.ab607.mmorpg.test;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import uk.ac.brighton.uni.ab607.mmorpg.test.GameCharacterProtoBuf.GameCharProtoBuf;
import uk.ac.brighton.uni.ab607.mmorpg.test.GameMessageProtoBuf.MessageProtoBuf;
import uk.ac.brighton.uni.ab607.mmorpg.test.asn1.AsnOutputStream;
import uk.ac.brighton.uni.ab607.mmorpg.test.asn1.Tag;
import uk.ac.brighton.uni.ab607.mmorpg.test.data.DataCharacter;
import uk.ac.brighton.uni.ab607.mmorpg.test.data.DataMessage;
import javafx.scene.Parent;

import com.almasb.common.test.Test;

public abstract class OrionTestBase extends Test {

    protected static Random rand = new Random();

    protected DataCharacter[] randomData;
    protected DataMessage[] randomData2;

    protected int numPackets = 0;

    protected void generateRandomData() {
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

    protected int testProtoBuf() throws Exception {
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

    protected int testByteStream() throws Exception {
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

    protected int testASN1() throws Exception {
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

    protected int testJavaSerialization() throws Exception {
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


    public abstract Parent getResultsContent();
    public abstract Parent getTestControls();

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
