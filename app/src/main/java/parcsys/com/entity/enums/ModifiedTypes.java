package parcsys.com.entity.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Иван on 15.04.2015.
 */
public enum ModifiedTypes {
    CREATE("C"),
    DELETE("D"),
    MODIFY("M");

    private final String id;

    private static final Map<String, ModifiedTypes> data = new HashMap<String, ModifiedTypes>();

    static {
        for(ModifiedTypes type : values()) {
            data.put(type.id, type);
        }
    }

    ModifiedTypes(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static ModifiedTypes getById(String id) {
        return data.get(id);
    }
}
