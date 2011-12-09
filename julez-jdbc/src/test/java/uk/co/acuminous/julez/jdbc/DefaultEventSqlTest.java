package uk.co.acuminous.julez.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;

public class DefaultEventSqlTest {

    @Test
    public void returnsSelectSql() {
        assertEquals("SELECT * FROM event ORDER BY timestamp ASC, id ASC", new DefaultEventSql("id").getSelectStatement());
    }
    
    @Test
    public void returnsCountSql() {
        assertEquals("SELECT count(*) FROM event", new DefaultEventSql("id").getCountStatement());
    }    
    
    @Test
    public void returnsInsertSql() {
        assertEquals("INSERT INTO event (foo) VALUES (?)", new DefaultEventSql("foo").getInsertStatement());        
        assertEquals("INSERT INTO event (id,timestamp,type) VALUES (?,?,?)", new DefaultEventSql("id", "timestamp", "type").getInsertStatement());        
    }
    
    @Test
    public void errorsWhenNoColumnNamesAreSpecifie() {
        try {
            new DefaultEventSql();
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            assertEquals("At least one column name is required", e.getMessage());
        }
    }
    
    @Test
    public void returnsColumnNames() {
        String[] columnNames = new String[] {"id"};
        DefaultEventSql sql = new DefaultEventSql(columnNames);
        assertEquals(Arrays.asList(columnNames), sql.getColumnNames());        
    }    
}
