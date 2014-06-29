package uk.ac.brighton.uni.ab607.mmorpg.common;

public class AttributeInfo {

    public int str = 1, vit = 1, dex = 1, 
            agi = 1, int_ = 1, wis = 1,
            wil = 1, per = 1, luc = 1;
    
    public AttributeInfo str(final int value) {
        str = value;
        return this;
    }
    
    public AttributeInfo vit(final int value) {
        vit = value;
        return this;
    }
    
    public AttributeInfo dex(final int value) {
        dex = value;
        return this;
    }
    
    public AttributeInfo agi(final int value) {
        agi = value;
        return this;
    }
    
    public AttributeInfo int_(final int value) {
        int_ = value;
        return this;
    }
    
    public AttributeInfo wis(final int value) {
        wis = value;
        return this;
    }
    
    public AttributeInfo wil(final int value) {
        wil = value;
        return this;
    }
    
    public AttributeInfo per(final int value) {
        per = value;
        return this;
    }
    
    public AttributeInfo luc(final int value) {
        luc = value;
        return this;
    }
}
