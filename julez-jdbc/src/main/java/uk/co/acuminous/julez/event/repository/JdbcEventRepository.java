package uk.co.acuminous.julez.event.repository;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventRepository;
import uk.co.acuminous.julez.event.pipe.BaseEventPipe;

public class JdbcEventRepository extends BaseEventPipe implements EventRepository {

    private JdbcTemplate jdbcTemplate;

    public JdbcEventRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }  
    
    public JdbcEventRepository ddl() {
        jdbcTemplate.execute(
            "CREATE TABLE event (" +
            "id VARCHAR(36) NOT NULL, " +
            "timestamp DOUBLE NOT NULL, " +
            "type VARCHAR(255) NOT NULL, " +
            "discriminator VARCHAR(255) NOT NULL, " +
            "PRIMARY KEY (id)" +
        ")");
        
        jdbcTemplate.execute(
            "CREATE TABLE event_data (" +
            "id VARCHAR(36) NOT NULL, " +
            "name VARCHAR(255) NULL, " +
            "value VARCHAR(4096) NULL, " +            
            "PRIMARY KEY (id, name)" +
        ")");        
        return this;
    }

    @Override
    public void onEvent(Event event) {
        jdbcTemplate.update("INSERT INTO event (id, timestamp, type, discriminator) VALUES (?, ?, ?, ?)", 
            event.getId(), 
            event.getTimestamp(), 
            event.getType(),
            event.getClass().getName());
        
        Map<String, String> data = event.getData();
        for (String name : data.keySet()) {
            jdbcTemplate.update("INSERT INTO event_data (id, name, value) VALUES (?, ?, ?)", 
                    event.getId(), 
                    name, 
                    data.get(name));                    
        }
    }

    private static final String SELECT_ALL = "SELECT e.* FROM event e ORDER BY timestamp ASC";    
    public void replay() {
        jdbcTemplate.query(SELECT_ALL, new EventRowMapper());
    }
              
    private static final String SELECT_ALL_AFTER = "SELECT e.* FROM event e WHERE timestamp > ? ORDER BY timestamp ASC";    
    public void raiseAllEventsAfter(long timestamp) {
        jdbcTemplate.query(SELECT_ALL_AFTER, new EventRowMapper(), timestamp);
    }
    
    private static final String SELECT_ALL_OF_TYPE = "SELECT e.* FROM event e WHERE type IN (?) ORDER BY timestamp ASC";    
    public void raiseAllEventsOfType(String... types) {        
        String sql = SELECT_ALL_OF_TYPE.replace("(?)", "(" + questionMarks(types) + ")");        
        jdbcTemplate.query(sql, new EventRowMapper(), (Object[]) types);
    }
    
    private static final String SELECT_ALL_AFTER_TIMESTAMP_OF_TYPE = "SELECT e.* FROM event e WHERE timestamp > ? AND type IN (?) ORDER BY timestamp ASC";    
    public void raiseAllEventsAfterTimestampOfType(Long timestamp, String... types) {        
        String sql = SELECT_ALL_AFTER_TIMESTAMP_OF_TYPE.replace("(?)", "(" + questionMarks(types) + ")");
        
        List<Object> args = new ArrayList<Object>();
        args.add(timestamp);
        args.addAll(Arrays.asList(types));         
        
        jdbcTemplate.query(sql, new EventRowMapper(), (Object[]) args.toArray());
    }    

    private String questionMarks(String... types) {
        StringBuilder sb = new StringBuilder("?");        
        for (int i = 0; i < types.length - 1; i++) {
            sb.append(",?");
        }
        return sb.toString();
    }    
    
    private class EventRowMapper implements RowMapper<Event> {
        @Override
        public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
            String discriminator = rs.getString("discriminator");
            try {
                Constructor<?> constructor = Class.forName(discriminator).getConstructor(String.class, Long.class, String.class);
                Event event = (Event) constructor.newInstance(rs.getString("id"), rs.getLong("timestamp"), rs.getString("type")); 
                
                jdbcTemplate.query("SELECT * FROM event_data WHERE id = ?", new EventDataRowMapper(event), event.getId());
                
                handler.onEvent(event);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        }  
    }    
    
    private class EventDataRowMapper implements RowMapper<Map.Entry<String, String>> {

        private final Event event;

        public EventDataRowMapper(Event event) {
            this.event = event;
        }

        @Override
        public Entry<String, String> mapRow(ResultSet rs, int rowNum) throws SQLException {
            event.getData().put(rs.getString("name"), rs.getString("value"));
            return null;
        }       
    }
}
