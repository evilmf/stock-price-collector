package stock.to;

import java.util.Objects;

public class Price {
	private long timestamp;
	private double close;
	private double high;
	private double low;
	private double open;
	private long volume;

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public double getClose() {
		return close;
	}

	public void setClose(double close) {
		this.close = close;
	}

	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public double getLow() {
		return low;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public double getOpen() {
		return open;
	}

	public void setOpen(double open) {
		this.open = open;
	}

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}

		if (getClass() != o.getClass()) {
			return false;
		}

		Price other = (Price) o;

		if (other.getTimestamp() != timestamp) {
			return false;
		}

		if (other.getClose() != close) {
			return false;
		}

		if (other.getHigh() != high) {
			return false;
		}

		if (other.getLow() != low) {
			return false;
		}

		if (other.getOpen() != open) {
			return false;
		}

		if (other.getVolume() != volume) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(timestamp, close, high, low, open, volume);
	}
}
