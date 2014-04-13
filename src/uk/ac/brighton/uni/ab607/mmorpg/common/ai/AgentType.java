package uk.ac.brighton.uni.ab607.mmorpg.common.ai;

public enum AgentType {
    GUARD(AgentMode.PATROL, AgentGoal.GUARD_CHEST),
    SCOUT(AgentMode.PASSIVE, AgentGoal.FIND_PLAYER),
    ASSASSIN(AgentMode.AGGRESSIVE, AgentGoal.KILL_PLAYER);

    public final AgentMode mode;
    public final AgentGoal initialGoal;
    private AgentType(AgentMode mode, AgentGoal goal) {
        this.mode = mode;
        this.initialGoal = goal;
    }
}
