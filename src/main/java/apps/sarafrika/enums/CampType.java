package apps.sarafrika.enums;

public enum CampType {
    HALF_DAY("Half Day: 9:00am - 13:00pm"),
    BOOT_CAMP("Boot Camp: Boarding");

    private final String displayName;

    CampType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static CampType fromString(String text) {
        for (CampType type : CampType.values()) {
            if (type.name().equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant for: " + text);
    }
}