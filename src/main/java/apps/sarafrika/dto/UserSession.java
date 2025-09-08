package apps.sarafrika.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import java.util.*;

@Schema(
    description = "User session data stored in Redis for USSD state management",
    example = """
        {
            "session_id": "ATUid_abcd1234567890",
            "phone_number": "+254712345678",
            "state_history": ["main_menu", "select_category", "select_camp"],
            "data": {
                "selectedCategory": "Adventure",
                "selectedCampId": "1",
                "participantName": "John Doe",
                "participantAge": 16
            },
            "pagination_offset": 0,
            "current_menu_items": ["1", "2", "3"]
        }
        """
)
public class UserSession {
    
    @JsonProperty("session_id")
    @Schema(
        description = "Unique session identifier from Africa's Talking",
        example = "ATUid_abcd1234567890",
        required = true
    )
    public String sessionId;
    
    @JsonProperty("phone_number")
    @Schema(
        description = "User's phone number in international format",
        example = "+254712345678",
        required = true
    )
    public String phoneNumber;
    
    @JsonProperty("state_history")
    @Schema(
        description = "Stack of menu states visited during the session (last item is current state)",
        example = "[\"main_menu\", \"select_category\", \"select_camp\"]"
    )
    public List<String> stateHistory = new ArrayList<>();
    
    @JsonProperty("data")
    @Schema(
        description = "Key-value store for user-entered data during the session",
        example = "{\"selectedCategory\": \"Adventure\", \"participantName\": \"John Doe\"}"
    )
    public Map<String, Object> data = new HashMap<>();
    
    @JsonProperty("pagination_offset")
    @Schema(
        description = "Current offset for paginated lists (used for camps, bookings)",
        example = "0",
        defaultValue = "0"
    )
    public int paginationOffset = 0;
    
    @JsonProperty("current_menu_items")
    @Schema(
        description = "List of item IDs currently displayed on the menu (for selection validation)",
        example = "[\"1\", \"2\", \"3\"]"
    )
    public List<String> currentMenuItems = new ArrayList<>();

    public UserSession() {}

    public UserSession(String sessionId, String phoneNumber) {
        this.sessionId = sessionId;
        this.phoneNumber = phoneNumber;
        this.stateHistory.add("main_menu");
    }

    public String getCurrentState() {
        if (stateHistory.isEmpty()) {
            return "main_menu";
        }
        return stateHistory.get(stateHistory.size() - 1);
    }

    public void pushState(String state) {
        this.stateHistory.add(state);
    }

    public String popState() {
        if (stateHistory.size() <= 1) {
            return "main_menu";
        }
        stateHistory.remove(stateHistory.size() - 1);
        return getCurrentState();
    }

    public void putData(String key, Object value) {
        this.data.put(key, value);
    }

    public Object getData(String key) {
        return this.data.get(key);
    }

    public String getStringData(String key) {
        Object value = this.data.get(key);
        return value != null ? value.toString() : null;
    }

    public Integer getIntegerData(String key) {
        Object value = this.data.get(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof String) {
            try {
                return Integer.valueOf((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public void resetPagination() {
        this.paginationOffset = 0;
        this.currentMenuItems.clear();
    }

    public void incrementPagination(int pageSize) {
        this.paginationOffset += pageSize;
    }
}