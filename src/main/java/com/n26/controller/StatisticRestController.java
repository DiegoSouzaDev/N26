package com.n26.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.n26.exception.NoEligibleTransactionsException;
import com.n26.service.TransactionService;

@RestController
public class StatisticRestController {
	
	private TransactionService transactionService;
	
	@Autowired
	public void StatisticController(TransactionService transactionService) {
		this.transactionService = transactionService;
	}
	
	@GetMapping(path = "/statistics", produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<String> getStatistics() throws NoEligibleTransactionsException {
		final Instant now = Instant.now();
		final String statistics = transactionService.getStatistics(now);
		return new ResponseEntity<String>(statistics, HttpStatus.OK);
		
	}
}
