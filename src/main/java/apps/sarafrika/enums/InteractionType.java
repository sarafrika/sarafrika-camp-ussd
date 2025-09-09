package apps.sarafrika.enums;

public enum InteractionType {
    INPUT("INPUT"),
    NAVIGATION("NAVIGATION"),
    VALIDATION_ERROR("VALIDATION_ERROR"),
    SESSION_START("SESSION_START"),
    SESSION_END("SESSION_END");

    private final String value;

    InteractionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static InteractionType fromValue(String value) {
        for (InteractionType type : InteractionType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid InteractionType value: " + value);
    }
}