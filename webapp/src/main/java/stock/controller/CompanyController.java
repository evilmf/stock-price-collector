package stock.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import stock.queue.StateQueue;
import stock.service.GoogleFinanceService;

@RestController
public class CompanyController {
	@Autowired
	GoogleFinanceService googleFinanceService;

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	StateQueue stateQueue;

	@RequestMapping(method = RequestMethod.GET, path = "/status")
	public boolean status() {
		return true;
	}
}
