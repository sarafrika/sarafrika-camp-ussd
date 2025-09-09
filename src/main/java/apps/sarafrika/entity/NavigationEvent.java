package apps.sarafrika.entity;

import apps.sarafrika.enums.NavigationType;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "navigation_events")
public class NavigationEvent extends BaseEntity {

    @Column(name = "session_id")
    public String sessionId;

    @Column(name = "phone_number")
    public String phoneNumber;

    @Column(name = "from_state")
    public String fromState;

    @Column(name = "to_state")
    public String toState;

    @Column(name = "navigation_type")
    public NavigationType navigationType;

    @Column(name = "user_input")
    public String userInput;

    @Column(name = "time_on_previous_page_ms")
    public Integer timeOnPreviousPageMs;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "page_data")
    public Map<String, Object> pageData = new HashMap<>();

    public NavigationEvent() {}

    public NavigationEvent(String sessionId, String phoneNumber, String fromState, String toState, NavigationType navigationType) {
        this.sessionId = sessionId;
        this.phoneNumber = phoneNumber;
        this.fromState = fromState;
        this.toState = toState;
        this.navigationType = navigationType;
    }
}