package uk.ac.brighton.uni.ab607.mmorpg.test.data;

import java.io.Serializable;

public class DataCharacter implements Serializable {
    private static final long serialVersionUID = 1L;

    public int xy;
    public int sprite;
    public byte placeDir;
    public int ids;

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof DataCharacter)) {
            return false;
        }

        DataCharacter other = (DataCharacter) o;
        return xy == other.xy && sprite == other.sprite && placeDir == other.placeDir && ids == other.ids;
    }
}
