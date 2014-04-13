package uk.ac.brighton.uni.ab607.mmorpg.common.ai;

public class AgentBehaviour implements java.io.Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -2595967627822210051L;
    public AgentType type;
    public AgentGoal currentGoal;
    public AgentGoalTarget currentTarget;

    /**
     *
     * @param type
     * @param target
     *               can be null
     */
    public AgentBehaviour(AgentType type, AgentGoalTarget target) {
        this.type = type;
        currentGoal = type.initialGoal;
        currentTarget = target;
    }

    public void setGoal(AgentGoal goal) {
        currentGoal = goal;
    }

    public void setTarget(AgentGoalTarget target) {
        currentTarget = target;
    }
}
