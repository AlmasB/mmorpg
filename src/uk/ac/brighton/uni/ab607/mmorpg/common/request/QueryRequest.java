package uk.ac.brighton.uni.ab607.mmorpg.common.request;

/**
 * QueryRequest is sent by a client in order to request
 * an action that doesn't affect the gameplay
 * i.e. check account details info, login player ...
 * as opposed to ActionRequest which contains
 * in-game commands
 * 
 * @author Almas Baimagambetov
 *
 */
public class QueryRequest implements java.io.Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 5973640579988158356L;

    public enum Query {
        CHECK, LOGIN, LOGOFF
    }
    
    public final Query query;
    public final String value1, value2;
    
    public QueryRequest(Query query, String... data) {
        this.query = query;
        value1 = data.length > 0 ? data[0] : "";
        value2 = data.length > 1 ? data[1] : "";
    }
}
