package stock.dao;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import stock.to.DailyPrice;
import stock.to.Price;

@Component
public class StockPriceDao {
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	private ObjectMapper jacksonObjectMapper;

	public void insertStockPriceDaily(DailyPrice dailyPrice) {
		jdbcTemplate.update(Queries.INSERT_STOCK_PRICE_DAILY, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, dailyPrice.getDate());
				ps.setLong(2, dailyPrice.getCompanyId());
				ps.setInt(3, dailyPrice.getMarketOpenMinute());
				ps.setInt(4, dailyPrice.getMarketCloseMinute());
				ps.setInt(5, dailyPrice.getInterval());
				ps.setInt(6, dailyPrice.getTimezoneOffset());
				ps.setString(7, dailyPrice.getExchange());

				PGobject priceList = new PGobject();
				priceList.setType("jsonb");
				try {
					priceList.setValue(jacksonObjectMapper.writeValueAsString(dailyPrice.getPriceList()));
				} catch (JsonProcessingException e) {
					String errMsg = String.format(
							"Failed to convert price list to JSON string [Company ID: %s; Date: %s]",
							dailyPrice.getCompanyId(), dailyPrice.getDate());
					// log.error(errMsg);
					throw new SQLException(errMsg, e.getCause());
				}

				ps.setObject(8, priceList);
				
				ps.setInt(9, dailyPrice.getMarketOpenMinute());
				ps.setInt(10, dailyPrice.getMarketCloseMinute());
				ps.setInt(11, dailyPrice.getInterval());
				ps.setInt(12, dailyPrice.getTimezoneOffset());
				ps.setString(13, dailyPrice.getExchange());
				ps.setObject(14, priceList);
			}
		});
	}

	public DailyPrice getLatestStockPriceDaily(long companyId) {
		return jdbcTemplate.query(Queries.GET_LATEST_STOCK_PRICE_DAILY, new Object[] { companyId },
				new ResultSetExtractor<DailyPrice>() {

					@Override
					public DailyPrice extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {
							DailyPrice dailyPrice = new DailyPrice();
							dailyPrice.setDate(rs.getLong("date"));
							dailyPrice.setCompanyId(rs.getLong("company_id"));
							dailyPrice.setMarketOpenMinute(rs.getInt("market_open_minute"));
							dailyPrice.setMarketCloseMinute(rs.getInt("market_close_minute"));
							dailyPrice.setInterval(rs.getInt("interval"));
							dailyPrice.setTimezoneOffset(rs.getInt("timezone_offset"));
							dailyPrice.setExchange(rs.getString("exchange"));

							dailyPrice.setCreateDate(
									rs.getTimestamp("create_date", Calendar.getInstance(TimeZone.getTimeZone("UTC")))
											.getTime());
							dailyPrice.setUpdateDate(
									rs.getTimestamp("update_date", Calendar.getInstance(TimeZone.getTimeZone("UTC")))
											.getTime());

							try {
								dailyPrice.setPriceList(jacksonObjectMapper.readValue(rs.getString("price_list"),
										new TypeReference<List<Price>>() {
										}));
							} catch (JsonParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (JsonMappingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							return dailyPrice;
						}

						return null;
					}

				});
	}
}
