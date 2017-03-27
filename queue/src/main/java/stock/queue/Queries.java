package stock.queue;

public abstract class Queries {
	final private String LS = System.getProperty("line.separator");
	final protected String DEQUEUE_QUERY_TEMPLATE = 	"with dequeued as (" + LS +
														"	select" + LS +
														"		*" + LS +
														"	from %s wsc" + LS +
														"	where wsc.namespace = ?" + LS +
														"	and wsc.state = ?" + LS +
														"	order by wsc.status_date asc" + LS +
														"	limit ?" + LS + 
														"	for update skip locked" + LS +
														")" + LS +
														"update %s wsc" + LS + 
														"set state = ?, status_date = now()" + LS +
														"from dequeued deq" + LS +
														"where deq.id = wsc.id" + LS +
														"returning wsc.id, wsc.namespace, wsc.object_id," + LS +
														"	wsc.state, wsc.status_date," + LS +
														"	deq.status_date previous_status_date";
	
	final protected String ENQUEUE_QUERY_TEMPLATE = 	"insert into %s " + LS +
														"	(namespace, object_id, state, status_date)" + LS + 
														"select" + LS +
														"	? namespace," + LS +
														"	object_id," + LS +
														"	? state," + LS +
														"	now() status_date" + LS +
														"from unnest(?) obj(object_id)" + LS +
														"on conflict (namespace, object_id)" + LS +
														"do update set state = ?, status_date = now()";
	
	final protected String FINALIZE_QUERY_TEMPLATE = 	"with in_progress as (" + LS +
														"	select" + LS +
														"		*" + LS +
														"	from %s wsc" + LS +
														"	where wsc.namespace = ?" + LS +
														"	and wsc.state in (?, ?)" + LS +
														"	and wsc.object_id = ?" + LS +
														"	for update skip locked" + LS +
														")" + LS +
														"update %s wsc " + LS +
														"set state = ?, status_date = now()" + LS +
														"from in_progress ip" + LS +
														"where ip.id = wsc.id" + LS +
														"returning wsc.*";
}
