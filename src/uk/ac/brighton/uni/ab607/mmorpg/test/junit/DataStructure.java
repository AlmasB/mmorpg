package uk.ac.brighton.uni.ab607.mmorpg.test.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import org.junit.Test;

import uk.ac.brighton.uni.ab607.mmorpg.test.GameCharacterProtoBuf.GameCharProtoBuf;
import uk.ac.brighton.uni.ab607.mmorpg.test.asn1.AsnInputStream;
import uk.ac.brighton.uni.ab607.mmorpg.test.asn1.AsnOutputStream;
import uk.ac.brighton.uni.ab607.mmorpg.test.asn1.Tag;
import uk.ac.brighton.uni.ab607.mmorpg.test.data.DataCharacter;

public class DataStructure {

    @Test
    public void testJavaSerialization() throws Exception {
        DataCharacter data = new DataCharacter();
        data.ids = 8002;
        data.placeDir = 122;
        data.sprite = 9000;
        data.xy = 10030;

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(output);

        oos.writeObject(data);
        oos.close();

        assertTrue("Serialized size <= 0", output.toByteArray().length > 0);

        ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(input);

        DataCharacter recreated = (DataCharacter) ois.readObject();

        assertNotNull("Reassembled object is null", recreated);
        assertEquals("Original and new object are different", data, recreated);
    }

    @Test
    public void testASN1() throws Exception {
        DataCharacter data = new DataCharacter();
        data.ids = 800244432;
        data.placeDir = 122;
        data.sprite = 912000;
        data.xy = 34530;

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        AsnOutputStream out = new AsnOutputStream();

        out.writeInteger(data.xy);
        out.writeInteger(data.sprite);
        out.write(data.placeDir);
        out.writeInteger(data.ids);
        //out.writeStringUTF8("Hello");
        out.close();

        output.write(out.toByteArray());

        //System.out.println(Arrays.toString(output.toByteArray()));

        AsnInputStream in = new AsnInputStream(output.toByteArray());

        DataCharacter recreated = new DataCharacter();
        in.read();
        recreated.xy = (int) in.readInteger();
        in.read();
        recreated.sprite = (int) in.readInteger();
        recreated.placeDir = (byte) in.read();
        in.read();
        recreated.ids = (int) in.readInteger();

        in.close();

        assertEquals("Original and new object are different", data, recreated);
    }

    @Test
    public void testProtoBuf() throws Exception {
        DataCharacter data = new DataCharacter();
        data.ids = 800244432;
        data.placeDir = 122;
        data.sprite = 912000;
        data.xy = 34530;

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        GameCharProtoBuf.Builder playerBuilder = GameCharProtoBuf.newBuilder();
        playerBuilder.setXy(data.xy);
        playerBuilder.setSpriteID(data.sprite);
        playerBuilder.setPlacedir(data.placeDir);
        playerBuilder.setIds(data.ids);

        GameCharProtoBuf player = playerBuilder.build();
        output.write(player.toByteArray());



        GameCharProtoBuf.Builder playerBuilder2 = GameCharProtoBuf.newBuilder();
        playerBuilder2.mergeFrom(output.toByteArray());

        GameCharProtoBuf player2 = playerBuilder2.build();

        DataCharacter recreated = new DataCharacter();
        recreated.xy = player2.getXy();
        recreated.placeDir = (byte) player2.getPlacedir();
        recreated.sprite = player2.getSpriteID();
        recreated.ids = player2.getIds();

        assertEquals("Original and new object are different", data, recreated);
    }
}
