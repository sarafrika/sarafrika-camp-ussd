-- Create trigger function to update updated_date timestamp
CREATE OR REPLACE FUNCTION update_updated_date_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_date = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for all tracking tables
CREATE TRIGGER update_user_interactions_updated_date BEFORE UPDATE
    ON user_interactions FOR EACH ROW EXECUTE PROCEDURE update_updated_date_column();

CREATE TRIGGER update_session_events_updated_date BEFORE UPDATE
    ON session_events FOR EACH ROW EXECUTE PROCEDURE update_updated_date_column();

CREATE TRIGGER update_navigation_events_updated_date BEFORE UPDATE
    ON navigation_events FOR EACH ROW EXECUTE PROCEDURE update_updated_date_column();

CREATE TRIGGER update_performance_metrics_updated_date BEFORE UPDATE
    ON performance_metrics FOR EACH ROW EXECUTE PROCEDURE update_updated_date_column();