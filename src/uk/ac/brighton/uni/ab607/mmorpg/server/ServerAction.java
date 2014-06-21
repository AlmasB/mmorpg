package uk.ac.brighton.uni.ab607.mmorpg.server;

import uk.ac.brighton.uni.ab607.mmorpg.common.ActionRequest;
import uk.ac.brighton.uni.ab607.mmorpg.common.Player;

public interface ServerAction {

    public void execute(Player p, ActionRequest req) throws BadActionRequestException;
}
