package uk.co.acuminous.julez.event.source;

import java.nio.BufferUnderflowException;
import java.util.Iterator;
import java.util.List;

import javax.sql.DataSource;

import uk.co.acuminous.julez.event.Event;
import uk.co.acuminous.julez.jdbc.SqlStatementProvider;
import uk.co.acuminous.julez.mapper.TwoWayMapper;

/**
 * 
 * An event repository which does not grab all stored events in one huge gulp in order to iterate.
 * 
 * Instead this implementation repeatedly grabs up to a specified buffer size until exhausted.
 * This approach can make better use of memory for large datasets, but at the expense of more database fetches.
 * 
 * Note that this implementation appends to the "selectStatement" from the supplied SqlStatementProvider and thus may fail for
 * databases which do not support the LIMIT OFFSET mechanism. As of now, Julez only supports hsqldb anyway, so the point is moot.
 * Don't say I didn't warn you, though.
 * 
 * @see BigGulpJdbcEventRepository
 *
 */
public class BufferedEventRepository extends JdbcEventRepository {

	private int bufferSize;
	
	public BufferedEventRepository(DataSource dataSource, TwoWayMapper columnMapper, int bufferSize) {
		super(dataSource, columnMapper);
		this.bufferSize = bufferSize;
	}

    public BufferedEventRepository(DataSource dataSource, TwoWayMapper columnMapper, SqlStatementProvider sql, int bufferSize) {
    	super(dataSource, columnMapper, sql);
		this.bufferSize = bufferSize;
    }

    @Override public Iterator<Event> iterator() {
    	return new BufferedIterator<Event>(new ListCollector<Event>() {
    		public List<Event> collect(int chunk) {
    	        return jdbcTemplate.query(
    	        		sql.getSelectStatement() + " limit " + bufferSize + " offset " + (chunk * bufferSize), 
    	        		new EventRowMapper());
    		}
    	});
	}    

    interface ListCollector<T> {
    	List<T> collect(int chunk);
    }
    
    class EmptyIterator<T> implements Iterator<T> {
		@Override public boolean hasNext() {
			return false;
		}

		@Override public T next() {
			return null;
		}

		@Override public void remove() {
			throw new UnsupportedOperationException("Cannot remove from empty Iterator");
		}
    }
    
    class BufferedIterator<T> implements Iterator<T> {
    	private ListCollector<T> listCollector;
    	private int chunk;
    	private Iterator<T> buffer;
    	
		public BufferedIterator(ListCollector<T> listCollector) {
			this.listCollector = listCollector;
			this.chunk = 0;
			this.buffer = new EmptyIterator<T>();
		}

		@Override public boolean hasNext() {
			if (null == buffer) return false;
			if (buffer.hasNext()) return true;
			return nextChunk();
		}

		private boolean nextChunk() {
			buffer = null;
			List<T> collected = listCollector.collect(chunk++);
			if (null == collected || collected.isEmpty()) return false;
			
			buffer = collected.iterator();
			return true;
		}

		@Override public T next() {
			if (null == buffer || !hasNext()) throw new BufferUnderflowException();
			return buffer.next();
		}

		@Override public void remove() {
			throw new UnsupportedOperationException("Cannot remove from buffered Iterator");
		}
    }
}
