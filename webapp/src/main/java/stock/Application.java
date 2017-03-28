package stock;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import stock.queue.StateQueue;
import stock.queue.StateQueueImpl;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class Application {
	@Autowired
	DataSource dataSource;

	@Value("${webapp.state_queue_table}")
	private String STATE_QUEUE_TABLE;
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	@Bean
	public StateQueue stateQueue() {
		return new StateQueueImpl(dataSource, STATE_QUEUE_TABLE);
	}
}
