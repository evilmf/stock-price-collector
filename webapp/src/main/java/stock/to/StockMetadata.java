package stock.to;


public class StockMetadata {
	private long date;
	private Long companyId;
	private Integer marketOpenMinute;
	private Integer marketCloseMinute;
	private Integer interval;
	private Integer timezoneOffset;
	private String exchange;

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public Integer getMarketOpenMinute() {
		return marketOpenMinute;
	}

	public void setMarketOpenMinute(Integer marketOpenMinute) {
		this.marketOpenMinute = marketOpenMinute;
	}

	public Integer getMarketCloseMinute() {
		return marketCloseMinute;
	}

	public void setMarketCloseMinute(Integer marketCloseMinute) {
		this.marketCloseMinute = marketCloseMinute;
	}

	public Integer getInterval() {
		return interval;
	}

	public void setInterval(Integer interval) {
		this.interval = interval;
	}

	public Integer getTimezoneOffset() {
		return timezoneOffset;
	}

	public void setTimezoneOffset(Integer timezoneOffset) {
		this.timezoneOffset = timezoneOffset;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
}
