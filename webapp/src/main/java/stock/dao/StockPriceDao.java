package stock.dao;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import stock.to.DailyPrice;
import stock.to.Price;
import stock.to.StockMetadata;

@Component
public class StockPriceDao {
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	private ObjectMapper jacksonObjectMapper;

	private static final Logger log = LoggerFactory.getLogger(StockPriceDao.class);

	public void insertStockPrice(Price price) {
		jdbcTemplate.update(Queries.INSERT_STOCK_PRICE_QUERY, new Object[] { price.getCompanyId(), price.getTimestamp(),
				price.getClose(), price.getHigh(), price.getLow(), price.getOpen(), price.getVolume() });
	}

	public Long getLatestStockPriceTimestamp(long companyId) {
		return jdbcTemplate.query(Queries.GET_LATEST_STOCK_PRICE, new Object[] { companyId },
				new ResultSetExtractor<Long>() {

					@Override
					public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {
							return rs.getLong("timestamp");
						}

						return null;
					}
				});
	}

	public Long getLatestStockPriceCreateDate(long companyId) {
		return jdbcTemplate.query(Queries.GET_LATEST_STOCK_PRICE, new Object[] { companyId },
				new ResultSetExtractor<Long>() {

					@Override
					public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
						if (rs.next()) {
							return rs.getTimestamp("create_date", Calendar.getInstance(TimeZone.getTimeZone("UTC")))
									.getTime();
						}

						return null;
					}
				});
	}

	public void insertStockPriceMetadata(StockMetadata metadata) {
		jdbcTemplate.update(Queries.INSERT_STOCK_METADATA_QUERY,
				new Object[] { metadata.getDate(), metadata.getCompanyId(), metadata.getMarketOpenMinute(),
						metadata.getMarketCloseMinute(), metadata.getInterval(), metadata.getTimezoneOffset(),
						metadata.getExchange() });
	}

	public void insertStockPriceDaily(DailyPrice dailyPrice) {
		jdbcTemplate.update(Queries.INSERT_STOCK_PRICE_DAILY, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setLong(1, dailyPrice.getMetadata().getDate());
				ps.setLong(2, dailyPrice.getMetadata().getCompanyId());
				ps.setInt(3, dailyPrice.getMetadata().getMarketOpenMinute());
				ps.setInt(4, dailyPrice.getMetadata().getMarketCloseMinute());
				ps.setInt(5, dailyPrice.getMetadata().getInterval());
				ps.setInt(6, dailyPrice.getMetadata().getTimezoneOffset());
				ps.setString(7, dailyPrice.getMetadata().getExchange());

				PGobject priceList = new PGobject();
				priceList.setType("jsonb");
				try {
					priceList.setValue(jacksonObjectMapper.writeValueAsString(dailyPrice.getPriceList()));
				} catch (JsonProcessingException e) {
					String errMsg = String.format(
							"Failed to convert price list to JSON string [Company ID: %s; Date: %s]",
							dailyPrice.getMetadata().getCompanyId(), dailyPrice.getMetadata().getDate());
					// log.error(errMsg);
					throw new SQLException(errMsg, e.getCause());
				}

				ps.setObject(8, priceList);
				
				ps.setInt(9, dailyPrice.getMetadata().getMarketOpenMinute());
				ps.setInt(10, dailyPrice.getMetadata().getMarketCloseMinute());
				ps.setInt(11, dailyPrice.getMetadata().getInterval());
				ps.setInt(12, dailyPrice.getMetadata().getTimezoneOffset());
				ps.setString(13, dailyPrice.getMetadata().getExchange());
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
							StockMetadata metadata = new StockMetadata();
							metadata.setDate(rs.getLong("date"));
							metadata.setCompanyId(rs.getLong("company_id"));
							metadata.setMarketOpenMinute(rs.getInt("market_open_minute"));
							metadata.setMarketCloseMinute(rs.getInt("market_close_minute"));
							metadata.setInterval(rs.getInt("interval"));
							metadata.setTimezoneOffset(rs.getInt("timezone_offset"));
							metadata.setExchange(rs.getString("exchange"));

							dailyPrice.setMetadata(metadata);
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
