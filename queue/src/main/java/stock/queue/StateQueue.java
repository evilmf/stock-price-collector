package stock.queue;

import java.util.List;

public interface StateQueue {
	public void enqueue(String namespace, List<Long> objectId);

	public Long dequeue(String namespace);

	public void setDone(String namespace, long objectId);
	
	public void setError(String namespace, long objectId);
}
