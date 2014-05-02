package uk.ac.brighton.uni.ab607.mmorpg.common.ai;

import uk.ac.brighton.uni.ab607.mmorpg.common.ai.AgentBehaviour.AgentGoal;
import uk.ac.brighton.uni.ab607.mmorpg.common.ai.AgentBehaviour.AgentMode;
import uk.ac.brighton.uni.ab607.mmorpg.common.ai.AgentBehaviour.AgentType;
import uk.ac.brighton.uni.ab607.mmorpg.common.object.Enemy;

public abstract class AgentRule {

    public AgentType type;
    public AgentGoal goal;
    public AgentMode mode;

    public AgentRule(AgentType type, AgentGoal goal, AgentMode mode) {
        this.type = type;
        this.goal = goal;
        this.mode = mode;
    }

    public abstract void execute(Enemy agent, AgentGoalTarget target);

    public boolean matches(AgentType type, AgentGoal goal, AgentMode mode) {
        return this.type == type && this.goal == goal && this.mode == mode;
    }
}
