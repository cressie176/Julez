package uk.co.acuminous.julez.jdbc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import uk.co.acuminous.julez.util.StringUtils;

public class DefaultEventSql implements SqlStatementProvider {

    private static final String SELECT_ALL = "SELECT * FROM event ORDER BY timestamp ASC, id ASC";
    private final List<String> columnNames;
    private final String insertSql;

    public DefaultEventSql(String... columnNames) {        
        this(Arrays.asList(columnNames));  
    }
    
    public DefaultEventSql(Collection<String> columnNames) {
        if (columnNames.isEmpty()) {
            throw new IllegalArgumentException("At least one column name is required");
        }
        this.columnNames = new ArrayList<String>(columnNames);   
        this.insertSql = buildInsertStatement();        
    }
        
    @Override
    public String getSelectStatement() {
        return SELECT_ALL;
    }

    @Override
    public String getInsertStatement() {
        return insertSql;
    }
    
    private String buildInsertStatement() {
        String commaSeparatedColumnNames = StringUtils.join(columnNames, ",");
        String placeHolders = commaSeparatedColumnNames.replaceAll("[^,]+", "?");
        return String.format("INSERT INTO event (%s) VALUES (%s)", commaSeparatedColumnNames, placeHolders);
    }

    public List<String> getColumnNames() {
        return columnNames;
    }  
    

}
