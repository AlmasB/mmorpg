package uk.ac.brighton.uni.ab607.mmorpg.common.item;

import uk.ac.brighton.uni.ab607.mmorpg.common.Stat;

public class Essence implements java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 4396825140388400662L;

    public final String name, id;
    public final Stat stat;
    public final int bonus;

    /*package-private*/ Essence(String id, String name, Stat stat, int bonus) {
        this.id = id;
        this.name = name;
        this.stat = stat;
        this.bonus = bonus;
    }

    /*package-private*/ Essence(Essence copy) {
        this.id = copy.id;
        this.name = copy.name;
        this.stat = copy.stat;
        this.bonus = copy.bonus;
    }

    @Override
    public String toString() {
        return "ID: " + id + "\n"
                + "The essence of " + name + "\n"
                + stat + " +" + bonus;
    }

    public String toStringHTML() {
        return "The essence of " + name + "<br/>\n"
                + "<font color=green>" + stat + " +" + bonus + "</font>";
    }
}
