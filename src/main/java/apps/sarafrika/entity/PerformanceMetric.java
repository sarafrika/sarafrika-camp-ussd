package apps.sarafrika.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "performance_metrics")
public class PerformanceMetric extends BaseEntity {

    @Column(name = "session_id")
    public String sessionId;

    @Column(name = "phone_number")
    public String phoneNumber;

    @Column(name = "metric_type")
    public String metricType;

    @Column(name = "metric_value")
    public BigDecimal metricValue;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "context_data")
    public Map<String, Object> contextData = new HashMap<>();

    public PerformanceMetric() {}

    public PerformanceMetric(String sessionId, String phoneNumber, String metricType, BigDecimal metricValue) {
        this.sessionId = sessionId;
        this.phoneNumber = phoneNumber;
        this.metricType = metricType;
        this.metricValue = metricValue;
    }
}