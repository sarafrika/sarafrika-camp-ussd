package apps.sarafrika.service;

import apps.sarafrika.dto.UserSession;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.value.ValueCommands;
import io.quarkus.redis.datasource.keys.KeyCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Optional;

@ApplicationScoped
public class SessionService {

    private static final Duration SESSION_TTL = Duration.ofMinutes(5);
    private static final String SESSION_KEY_PREFIX = "ussd:session:";

    @Inject
    RedisDataSource redisDataSource;

    @Inject
    ObjectMapper objectMapper;

    private ValueCommands<String, String> valueCommands;
    private KeyCommands<String> keyCommands;

    public void init() {
        this.valueCommands = redisDataSource.value(String.class);
        this.keyCommands = redisDataSource.key();
    }

    public void saveSession(String sessionId, UserSession session) {
        if (valueCommands == null) {
            init();
        }
        try {
            String sessionJson = objectMapper.writeValueAsString(session);
            String key = SESSION_KEY_PREFIX + sessionId;
            valueCommands.setex(key, SESSION_TTL.getSeconds(), sessionJson);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save session", e);
        }
    }

    public Optional<UserSession> getSession(String sessionId) {
        if (valueCommands == null) {
            init();
        }
        try {
            String key = SESSION_KEY_PREFIX + sessionId;
            String sessionJson = valueCommands.get(key);
            
            if (sessionJson == null) {
                return Optional.empty();
            }
            
            UserSession session = objectMapper.readValue(sessionJson, UserSession.class);
            return Optional.of(session);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve session", e);
        }
    }

    public void deleteSession(String sessionId) {
        if (valueCommands == null) {
            init();
        }
        String key = SESSION_KEY_PREFIX + sessionId;
        valueCommands.getdel(key);
    }

    public void extendSession(String sessionId) {
        if (keyCommands == null) {
            init();
        }
        String key = SESSION_KEY_PREFIX + sessionId;
        keyCommands.expire(key, SESSION_TTL);
    }
}