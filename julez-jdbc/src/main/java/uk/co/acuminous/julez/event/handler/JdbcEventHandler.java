package uk.co.acuminous.julez.event.handler;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.event.EventHandler;

public class JdbcEventHandler implements EventHandler {

    private JdbcTemplate jdbcTemplate;

    public JdbcEventHandler(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
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
}
