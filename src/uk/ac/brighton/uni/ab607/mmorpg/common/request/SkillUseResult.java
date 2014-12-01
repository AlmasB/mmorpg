package uk.ac.brighton.uni.ab607.mmorpg.common.request;

public class SkillUseResult implements java.io.Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 606462172238372852L;

    public static SkillUseResult DEFAULT_FALSE = new SkillUseResult(false, "");
    public static SkillUseResult DEFAULT_TRUE = new SkillUseResult(true, "");

    public final String data;
    public final boolean success;

    /**
     * Hidden ctor
     *
     */
    private SkillUseResult(boolean success, String data) {
        this.success = success;
        this.data = data;
    }

    public SkillUseResult(String data) {
        this(true, data);
    }

    public SkillUseResult(int dmg) {
        this(true, dmg + "");
    }
}
