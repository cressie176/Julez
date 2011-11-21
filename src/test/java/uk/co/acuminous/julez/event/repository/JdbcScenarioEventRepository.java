package uk.co.acuminous.julez.event.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventHandler;
import uk.co.acuminous.julez.scenario.ScenarioEvent;

public class JdbcScenarioEventRepository implements ScenarioEventRepository, EventHandler {

    private JdbcTemplate jdbcTemplate;

    public JdbcScenarioEventRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }  
    
    public JdbcScenarioEventRepository ddl() {
        jdbcTemplate.execute(
            "CREATE TABLE scenario_event (" +
            "id VARCHAR(36) NOT NULL, " +
            "timestamp DOUBLE NOT NULL, " +
            "type VARCHAR(255) NOT NULL, " +
            "correlation_id VARCHAR(255) NULL, " +            
            "PRIMARY KEY (id)" +
        ")");
        
        jdbcTemplate.execute(
            "CREATE TABLE scenario_event_data (" +
            "id VARCHAR(36) NOT NULL, " +
            "message VARCHAR(4096) NULL, " +
            "status_code NUMERIC(3) NULL, " +            
            "PRIMARY KEY (id)" +
        ")");        
        return this;
    }

    @Override
    public void onEvent(Event event) {
       add((ScenarioEvent) event);        
    }    
    
    @Override
    public void add(ScenarioEvent event) {
        jdbcTemplate.update("INSERT INTO scenario_event (id, timestamp, type, correlation_id) VALUES (?, ?, ?, ?)", 
            event.getId(), 
            event.getTimestamp(), 
            event.getType(),
            event.getCorrelationId());
        
        if (!event.getData().isEmpty()) {
        
            jdbcTemplate.update("INSERT INTO scenario_event_data (id, message, status_code) VALUES (?, ?, ?)", 
                    event.getId(), 
                    event.getData().get("message"), 
                    event.getData().get("statusCode"));        
        }
    }

    @Override
    public int count() {
        return jdbcTemplate.queryForInt("SELECT count(*) FROM scenario_event");
    }

    @Override
    public ScenarioEvent get(String id) {
        return jdbcTemplate.queryForObject("SELECT se.id, se.timestamp, se.type, se.correlation_id, sed.message, sed.status_code FROM scenario_event se LEFT JOIN scenario_event_data sed ON se.id = sed.id WHERE id = ?", new ResultRowMapper(), id);
    }

    @Override
    public void dump() {
        for(ScenarioEvent event : jdbcTemplate.query("SELECT se.id, se.timestamp, se.type, se.correlation_id, sed.message, sed.status_code FROM scenario_event se LEFT JOIN scenario_event_data sed ON se.id = sed.id ORDER BY timestamp DESC", new ResultRowMapper())) {
            System.out.println(event);
        }        
    }
    
    @Override
    public void dump(String type) {
        for(ScenarioEvent event : jdbcTemplate.query("SELECT se.id, se.timestamp, se.type, se.correlation_id, sed.message, sed.status_code FROM scenario_event se LEFT JOIN scenario_event_data sed ON se.id = sed.id WHERE type = ? ORDER BY timestamp DESC", new ResultRowMapper(), type)) {
            System.out.println(event);
        }         
    }

    private class ResultRowMapper implements RowMapper<ScenarioEvent> {
        @Override
        public ScenarioEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
            ScenarioEvent event = new ScenarioEvent (
                rs.getString("id"),
                rs.getLong("timestamp"),
                rs.getString("type"),
                rs.getString("correlation_id"));
            
            if (rs.getString("message") != null) {
                event.getData().put("message", rs.getString("message"));
            }
            
            Integer statusCode = getInteger("status_code", rs);
            if (statusCode != null) {
                event.getData().put("statusCode", statusCode);
            }
            
            return event;
        }  
    }
    
    private Integer getInteger(String column, ResultSet rs) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }
}
