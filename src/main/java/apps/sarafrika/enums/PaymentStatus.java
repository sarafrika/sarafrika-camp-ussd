package apps.sarafrika.enums;

public enum PaymentStatus {
    PENDING("PENDING"),
    INITIATED("INITIATED"),
    SUCCESS("SUCCESS"),
    FAILED("FAILED"),
    CANCELLED("CANCELLED"),
    REFUNDED("REFUNDED");

    private final String value;

    PaymentStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PaymentStatus fromValue(String value) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid PaymentStatus value: " + value);
    }
}