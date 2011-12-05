package uk.co.acuminous.julez.event.source;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    private static final String SELECT_ALL = "SELECT * FROM event ORDER BY timestamp ASC, id ASC";    
    private static final String COUNT_ALL = "SELECT count(*) FROM event";
    
    private JdbcTemplate jdbcTemplate;
    private final String selectAll;
    private final String countAll;

    public JdbcEventRepository(DataSource dataSource) {
        this(dataSource, SELECT_ALL, COUNT_ALL);
    }
    
    public JdbcEventRepository(DataSource dataSource, String selectAll, String countAll) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.selectAll = selectAll;
        this.countAll = countAll;        
    }
        
    @Override
    public void raise() {
        jdbcTemplate.query(selectAll, new EventRaisingRowMapper());        
    }
    
    @Override
    public List<Event> list() {        
        return jdbcTemplate.query(selectAll, new EventRowMapper());
    }
    
    @Override
    public int count() {
        return jdbcTemplate.queryForInt(countAll);
    }              
    
    private class EventRaisingRowMapper implements RowMapper<Event> {
        
        private EventRowMapper underlyingMapper = new EventRowMapper();
        
        @Override
        public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
            Event event = underlyingMapper.mapRow(rs, rowNum);
            onEvent(event);
            return null;
        }  
    }     
    
    private class EventRowMapper implements RowMapper<Event> {
        @Override
        public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
            String discriminator = rs.getString("discriminator");
            try {
                Constructor<?> constructor = Class.forName(discriminator).getConstructor(String.class, Long.class, String.class);
                Event event = (Event) constructor.newInstance(rs.getString("id"), rs.getLong("timestamp"), rs.getString("type")); 
                
                jdbcTemplate.query("SELECT * FROM event_data WHERE id = ?", new EventDataRowMapper(event), event.getId());
                
                return event;
                
            } catch (Exception e) {
                throw new RuntimeException(e);
            }        
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
