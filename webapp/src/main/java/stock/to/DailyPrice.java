package stock.to;

import java.util.ArrayList;
import java.util.List;

public class DailyPrice {
	private StockMetadata metadata;

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

	public StockMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(StockMetadata metadata) {
		this.metadata = metadata;
	}

	@Override
	public String toString() {
		String s = String.format(
				"Prices{exchange=%s, marketOpenMinute=%s, marketCloseMinute=%s, interval=%s, timezoneOffset=%s, numberOfPrice=%s}",
				metadata.getExchange(), metadata.getMarketOpenMinute(), metadata.getMarketCloseMinute(),
				metadata.getInterval(), metadata.getTimezoneOffset(), priceList.size());

		return s;
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
}
