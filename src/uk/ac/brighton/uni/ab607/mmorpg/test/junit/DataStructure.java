package uk.ac.brighton.uni.ab607.mmorpg.test.junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.* ;

import uk.ac.brighton.uni.ab607.mmorpg.test.data.DataCharacter;
import static org.junit.Assert.* ;

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
}
