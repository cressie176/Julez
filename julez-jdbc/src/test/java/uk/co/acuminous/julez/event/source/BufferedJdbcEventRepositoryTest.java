package uk.co.acuminous.julez.event.source;

import uk.co.acuminous.julez.jdbc.SqlStatementProvider;

public class BufferedJdbcEventRepositoryTest extends JdbcEventRepositoryTest {

	private int bufferSize = 3;
	
	@Override protected JdbcEventRepository createRepository(SqlStatementProvider sql) {
		return new BufferedEventRepository(dataSource, columnMapper, sql, bufferSize);
	}        

	@Override protected JdbcEventRepository createRepository() {
		return new BufferedEventRepository(dataSource, columnMapper, bufferSize);
	}

}
