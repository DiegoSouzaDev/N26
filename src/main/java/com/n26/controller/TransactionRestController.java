
package com.n26.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.n26.dto.Transaction;
import com.n26.exception.FutureTransactionException;
import com.n26.exception.TransactionTooOldException;
import com.n26.service.TransactionService;

@RestController
@RequestMapping(path = "/transactions")
public class TransactionRestController {

	private final TransactionService service;

	@Autowired
	public TransactionRestController(final TransactionService service) {
		this.service = service;
	}
	
	@PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Void> saveTransaction(@RequestBody final Transaction transaction) throws FutureTransactionException, TransactionTooOldException {
		final Instant now = Instant.now();
		service.saveTransaction(transaction, now);
		return new ResponseEntity<Void>(HttpStatus.CREATED);
	}

	@DeleteMapping()
	public ResponseEntity<Void> cleanUpTransaction() {
		service.cleanUpRepositoryContent();
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
}
