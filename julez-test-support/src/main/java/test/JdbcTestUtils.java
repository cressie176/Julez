package test;

import java.sql.SQLException;

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
    
    public static void nukeDatabase() {
        try {
            getDataSource().getConnection().prepareStatement("shutdown").execute();
        } catch (SQLException e) {
            // Meh
        }
    }
}
