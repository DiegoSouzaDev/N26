package com.n26.repository;

import java.time.Instant;
import java.util.List;

import com.n26.dto.Transaction;

public interface TransactionRepository {

	List<Transaction> getEligibleTransactionList(Instant startPoint);
	
	void removeOldTransactionfromStorage(final Instant basePoint);

	void saveTransaction(Transaction transaction);
	
	void cleanUpRepositoryContent();
}
