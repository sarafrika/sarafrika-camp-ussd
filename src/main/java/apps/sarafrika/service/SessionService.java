package apps.sarafrika.service;

import apps.sarafrika.dto.UserSession;
import apps.sarafrika.enums.SessionEventType;
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

    @Inject
    TrackingService trackingService;

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
        
        long startTime = System.currentTimeMillis();
        SessionEventType eventType = session.stateHistory.size() <= 1 ? 
                SessionEventType.CREATED : SessionEventType.DATA_UPDATED;
                
        try {
            String sessionJson = objectMapper.writeValueAsString(session);
            String key = SESSION_KEY_PREFIX + sessionId;
            valueCommands.setex(key, SESSION_TTL.getSeconds(), sessionJson);
            
            int redisOperationTime = (int) (System.currentTimeMillis() - startTime);
            
            trackingService.trackSessionEventAsync(sessionId, session.phoneNumber, eventType,
                    session, redisOperationTime, null, null, null);
                    
        } catch (Exception e) {
            int redisOperationTime = (int) (System.currentTimeMillis() - startTime);
            
            trackingService.trackSessionEventAsync(sessionId, session.phoneNumber, 
                    SessionEventType.EXPIRED, session, redisOperationTime, null, null, null);
                    
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
        
        try {
            UserSession session = getSession(sessionId).orElse(null);
            String key = SESSION_KEY_PREFIX + sessionId;
            valueCommands.getdel(key);
            
            if (session != null) {
                trackingService.trackSessionEventAsync(sessionId, session.phoneNumber, 
                        SessionEventType.TERMINATED, session, null, null, null, null);
            }
        } catch (Exception e) {
            String key = SESSION_KEY_PREFIX + sessionId;
            valueCommands.getdel(key);
        }
    }

    public void extendSession(String sessionId) {
        if (keyCommands == null) {
            init();
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            String key = SESSION_KEY_PREFIX + sessionId;
            keyCommands.expire(key, SESSION_TTL);
            
            int redisOperationTime = (int) (System.currentTimeMillis() - startTime);
            
            UserSession session = getSession(sessionId).orElse(null);
            if (session != null) {
                trackingService.trackSessionEventAsync(sessionId, session.phoneNumber, 
                        SessionEventType.EXTENDED, session, redisOperationTime, null, null, null);
            }
        } catch (Exception e) {
            String key = SESSION_KEY_PREFIX + sessionId;
            keyCommands.expire(key, SESSION_TTL);
        }
    }
}