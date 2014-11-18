package uk.ac.brighton.uni.ab607.mmorpg.client.fx;

import java.util.ArrayList;

import uk.ac.brighton.uni.ab607.mmorpg.common.request.ActionRequest;

import javafx.stage.Stage;

public abstract class UIBaseWindow extends Stage {

    private ArrayList<ActionRequest> requests = new ArrayList<ActionRequest>();

    public void addActionRequest(ActionRequest action) {
        requests.add(action);
    }

    public ActionRequest[] clearPendingActionRequests() {
        ActionRequest[] res = new ActionRequest[requests.size()];
        requests.toArray(res);
        requests.clear();
        return res;
    }
}
