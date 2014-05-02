package uk.ac.brighton.uni.ab607.mmorpg.common.ai;

public class AgentBehaviour implements java.io.Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -2595967627822210051L;

    public enum AgentType {
        GUARD, SCOUT, ASSASSIN
    }

    public enum AgentGoal {
        FIND_OBJECT, KILL_OBJECT, GUARD_OBJECT
    }

    public enum AgentMode {
        AGGRESSIVE, PASSIVE, PATROL
    }

    public AgentType type;
    public AgentGoal currentGoal;
    public AgentMode currentMode;
    public AgentGoalTarget currentTarget = null;

    public AgentBehaviour(AgentType type, AgentGoal goal, AgentMode mode) {
        this.type = type;
        currentGoal = goal;
        currentMode = mode;
    }

    public void setGoal(AgentGoal goal) {
        currentGoal = goal;
    }

    public void setMode(AgentMode mode) {
        currentMode = mode;
    }

    public void setTarget(AgentGoalTarget target) {
        currentTarget = target;
    }
}
