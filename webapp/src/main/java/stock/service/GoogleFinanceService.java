package stock.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import stock.to.Price;
import stock.to.DailyPrice;
import stock.to.StockMetadata;

@Service
public class GoogleFinanceService {
	@Autowired
	RestTemplate restTemplate;

	@Autowired
	JdbcTemplate jdbcTemplate;

	private static final Logger log = LoggerFactory.getLogger(GoogleFinanceService.class);

	@Value("${webapp.google_finance_api_url}")
	private String url;

	public List<DailyPrice> getPrices(String ticker, long companyId, int numberOfDay) throws IOException {
		String res = restTemplate.getForObject(String.format(url, numberOfDay, ticker), String.class);
		BufferedReader reader = new BufferedReader(new StringReader(res));
		List<DailyPrice> pricesList = new ArrayList<DailyPrice>();

		String exchange = java.net.URLDecoder.decode(reader.readLine(), "UTF-8").split("=")[1];
		Integer marketOpenMinute = Integer.valueOf(reader.readLine().split("=")[1]);
		Integer marketCloseMinute = Integer.valueOf(reader.readLine().split("=")[1]);
		Integer interval = Integer.valueOf(reader.readLine().split("=")[1]);
		reader.readLine();
		reader.readLine();
		Integer timezoneOffset = Integer.valueOf(reader.readLine().split("=")[1]);

		String r = reader.readLine();
		Long b = null;
		String[] ps = null;
		DailyPrice prices = null;
		Integer i = null;
		while (r != null) {
			Long t = null;
			if (r.startsWith("a")) {
				r = r.substring(1);
				ps = r.split(",");
				b = Long.valueOf(ps[0]);
				t = Long.valueOf(ps[0]);

				prices = new DailyPrice(390);
				StockMetadata metadata = new StockMetadata();
				metadata.setExchange(exchange);
				metadata.setInterval(interval);
				metadata.setMarketOpenMinute(marketOpenMinute);
				metadata.setMarketCloseMinute(marketCloseMinute);
				metadata.setTimezoneOffset(timezoneOffset);
				metadata.setDate(b / 3600 / 24 * 3600 * 24);
				metadata.setCompanyId(companyId);
				metadata.setExchange(exchange);
				prices.setMetadata(metadata);

				pricesList.add(prices);
				i = pricesList.size() - 1;
			} else if (r.startsWith("TIMEZONE_OFFSET")) {
				pricesList.get(i).getMetadata().setTimezoneOffset(Integer.valueOf(r.split("=")[1]));
				r = reader.readLine();
				continue;
			} else {
				ps = r.split(",");
				t = b + Long.valueOf(ps[0]) * prices.getMetadata().getInterval();
			}

			Price p = new Price();
			p.setTimestamp(t);
			p.setClose(Double.valueOf(ps[1]));
			p.setHigh(Double.valueOf(ps[2]));
			p.setLow(Double.valueOf(ps[3]));
			p.setOpen(Double.valueOf(ps[4]));
			p.setVolume(Long.valueOf(ps[5]));
			p.setCompanyId(companyId);

			pricesList.get(i).getPriceList().add(p);

			r = reader.readLine();
		}

		log.info(prices.toString());

		return pricesList;
	}
}
