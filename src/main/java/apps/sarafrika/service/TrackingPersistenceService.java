package apps.sarafrika.service;

import apps.sarafrika.entity.*;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@ApplicationScoped
public class TrackingPersistenceService {

    private static final Logger LOG = Logger.getLogger(TrackingPersistenceService.class);
    private static final int BATCH_SIZE = 50;
    private final AtomicInteger errorCount = new AtomicInteger(0);
    private final AtomicInteger successCount = new AtomicInteger(0);

    @Inject
    TrackingService trackingService;

    @Scheduled(every = "2s")
    public void processBatchEvents() {
        if (!trackingService.isTrackingEnabled()) {
            return;
        }

        List<Object> batch = drainEventQueue(BATCH_SIZE);
        if (batch.isEmpty()) {
            return;
        }

        int successfulPersists = persistBatch(batch);
        successCount.addAndGet(successfulPersists);
        
        if (errorCount.get() > 0 && successfulPersists > 0) {
            errorCount.set(0);
        }
    }

    private List<Object> drainEventQueue(int maxSize) {
        List<Object> batch = new ArrayList<>();
        var queue = trackingService.getEventQueue();
        
        Object event;
        while (batch.size() < maxSize && (event = queue.poll()) != null) {
            batch.add(event);
        }
        
        return batch;
    }

    private int persistBatch(List<Object> events) {
        int successCount = 0;
        int failureCount = 0;
        
        for (Object event : events) {
            try {
                persistSingleEvent(event);
                successCount++;
            } catch (Exception e) {
                failureCount++;
                LOG.error("Failed to persist individual event: " + event.getClass().getSimpleName(), e);
            }
        }
        
        if (failureCount > 0) {
            int currentErrors = errorCount.addAndGet(failureCount);
            if (currentErrors > 50) { // Increased threshold
                trackingService.disableTracking();
                LOG.error("Disabling tracking due to persistent errors: " + currentErrors);
            }
        }
        
        return successCount;
    }
    
    @Transactional(value = TxType.REQUIRES_NEW)
    private void persistSingleEvent(Object event) {
        switch (event) {
            case UserInteraction interaction -> interaction.persist();
            case SessionEvent sessionEvent -> sessionEvent.persist();
            case NavigationEvent navigationEvent -> navigationEvent.persist();
            case PerformanceMetric performanceMetric -> performanceMetric.persist();
            default -> LOG.debug("Unknown event type: " + event.getClass().getSimpleName());
        }
    }

    @Scheduled(every = "1m")
    public void logTrackingStats() {
        if (trackingService.isTrackingEnabled()) {
            int queueSize = trackingService.getQueueSize();
            int successTotal = successCount.get();
            int errorTotal = errorCount.get();
            
            LOG.debug(String.format("Tracking stats - Queue: %d, Success: %d, Errors: %d", 
                     queueSize, successTotal, errorTotal));
                     
            if (queueSize > 1000) {
                LOG.warn("Tracking queue is growing large: " + queueSize);
            }
        }
    }

    @Scheduled(every = "24h")
    @Transactional
    public void cleanupOldTrackingData() {
        try {
            long deletedInteractions = UserInteraction.delete("createdDate < ?1", 
                java.time.LocalDateTime.now().minusDays(90));
            
            long deletedSessions = SessionEvent.delete("createdDate < ?1", 
                java.time.LocalDateTime.now().minusDays(60));
            
            long deletedNavigation = NavigationEvent.delete("createdDate < ?1", 
                java.time.LocalDateTime.now().minusDays(30));
                
            long deletedMetrics = PerformanceMetric.delete("createdDate < ?1", 
                java.time.LocalDateTime.now().minusDays(7));
            
            LOG.info(String.format("Cleaned up tracking data - Interactions: %d, Sessions: %d, Navigation: %d, Metrics: %d",
                    deletedInteractions, deletedSessions, deletedNavigation, deletedMetrics));
                    
        } catch (Exception e) {
            LOG.error("Failed to cleanup old tracking data", e);
        }
    }
}