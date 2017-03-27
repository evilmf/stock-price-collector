package stock.queue;

import org.springframework.dao.UncategorizedDataAccessException;

public class StateQueueException extends UncategorizedDataAccessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StateQueueException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
