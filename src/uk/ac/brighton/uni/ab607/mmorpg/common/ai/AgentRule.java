package uk.ac.brighton.uni.ab607.mmorpg.common.ai;

public abstract class AgentRule {

    public AgentType type;
    public AgentGoal goal;

    public AgentRule(AgentType type, AgentGoal goal) {
        this.type = type;
        this.goal = goal;
    }

    public abstract void execute(EnemyAgent agent, AgentGoalTarget target);

    public boolean matches(AgentType type, AgentGoal goal) {
        return this.type == type && this.goal == goal;
    }
}
