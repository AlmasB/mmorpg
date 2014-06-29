package uk.ac.brighton.uni.ab607.mmorpg.common.request;

import uk.ac.brighton.uni.ab607.mmorpg.common.request.QueryRequest.Query;

public class ServerResponse implements java.io.Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -2898202097915404801L;
    
    public final Query query;
    public final boolean ok;
    public final String message, data;
    public final int value1, value2;
    
    public ServerResponse(Query query, boolean ok, String message, String data, int... values) {
        this.query = query;
        this.ok = ok;
        this.message = message;
        this.data = data;
        value1 = values.length > 0 ? values[0] : 0;
        value2 = values.length > 1 ? values[1] : 0;
    }
}
