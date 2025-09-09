package apps.sarafrika.entity;

import apps.sarafrika.enums.InteractionType;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "user_interactions")
public class UserInteraction extends BaseEntity {

    @Column(name = "session_id")
    public String sessionId;

    @Column(name = "phone_number")
    public String phoneNumber;

    @Column(name = "interaction_type")
    public InteractionType interactionType;

    @Column(name = "current_state")
    public String currentState;

    @Column(name = "previous_state")
    public String previousState;

    @Column(name = "user_input")
    public String userInput;

    @Column(name = "response_generated")
    public String responseGenerated;

    @Column(name = "processing_time_ms")
    public Integer processingTimeMs;

    @Column(name = "error_message")
    public String errorMessage;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata")
    public Map<String, Object> metadata = new HashMap<>();

    public UserInteraction() {}

    public UserInteraction(String sessionId, String phoneNumber, InteractionType interactionType) {
        this.sessionId = sessionId;
        this.phoneNumber = phoneNumber;
        this.interactionType = interactionType;
    }
}