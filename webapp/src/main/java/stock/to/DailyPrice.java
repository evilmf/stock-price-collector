package stock.to;

import java.util.ArrayList;
import java.util.List;

public class DailyPrice {
	private long date;
	private Long companyId;
	private Integer marketOpenMinute;
	private Integer marketCloseMinute;
	private Integer interval;
	private Integer timezoneOffset;
	private String exchange;

	private List<Price> priceList;

	private long createDate;
	private long updateDate;

	public DailyPrice() {
		setPriceList(new ArrayList<Price>());
	}

	public DailyPrice(int numberOfPrice) {
		setPriceList(new ArrayList<Price>(numberOfPrice));
	}

	public List<Price> getPriceList() {
		return priceList;
	}

	public void setPriceList(List<Price> priceList) {
		this.priceList = priceList;
	}

	public long getCreateDate() {
		return createDate;
	}

	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}

	public long getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(long updateDate) {
		this.updateDate = updateDate;
	}

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

	@Override
	public String toString() {
		String s = String.format(
				"Prices{exchange=%s, marketOpenMinute=%s, marketCloseMinute=%s, interval=%s, timezoneOffset=%s, numberOfPrice=%s}",
				exchange, marketOpenMinute, marketCloseMinute, interval, timezoneOffset, priceList.size());

		return s;
	}
}
