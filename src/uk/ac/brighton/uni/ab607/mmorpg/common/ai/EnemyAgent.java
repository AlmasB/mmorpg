package uk.ac.brighton.uni.ab607.mmorpg.common.ai;

public interface EnemyAgent {

    public void proceedToGoal();    // perhaps pass some info?
    public AgentMode getMode();
    public AgentGoal getGoal();

    public void patrol(AgentGoalTarget target);
    public void search(AgentGoalTarget target);
    public void attackAI(AgentGoalTarget target);
    public boolean canSee(AgentGoalTarget target);
}
