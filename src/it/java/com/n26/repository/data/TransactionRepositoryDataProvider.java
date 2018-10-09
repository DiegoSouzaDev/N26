package com.n26.repository.data;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.n26.dto.Transaction;

public class TransactionRepositoryDataProvider {

	public Map<String, Transaction> getTransactionMap(final Instant now) {
		final Map<String, Transaction> transactionMap = new HashMap();
		transactionMap.put(UUID.randomUUID().toString(), buildTransaction(4.00, now));
		transactionMap.put(UUID.randomUUID().toString(), buildTransaction(80.00, now.minus(8, ChronoUnit.SECONDS)));
		transactionMap.put(UUID.randomUUID().toString(), buildTransaction(400.00, now.minus(40, ChronoUnit.SECONDS)));
		transactionMap.put(UUID.randomUUID().toString(), buildTransaction(800.00, now.minus(80, ChronoUnit.SECONDS)));
		
		return transactionMap;

	}
	
	public Transaction buildTransaction(Double amount, Instant timeStamp) {
		final Transaction transaction = new Transaction();
		transaction.setAmount(amount);
		transaction.setTimestamp(timeStamp);
		return transaction;
	}
	
}
