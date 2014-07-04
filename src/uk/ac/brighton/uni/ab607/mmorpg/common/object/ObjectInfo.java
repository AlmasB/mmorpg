package uk.ac.brighton.uni.ab607.mmorpg.common.object;

public class ObjectInfo implements java.io.Serializable {
    private static final long serialVersionUID = -6239085741159928102L;
    
    private String id, name, description;
    
    public ObjectInfo(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    
    public String getID() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
}
