package uk.co.acuminous.julez.event.source;

import java.util.Iterator;

import javax.sql.DataSource;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.jdbc.SqlStatementProvider;
import uk.co.acuminous.julez.mapper.TwoWayMapper;

/**
 * 
 * An event repository which grabs all stored events in one huge gulp in order to iterate.
 * 
 * This approach can make better use of database fetches, but at the expense of memory for large datasets.
 * 
 * @see BufferedEventRepository
 *
 */
public class BigGulpJdbcEventRepository extends JdbcEventRepository {

	public BigGulpJdbcEventRepository(DataSource dataSource, TwoWayMapper columnMapper) {
		super(dataSource, columnMapper);
	}

    public BigGulpJdbcEventRepository(DataSource dataSource, TwoWayMapper columnMapper, SqlStatementProvider sql) {
    	super(dataSource, columnMapper, sql);
    }


    @Override public Iterator<Event> iterator() {
        return jdbcTemplate.query(sql.getSelectStatement(), new EventRowMapper()).iterator();
	}    

}
