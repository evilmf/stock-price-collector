package stock.queue;

public enum State {
	READY(1), IN_PROGRESS(2), DONE(3), ERROR(4);

	private int state;

	private State(int state) {
		this.state = state;
	}

	public int getState() {
		return state;
	}
}