package stock.dao;

public class Queries {
	final private static String LS = System.getProperty("line.separator");

	final public static String ALL_COMPANY_QUERY = "select * from company where is_active is true";

	final public static String GET_COMPANY_BY_ID_QUERY = "select * from company where id = ?";
	
	final public static String INSERT_STOCK_PRICE_DAILY = "insert into stock_price_daily " + LS
			+ "(id, date, company_id, market_open_minute, market_close_minute, interval, timezone_offset, exchange, price_list, create_date, update_date)" + LS
			+ "values (nextval('seq_stock_price_daily_id'), ?, ?, ?, ?, ?, ?, ?, ?, now(), now())" + LS
			+ "on conflict (company_id, date) do update set" + LS
			+ "market_open_minute = ?, " + LS
			+ "market_close_minute = ?, " + LS
			+ "interval = ?," + LS
			+ "timezone_offset = ?," + LS
			+ "exchange = ?, " + LS
			+ "price_list = ?, " + LS
			+ "update_date = now()";
	
	final public static String GET_LATEST_STOCK_PRICE_DAILY = "select * from stock_price_daily where company_id = ? order by date desc limit 1";
}
