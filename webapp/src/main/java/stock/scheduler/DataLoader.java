package stock.scheduler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import stock.dao.CompanyDao;
import stock.dao.StockPriceDao;
import stock.queue.StateQueue;
import stock.service.GoogleFinanceService;
import stock.to.DailyPrice;

@Component
public class DataLoader {
	@Autowired
	StateQueue stateQueue;

	@Autowired
	CompanyDao companyDao;

	@Autowired
	StockPriceDao stockPriceDao;

	@Autowired
	GoogleFinanceService googleFinanceService;

	@Autowired
	TransactionTemplate transactionTemplate;

	private static final Logger log = LoggerFactory.getLogger(DataLoader.class);
	private final String NAMESPACE = "LOAD";
	
	@Value("${webapp.price_loader_thread_pool_size}")
	private int PRICE_LOADER_THREAD_POOL_SIZE;

	@Scheduled(cron = "${webapp.price_loader_cron}")
	public void load() throws InterruptedException {
		log.info("Start loading...");

		List<Long> companyIds = companyDao.getActiveCompanyIds();
		log.info("Number of companies to load: {}", companyIds.size());
		stateQueue.enqueue(NAMESPACE, companyIds);

		ExecutorService executorService = Executors.newFixedThreadPool(PRICE_LOADER_THREAD_POOL_SIZE);
		List<Callable<Void>> tasks = new ArrayList<Callable<Void>>(PRICE_LOADER_THREAD_POOL_SIZE);
		for (int i = 0; i < PRICE_LOADER_THREAD_POOL_SIZE; i++) {
			tasks.add(new stockPriceTask());
		}

		executorService.invokeAll(tasks);

		log.info("Done loading!");
	}

	private class stockPriceTask implements Callable<Void> {

		@Override
		public Void call() throws Exception {
			Long cid = null;
			Boolean isError;
			while (true) {
				isError = false;
				try {
					cid = stateQueue.dequeue(NAMESPACE);
					if (cid == null) {
						log.info("Queue is empty!");
						break;
					}

					DailyPrice latestDailyPrice = stockPriceDao.getLatestStockPriceDaily(cid);
					int numberOfDay = calculateStockPriceTimeRange(latestDailyPrice);
					if (numberOfDay <= 0) {
						log.info("Stock price for company ID {} is already up to date!", cid);
						continue;
					}

					String ticker = companyDao.getCompanyTickerById(cid);
					if (ticker == null || ticker.trim().isEmpty()) {
						log.warn("Company ID {} has no ticker", cid);
						continue;
					}

					log.info("Fetching {} day(s) of stock price for company ID {}, ticker {}", numberOfDay, cid,
							ticker);
					List<DailyPrice> pricesList = googleFinanceService.getPrices(ticker, cid, numberOfDay);
					log.info("Done fetching stock price for company ID {}. Number of days: {}", cid, pricesList.size());

					for (DailyPrice prices : pricesList) {
						stockPriceDao.insertStockPriceDaily(prices);	
					}

					Thread.sleep(1000);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					isError = true;
				} finally {
					if (cid != null) {
						log.info("Done working on company ID {}", cid);
						if (!isError) {
							stateQueue.setDone(NAMESPACE, cid);
						} else {
							stateQueue.setError(NAMESPACE, cid);
						}
					}
				}
			}

			log.info("Done with task!");

			return null;
		}

	}

	private int calculateStockPriceTimeRange(DailyPrice dailyPrice) {
		int maxNumOfDay = 15;
		if (dailyPrice == null) {
			return maxNumOfDay;
		}

		Calendar currentTimestamp = Calendar.getInstance();
		log.info("Running calculateStockPriceTimeRange for {} time {}", dailyPrice.getCompanyId(),
				currentTimestamp.getTime());
		Calendar latestStockPriceTimestamp = Calendar.getInstance();
		latestStockPriceTimestamp.setTime(new Date(dailyPrice.getDate() * 1000));
		Calendar lastRunTimestamp = Calendar.getInstance();
		lastRunTimestamp.setTime(new Date(dailyPrice.getUpdateDate()));

		latestStockPriceTimestamp.add(Calendar.MINUTE, dailyPrice.getMarketCloseMinute());
		if (lastRunTimestamp.after(latestStockPriceTimestamp)) {
			latestStockPriceTimestamp.add(Calendar.DATE, 1);
		}
		latestStockPriceTimestamp.set(Calendar.HOUR_OF_DAY, 0);
		latestStockPriceTimestamp.set(Calendar.MINUTE, 0);
		latestStockPriceTimestamp.set(Calendar.SECOND, 0);
		latestStockPriceTimestamp.set(Calendar.MILLISECOND, 0);
		log.info("Running calculateStockPriceTimeRange for {} time {} vs {}", dailyPrice.getCompanyId(),
				currentTimestamp.getTime(), latestStockPriceTimestamp.getTime());
		int numOfDay = 0;
		while (true) {
			switch (latestStockPriceTimestamp.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.SATURDAY:
			case Calendar.SUNDAY:
				latestStockPriceTimestamp.add(Calendar.DATE, 1);
				continue;
			}

			if (latestStockPriceTimestamp.before(currentTimestamp) && numOfDay < maxNumOfDay) {
				latestStockPriceTimestamp.add(Calendar.DATE, 1);
				numOfDay += 1;
			} else {
				break;
			}
		}

		return numOfDay;
	}
	
}
