package apps.sarafrika.entity;

import apps.sarafrika.dto.UserSession;
import apps.sarafrika.enums.SessionEventType;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "session_events")
public class SessionEvent extends BaseEntity {

    @Column(name = "session_id")
    public String sessionId;

    @Column(name = "phone_number")
    public String phoneNumber;

    @Column(name = "event_type")
    public SessionEventType eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "session_data_snapshot")
    public UserSession sessionDataSnapshot;

    @Column(name = "duration_seconds")
    public Integer durationSeconds;

    @Column(name = "redis_operation_time_ms")
    public Integer redisOperationTimeMs;

    @Column(name = "network_code")
    public String networkCode;

    @Column(name = "service_code")
    public String serviceCode;

    public SessionEvent() {}

    public SessionEvent(String sessionId, String phoneNumber, SessionEventType eventType) {
        this.sessionId = sessionId;
        this.phoneNumber = phoneNumber;
        this.eventType = eventType;
    }
}