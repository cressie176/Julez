package uk.co.acuminous.julez.event.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventHandler;
import uk.co.acuminous.julez.scenario.ScenarioEvent;

public class ScenarioEventJdbcRepository implements ScenarioEventRepository, EventHandler {

    private JdbcTemplate jdbcTemplate;

    public ScenarioEventJdbcRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }  
    
    public ScenarioEventJdbcRepository ddl() {
        jdbcTemplate.execute(
            "CREATE TABLE scenario_event (" +
            "id VARCHAR(36) NOT NULL, " +
            "timestamp DOUBLE NOT NULL, " +
            "type VARCHAR(255) NOT NULL, " +
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
        jdbcTemplate.update("INSERT INTO scenario_event (id, timestamp, type) VALUES (?, ?, ?)", 
            event.getId(), 
            event.getTimestamp(), 
            event.getType());
    }

    @Override
    public int count() {
        return jdbcTemplate.queryForInt("SELECT count(*) FROM scenario_event");
    }

    @Override
    public ScenarioEvent get(String id) {
        return jdbcTemplate.queryForObject("SELECT * FROM scenario_event WHERE id = ?", new ResultRowMapper(), id);
    }

    @Override
    public void dump() {
        for(ScenarioEvent event : jdbcTemplate.query("SELECT * FROM scenario_event ORDER BY timestamp DESC", new ResultRowMapper())) {
            System.out.println(event);
        }        
    }
    
    @Override
    public void dump(String type) {
        for(ScenarioEvent event : jdbcTemplate.query("SELECT * FROM scenario_event WHERE type = ? ORDER BY timestamp DESC", new ResultRowMapper(), type)) {
            System.out.println(event);
        }         
    }

    private class ResultRowMapper implements RowMapper<ScenarioEvent> {
        @Override
        public ScenarioEvent mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ScenarioEvent (
                rs.getString("id"),
                rs.getLong("timestamp"),
                rs.getString("type")
            );
        }  
    }
}
