package uk.ac.brighton.uni.ab607.mmorpg.test.junit;

import org.junit.* ;
import static org.junit.Assert.* ;

/**
 * Proves that the last 4 bits of a byte can hold two values
 * if the values are in range [0..4)
 *
 * The same can be done with 4 bits and 4 bits since a byte is 8 bits
 * and with other types
 *
 * [xx, xx, xx, xx]
 * so [xx, xx, 11, 11] is 3 and 3
 *
 * @author Almas Baimagambetov (ab607@uni.brighton.ac.uk)
 * @version 1.0
 *
 */
public class BitCondensing {
    @Test
    public void test() {
        // test byte
        for (byte i = 0; i < 4; i++) {
            for (byte j = 0; j < 4; j++) {
                // & 0xFF to demote int to byte
                // because shifting is done on ints
                byte b = (byte)((i << 2 | j) & 0xFF);

                assertTrue(decodeA(b) == i);
                assertTrue(decodeB(b) == j);
            }
        }

        // test 2 ints (up to 65535 uint) in 1 int
        for (int i = 0; i < 65536; i++) {
            for (int j = 0; j < 65536; j++) {
                int enc = i << 16 | j;

                assertTrue(decodeA(enc) == i);
                assertTrue(decodeB(enc) == j);
            }
        }
    }

    private byte decodeA(byte b) {
        return (byte)(b >> 2 & 0b11);
    }

    private byte decodeB(byte b) {
        return (byte)(b & 0b11);
    }

    private int decodeA(int i) {
        return i >> 16 & 0xFFFF;
    }

    private int decodeB(int i) {
        return i & 0xFFFF;
    }
}
