package uk.co.acuminous.julez.result;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


public class JdbcResultRepository implements ResultRepository {

    private JdbcTemplate jdbcTemplate;

    public JdbcResultRepository(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
    
    public void ddl() {
        jdbcTemplate.execute(
            "CREATE TABLE result (" +
            "id VARCHAR(36) NOT NULL, " +
            "source VARCHAR(255) NOT NULL, " +
            "run VARCHAR(255) NOT NULL, " +
            "timestamp DOUBLE NOT NULL, " +
            "scenario VARCHAR(255) NOT NULL, " +
            "status CHAR(4) NOT NULL, " +
            "description VARCHAR(4096) NULL, " +
            "PRIMARY KEY (id)" +
        ")");
    }

    @Override
    public void add(Result result) {
        jdbcTemplate.update("INSERT INTO result (id, source, run, timestamp, scenario, status, description) VALUES (?, ?, ?, ?, ?, ?, ?)", 
            result.getId(), 
            result.getSource(), 
            result.getRun(), 
            result.getTimestamp(), 
            result.getScenarioName(), 
            result.getStatus().name(), 
            result.getDescription());
    }

    @Override
    public int count() {
        return jdbcTemplate.queryForInt("SELECT count(*) FROM result");
    }

    @Override
    public Result get(String id) {
        return jdbcTemplate.queryForObject("SELECT * FROM result WHERE id = ?", new ResultRowMapper(), id);
    }

    @Override
    public void dump() {
        for(Result result : jdbcTemplate.query("SELECT * FROM result ORDER BY timestamp DESC", new ResultRowMapper())) {
            System.out.println(result);
        }        
    }
    
    @Override
    public void dump(ResultStatus status) {
        for(Result result : jdbcTemplate.query("SELECT * FROM result WHERE status = ? ORDER BY timestamp DESC", new ResultRowMapper(), status.name())) {
            System.out.println(result);
        }         
    }

    class ResultRowMapper implements RowMapper<Result> {
        @Override
        public Result mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Result(
                rs.getString("id"),
                rs.getString("source"),
                rs.getString("run"),
                rs.getString("scenario"),
                rs.getLong("timestamp"),
                ResultStatus.valueOf(rs.getString("status")),
                rs.getString("description")
            );
        }  
    }
    
}
