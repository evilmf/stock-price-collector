package stock.exception;

public class InvalidTickerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1575677457990289494L;

	public InvalidTickerException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
