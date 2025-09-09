package apps.sarafrika.enums;

public enum SessionEventType {
    CREATED("CREATED"),
    EXTENDED("EXTENDED"),
    EXPIRED("EXPIRED"),
    TERMINATED("TERMINATED"),
    DATA_UPDATED("DATA_UPDATED");

    private final String value;

    SessionEventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static SessionEventType fromValue(String value) {
        for (SessionEventType type : SessionEventType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid SessionEventType value: " + value);
    }
}