package uk.ac.brighton.uni.ab607.mmorpg.server;

import uk.ac.brighton.uni.ab607.mmorpg.common.Player;
import uk.ac.brighton.uni.ab607.mmorpg.common.request.ActionRequest;

public interface ServerAction {

    public void execute(Player p, ActionRequest req) throws BadActionRequestException;
}
