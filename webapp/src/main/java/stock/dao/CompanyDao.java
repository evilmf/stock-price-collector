package stock.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class CompanyDao {
	@Autowired
	JdbcTemplate jdbcTemplate;

	public List<Long> getActiveCompanyIds() {
		return jdbcTemplate.query(Queries.ALL_COMPANY_QUERY, new RowMapper<Long>() {

			@Override
			public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
				return rs.getLong("id");
			}
		});
	}

	public String getCompanyTickerById(Long companyId) {
		return jdbcTemplate.query(Queries.GET_COMPANY_BY_ID_QUERY, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, companyId);
			}
		}, new ResultSetExtractor<String>() {

			@Override
			public String extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					return rs.getString("ticker");
				}

				return null;
			}
		});
	}
}
