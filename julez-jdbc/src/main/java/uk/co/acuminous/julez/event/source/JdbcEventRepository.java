package uk.co.acuminous.julez.event.source;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventRepository;
import uk.co.acuminous.julez.event.pipe.BaseEventPipe;
import uk.co.acuminous.julez.mapper.OneWayMapper;
import uk.co.acuminous.julez.mapper.TwoWayMapper;

public class JdbcEventRepository extends BaseEventPipe implements EventRepository {

    private static final String SELECT_ALL = "SELECT * FROM event ORDER BY timestamp ASC, id ASC";    
    private static final String COUNT_ALL = "SELECT count(*) FROM event";
    
    private final JdbcTemplate jdbcTemplate;
    private final String selectAll;
    private final String countAll;
    private final TwoWayMapper columnMapper;
    private final OneWayMapper eventClassResolver;

    public JdbcEventRepository(DataSource dataSource, TwoWayMapper columnMapper, OneWayMapper eventClassResolver) {
        this(dataSource, columnMapper, eventClassResolver, SELECT_ALL, COUNT_ALL);
    }
    
    public JdbcEventRepository(DataSource dataSource, TwoWayMapper columnMapper, OneWayMapper eventClassResolver, String selectAll, String countAll) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.columnMapper = columnMapper;        
        this.eventClassResolver = eventClassResolver;
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
            try {
                Map<String, String> eventData = new HashMap<String, String>();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    String columnName = rs.getMetaData().getColumnName(i);
                    String key = columnMapper.getKey(columnName);
                    String value = rs.getString(i);
                    
                    if (key != null && value != null) {
                        eventData.put(key, value);                        
                    }
                }
                
                String className = eventClassResolver.getValue(eventData.get(Event.TYPE));
                Constructor<?> constructor = Class.forName(className).getConstructor(Map.class);
                Event event = (Event) constructor.newInstance(eventData); 
                return event;
                
            } catch (Exception e) {
                throw new RuntimeException(e);
            }        
        }  
    }    
}
