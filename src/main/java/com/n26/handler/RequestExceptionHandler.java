package com.n26.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.n26.exception.FutureTransactionException;
import com.n26.exception.InvalidJsonMessageException;
import com.n26.exception.NoEligibleTransactionsException;
import com.n26.exception.TransactionTooOldException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@RestController
@Slf4j
public class RequestExceptionHandler {
	
	private static final String EMPTY_STATISTICS = "{\"sum\":\"0.00\",\"avg\":\"0.00\",\"max\":\"0.00\",\"min\":\"0.00\",\"count\":0}";

	@ExceptionHandler(NoEligibleTransactionsException.class)
	@ResponseStatus(code = HttpStatus.OK)
	public String noElegibleTransactionHandler(final NoEligibleTransactionsException exception) {
		log.debug("No Statistics to return");
		return EMPTY_STATISTICS;

	}

	@ExceptionHandler(TransactionTooOldException.class)
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void transactionTooOldExceptionHandler(final TransactionTooOldException exception) {
		log.debug("Transaction received is too old");
	}

	@ExceptionHandler(FutureTransactionException.class)
	@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
	public void futureTransactionExceptionHandler(final FutureTransactionException exception) {
		log.debug("Transaction received has a future timestamp");
	}

	@ExceptionHandler(InvalidFormatException.class)
	@ResponseStatus(code = HttpStatus.UNPROCESSABLE_ENTITY)
	public void InvalidFormatExceptionHandler(final InvalidFormatException exception) {
		log.info("Error parsisng object", exception);
	}
	
	@ExceptionHandler(InvalidJsonMessageException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public void InvalidJsonMessageExceptionHandler(final InvalidJsonMessageException exception) {
		log.info("Error parsisng object", exception);
	}
	
}
