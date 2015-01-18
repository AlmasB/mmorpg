package uk.ac.brighton.uni.ab607.mmorpg.test.data;

import java.io.Serializable;

public class DataMessage implements Serializable {
    private static final long serialVersionUID = 2L;

    public int xy;
    public byte type;
    public String text;

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof DataMessage)) {
            return false;
        }

        DataMessage other = (DataMessage) o;
        return xy == other.xy && type == other.type && text != null && text.equals(other.text);
    }
}
