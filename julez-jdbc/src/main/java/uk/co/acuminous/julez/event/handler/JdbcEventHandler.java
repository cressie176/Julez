package uk.co.acuminous.julez.event.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventHandler;
import uk.co.acuminous.julez.mapper.TwoWayMapper;
import uk.co.acuminous.julez.util.StringUtils;

public class JdbcEventHandler implements EventHandler {

    private final JdbcTemplate jdbcTemplate;
    private final TwoWayMapper columnMapper;
    private final String sql;

    public JdbcEventHandler(DataSource dataSource, TwoWayMapper columnMapper) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.columnMapper = columnMapper;
        this.sql = buildInsertStatement();
    }

    private String buildInsertStatement() {
        String columnNames = StringUtils.join(columnMapper.getValues(), ",");
        String placeHolders = columnNames.replaceAll("[^,]+", "?");
        return String.format("INSERT INTO event (%s) VALUES (%s)", columnNames, placeHolders);
    }  
    
    @Override
    public void onEvent(Event event) {
        Collection<String> propertyNames = columnMapper.getKeys();
        List<Object> params = new ArrayList<Object>(propertyNames.size());
        for (String propertyName : propertyNames) {
            params.add(event.get(propertyName));
        }            
        jdbcTemplate.update(sql, params.toArray());
    }                
}
