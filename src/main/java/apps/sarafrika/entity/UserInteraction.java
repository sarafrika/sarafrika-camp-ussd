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

    @Column(name = "action")
    public InteractionType interactionType;

    @Column(name = "menu_level")
    public String currentState;

    @Column(name = "user_input")
    public String userInput;

    @Column(name = "response_sent")
    public String responseGenerated;

    public UserInteraction() {}

    public UserInteraction(String sessionId, String phoneNumber, InteractionType interactionType) {
        this.sessionId = sessionId;
        this.phoneNumber = phoneNumber;
        this.interactionType = interactionType;
    }
}