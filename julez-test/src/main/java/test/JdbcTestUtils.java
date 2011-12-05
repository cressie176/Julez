package test;

import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class JdbcTestUtils {
    
    public static SingleConnectionDataSource getDataSource() {
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource();
        dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
        dataSource.setUrl("jdbc:hsqldb:mem:julez");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    public static void ddl() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSource());
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
    }     
    
    public static void nukeDatabase() {
        try {
            getDataSource().getConnection().prepareStatement("shutdown").execute();
        } catch (SQLException e) {
            // Meh
        }
    }
}
