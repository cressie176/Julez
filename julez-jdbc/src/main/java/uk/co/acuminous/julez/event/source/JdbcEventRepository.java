package uk.co.acuminous.julez.event.source;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.pipe.PassThroughPipe;
import uk.co.acuminous.julez.event.repository.EventRepository;
import uk.co.acuminous.julez.jdbc.DefaultEventSql;
import uk.co.acuminous.julez.jdbc.SqlStatementProvider;
import uk.co.acuminous.julez.mapper.TwoWayMapper;

public class JdbcEventRepository extends PassThroughPipe implements EventRepository {

    private final JdbcTemplate jdbcTemplate;
    private final TwoWayMapper columnMapper;
    private final SqlStatementProvider sql;

    public JdbcEventRepository(DataSource dataSource, TwoWayMapper columnMapper) {
        this(dataSource, columnMapper, new DefaultEventSql(columnMapper.getValues()));        
    }
    
    public JdbcEventRepository(DataSource dataSource, TwoWayMapper columnMapper, SqlStatementProvider sql) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.columnMapper = columnMapper;        
        this.sql = sql;
    }    
    
    @Override
    public void onEvent(Event event) {
        List<String> columnNames = sql.getColumnNames();        
        List<Object> params = new ArrayList<Object>(sql.getColumnNames().size());
        for (String columnName : columnNames) {            
            String propertyName = columnMapper.getKey(columnName);
            params.add(event.get(propertyName));
        }            
        jdbcTemplate.update(sql.getInsertStatement(), params.toArray());
    }    
        
    @Override
    public void raise() {
        jdbcTemplate.query(sql.getSelectStatement(), new EventRaisingRowMapper());        
    }
    
    @Override
    public List<Event> getAll() {        
        return jdbcTemplate.query(sql.getSelectStatement(), new EventRowMapper());
    }
    
    @Override
    public int count() {
        return jdbcTemplate.queryForInt(sql.getCountStatement());
    }              
    
    private class EventRaisingRowMapper implements RowMapper<Event> {
        
        private EventRowMapper underlyingMapper = new EventRowMapper();
        
        @Override
        public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
            Event event = underlyingMapper.mapRow(rs, rowNum);
            handler.onEvent(event);
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
                
                return new Event(eventData);
                
            } catch (Exception e) {
                throw new RuntimeException(e);
            }        
        }  
    }    
}
