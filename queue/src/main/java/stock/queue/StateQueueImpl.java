package stock.queue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;


final public class StateQueueImpl extends Queries implements StateQueue {

	private JdbcTemplate jdbcTemplate;
	private String DEQUEUE_QUERY;
	private String ENQUEUE_QUERY;
	private String FINALIZE_QUERY;
	private PlatformTransactionManager txManager;
	private TransactionTemplate transactionTemplate;
	private static final Logger log = LoggerFactory.getLogger(StateQueueImpl.class);

	public StateQueueImpl(DataSource dataSource, String queueName) {
		DEQUEUE_QUERY = String.format(DEQUEUE_QUERY_TEMPLATE, queueName, queueName);
		ENQUEUE_QUERY = String.format(ENQUEUE_QUERY_TEMPLATE, queueName);
		FINALIZE_QUERY = String.format(FINALIZE_QUERY_TEMPLATE, queueName, queueName);
		jdbcTemplate = new JdbcTemplate(dataSource);

		txManager = new DataSourceTransactionManager(dataSource);
		transactionTemplate = new TransactionTemplate(txManager);
	}

	@Override
	public void enqueue(String namespace, List<Long> objectId) {
		jdbcTemplate.update(ENQUEUE_QUERY, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, namespace);
				ps.setInt(2, State.READY.getState());
				ps.setArray(3, ps.getConnection().createArrayOf("bigint", objectId.toArray()));
				ps.setInt(4, State.READY.getState());
			}
		});
	}

	@Override
	public Long dequeue(String namespace) {
		return jdbcTemplate.query(DEQUEUE_QUERY, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, namespace);
				ps.setInt(2, State.READY.getState());
				ps.setInt(3, 1);
				ps.setInt(4, State.IN_PROGRESS.getState());
			}
		}, new ResultSetExtractor<Long>() {

			@Override
			public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					return rs.getLong("object_id");
				} else {
					return null;
				}
			}
		});
	}

	@Override
	public void setDone(String namespace, long objectId) throws StateQueueException {
		setFinalState("LOAD", objectId, State.IN_PROGRESS, State.DONE);
	}

	@Override
	public void setError(String namespace, long objectId) throws StateQueueException {
		setFinalState("LOAD", objectId, State.IN_PROGRESS, State.ERROR);
	}

	private void setFinalState(String namespace, Long objectId, State previousState, State finalState)
			throws StateQueueException {
		transactionTemplate.execute(new TransactionCallback<Void>() {

			@Override
			public Void doInTransaction(TransactionStatus ts) {
				List<Long> ids = jdbcTemplate.query(FINALIZE_QUERY, new PreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setString(1, namespace);
						ps.setInt(2, previousState.getState());
						ps.setInt(3, finalState.getState());
						ps.setLong(4, objectId);
						ps.setInt(5, finalState.getState());
					}
				}, new RowMapper<Long>() {

					@Override
					public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getLong("object_id");
					}
				});

				if (!ids.contains(objectId)) {
					String errMsg = String.format(
							"Fail to update state to %s for object id %s. Either its previous state is not %s or it doesn't exist within namespace %s.",
							finalState.getState(), objectId, previousState.getState(), namespace);
					//throw new StateQueueException(errMsg, null);
					log.error(errMsg);
					ts.setRollbackOnly();
				}

				return null;
			}
		});
	}
}
