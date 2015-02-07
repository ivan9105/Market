package parcsys.com.entity.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Иван on 25.01.2015.
 */
public enum SoldDestinationType {
    TECHNOLOGY("Technology"),
    PRODUCTS("Products"),
    HOUSEHOLD_GOODS("Household goods");

    private final String id;
    private static final Map<String, SoldDestinationType> data = new HashMap<String, SoldDestinationType>();
    static {
        for (SoldDestinationType type : values()) {
            data.put(type.getId(), type);
        }
    }

    private SoldDestinationType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static SoldDestinationType getById(String id) {
        return data.get(id);
    }
}
