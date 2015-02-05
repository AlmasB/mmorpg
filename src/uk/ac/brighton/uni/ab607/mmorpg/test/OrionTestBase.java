package uk.ac.brighton.uni.ab607.mmorpg.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import javafx.scene.Parent;
import uk.ac.brighton.uni.ab607.mmorpg.test.GameCharacterProtoBuf.GameCharProtoBuf;
import uk.ac.brighton.uni.ab607.mmorpg.test.GameMessageProtoBuf.MessageProtoBuf;
import uk.ac.brighton.uni.ab607.mmorpg.test.asn1.AsnInputStream;
import uk.ac.brighton.uni.ab607.mmorpg.test.asn1.AsnOutputStream;
import uk.ac.brighton.uni.ab607.mmorpg.test.asn1.Tag;
import uk.ac.brighton.uni.ab607.mmorpg.test.data.DataCharacter;
import uk.ac.brighton.uni.ab607.mmorpg.test.data.DataMessage;

import com.almasb.common.test.Test;

public abstract class OrionTestBase extends Test {

    // TODO: read and write

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
            randomData[i].placeDir = (byte)rand.nextInt(16);
            randomData[i].ids = rand.nextInt();

            randomData2[i] = new DataMessage();
            randomData2[i].xy = rand.nextInt();
            randomData2[i].type = (byte) rand.nextInt();

            byte[] tmp = new byte[59];
            //byte[] tmp = new byte[rand.nextInt(60)];
            rand.nextBytes(tmp);
            randomData2[i].text = new String(tmp);
        }
    }

    protected byte[] testProtoBuf() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        for (DataCharacter data : randomData) {
            GameCharProtoBuf.Builder playerBuilder = GameCharProtoBuf.newBuilder();
            playerBuilder.setXy(data.xy);
            playerBuilder.setSpriteID(data.sprite);
            playerBuilder.setPlacedir(data.placeDir);
            playerBuilder.setIds(data.ids);

            GameCharProtoBuf player = playerBuilder.build();
            output.write(player.toByteArray());

            GameCharProtoBuf.Builder playerBuilder2 = GameCharProtoBuf.newBuilder();
            playerBuilder2.mergeFrom(player.toByteArray());
        }

        for (DataMessage data : randomData2) {
            MessageProtoBuf.Builder messageBuilder = MessageProtoBuf.newBuilder();
            messageBuilder.setXy(data.xy);
            messageBuilder.setType(data.type);
            messageBuilder.setText(data.text);

            MessageProtoBuf message = messageBuilder.build();
            output.write(message.toByteArray());

            MessageProtoBuf.Builder messageBuilder2 = MessageProtoBuf.newBuilder();
            messageBuilder2.mergeFrom(message.toByteArray());
        }

        return output.toByteArray();
    }

    protected byte[] testByteStream() throws Exception {
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

        return output.toByteArray();
    }

    protected byte[] testASN1() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        AsnOutputStream out = new AsnOutputStream();

        for (DataCharacter data : randomData) {
            out.writeInteger(data.xy);
            out.writeInteger(data.sprite);
            out.write(data.placeDir);
            out.writeInteger(data.ids);
        }

        for (DataMessage data : randomData2) {
            out.writeInteger(data.xy);
            out.write(data.type);
            out.writeStringUTF8(data.text);
        }
        out.close();

        output.write(out.toByteArray());

        AsnInputStream in = new AsnInputStream(output.toByteArray());

        for (DataCharacter data : randomData) {
            DataCharacter recreated = new DataCharacter();
            in.read();
            recreated.xy = (int) in.readInteger();
            in.read();
            recreated.sprite = (int) in.readInteger();
            recreated.placeDir = (byte) in.read();
            in.read();
            recreated.ids = (int) in.readInteger();
        }

        for (DataMessage data : randomData2) {
            DataCharacter recreated = new DataCharacter();
            in.read();
            recreated.xy = (int) in.readInteger();
            recreated.placeDir = (byte) in.read();
            in.read();
            in.readUTF8String();
        }

        in.close();

        return output.toByteArray();
    }

    protected byte[] testJavaSerialization() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(output);
        for (DataCharacter data : randomData) {
            oos.writeObject(data);
        }
        for (DataMessage data : randomData2) {
            oos.writeObject(data);
        }

        oos.close();

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(output.toByteArray()));
        for (DataCharacter data : randomData) {
            ois.readObject();
        }
        for (DataMessage data : randomData2) {
            ois.readObject();
        }

        return output.toByteArray();
    }

    protected byte[] toByteStream(DataCharacter data) {
        GameCharacterByteStream player = new GameCharacterByteStream();
        player.setXY(data.xy);
        player.setSpriteID(data.sprite);
        player.setPlaceDir(data.placeDir);
        player.setIDs(data.ids);

        return player.toByteArray();
    }

    protected byte[] toProtoBuf(DataCharacter data) {
        GameCharProtoBuf.Builder playerBuilder = GameCharProtoBuf.newBuilder();
        playerBuilder.setXy(data.xy);
        playerBuilder.setSpriteID(data.sprite);
        playerBuilder.setPlacedir(data.placeDir);
        playerBuilder.setIds(data.ids);

        GameCharProtoBuf player = playerBuilder.build();
        return player.toByteArray();
    }

    protected byte[] toJavaSerialization(DataCharacter data) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(output);
        oos.writeObject(data);

        oos.close();
        return output.toByteArray();
    }

    protected byte[] toASN1(DataCharacter data) throws Exception {
        AsnOutputStream out = new AsnOutputStream();

        out.writeInteger(data.xy);
        out.writeInteger(data.sprite);
        out.write(data.placeDir);
        out.writeInteger(data.ids);
        return out.toByteArray();
    }

    public abstract Parent getResultsContent();
    public abstract Parent getTestControls();

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
