package apps.sarafrika.enums;

public enum NavigationType {
    FORWARD("FORWARD"),
    BACK("BACK"),
    DIRECT("DIRECT"),
    EXIT("EXIT"),
    PAGINATION("PAGINATION");

    private final String value;

    NavigationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static NavigationType fromValue(String value) {
        for (NavigationType type : NavigationType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid NavigationType value: " + value);
    }
}