package apps.sarafrika.service;

import apps.sarafrika.dto.UserSession;
import apps.sarafrika.entity.*;
import apps.sarafrika.enums.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@ApplicationScoped
public class TrackingService {

    private static final Logger LOG = Logger.getLogger(TrackingService.class);
    
    private final ConcurrentLinkedQueue<Object> eventQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean trackingEnabled = new AtomicBoolean(true);
    
    @Inject
    ManagedExecutor executor;
    
    @Inject
    TrackingPersistenceService persistenceService;

    public void trackInteractionAsync(String sessionId, String phoneNumber, InteractionType type, 
                                    String currentState, String previousState, String userInput, 
                                    String response, Integer processingTimeMs, String errorMessage, 
                                    Map<String, Object> metadata) {
        if (!trackingEnabled.get()) {
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                UserInteraction interaction = new UserInteraction(sessionId, phoneNumber, type);
                interaction.currentState = currentState; // Maps to menu_level column
                interaction.userInput = userInput;
                interaction.responseGenerated = response; // Maps to response_sent column
                
                eventQueue.offer(interaction);
            } catch (Exception e) {
                LOG.debug("Failed to queue interaction tracking event", e);
            }
        }, executor);
    }

    public void trackSessionEventAsync(String sessionId, String phoneNumber, SessionEventType eventType,
                                     UserSession sessionSnapshot, Integer redisOperationTimeMs,
                                     String networkCode, String serviceCode, Integer durationSeconds) {
        if (!trackingEnabled.get()) {
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                SessionEvent event = new SessionEvent(sessionId, phoneNumber, eventType);
                event.sessionDataSnapshot = sessionSnapshot;
                event.redisOperationTimeMs = redisOperationTimeMs;
                event.networkCode = networkCode;
                event.serviceCode = serviceCode;
                event.durationSeconds = durationSeconds;
                
                eventQueue.offer(event);
            } catch (Exception e) {
                LOG.debug("Failed to queue session event tracking", e);
            }
        }, executor);
    }

    public void trackNavigationAsync(String sessionId, String phoneNumber, String fromState, 
                                   String toState, NavigationType navigationType, String userInput,
                                   Integer timeOnPreviousPageMs, Map<String, Object> pageData) {
        if (!trackingEnabled.get()) {
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                NavigationEvent event = new NavigationEvent(sessionId, phoneNumber, fromState, toState, navigationType);
                event.userInput = userInput;
                event.timeOnPreviousPageMs = timeOnPreviousPageMs;
                if (pageData != null) {
                    event.pageData.putAll(pageData);
                }
                
                eventQueue.offer(event);
            } catch (Exception e) {
                LOG.debug("Failed to queue navigation tracking event", e);
            }
        }, executor);
    }

    public void trackPerformanceAsync(String sessionId, String phoneNumber, String metricType, 
                                    BigDecimal metricValue, Map<String, Object> contextData) {
        if (!trackingEnabled.get()) {
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                PerformanceMetric metric = new PerformanceMetric(sessionId, phoneNumber, metricType, metricValue);
                if (contextData != null) {
                    metric.contextData.putAll(contextData);
                }
                
                eventQueue.offer(metric);
            } catch (Exception e) {
                LOG.debug("Failed to queue performance tracking event", e);
            }
        }, executor);
    }

    public void disableTracking() {
        trackingEnabled.set(false);
        LOG.warn("Tracking has been disabled due to errors or system issues");
    }

    public void enableTracking() {
        trackingEnabled.set(true);
        LOG.info("Tracking has been enabled");
    }

    public boolean isTrackingEnabled() {
        return trackingEnabled.get();
    }

    public int getQueueSize() {
        return eventQueue.size();
    }

    ConcurrentLinkedQueue<Object> getEventQueue() {
        return eventQueue;
    }
}