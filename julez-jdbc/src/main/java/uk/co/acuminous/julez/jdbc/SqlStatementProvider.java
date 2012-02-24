package uk.co.acuminous.julez.jdbc;

import java.util.List;

public interface SqlStatementProvider {
    String getSelectStatement();
    String getInsertStatement();
    List<String> getColumnNames();
}
