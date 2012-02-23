package uk.co.acuminous.julez.event.source;

import uk.co.acuminous.julez.jdbc.SqlStatementProvider;

public class BigGulpJdbcEventRepositoryTest extends JdbcEventRepositoryTest {

	@Override protected JdbcEventRepository createRepository(SqlStatementProvider sql) {
		return new BigGulpJdbcEventRepository(dataSource, columnMapper, sql);
	}        

	@Override protected JdbcEventRepository createRepository() {
		return new BigGulpJdbcEventRepository(dataSource, columnMapper);
	}

}
