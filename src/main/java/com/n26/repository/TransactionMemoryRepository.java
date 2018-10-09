package com.n26.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.n26.dto.Transaction;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@Qualifier(value = "MemoryRepository")
public class TransactionMemoryRepository implements TransactionRepository {
	@Getter
	private final ConcurrentHashMap<String, Transaction> transactionStorage = new ConcurrentHashMap<String, Transaction>();
	
	@Override
	public List<Transaction> getEligibleTransactionList(final Instant basePoint) {
		return transactionStorage.values().stream().filter(t -> t.getTimestamp().isAfter(basePoint)).collect(Collectors.toList());
	}
	
	@Override
	public void removeOldTransactionfromStorage(final Instant basePoint) {
		log.info("Storage transaction number BEFORE cleanup: {}", transactionStorage.size());
		transactionStorage.values().removeIf(transaction -> transaction.getTimestamp().isBefore(basePoint));
		log.info("Storage transaction number AFTER cleanup: {}", transactionStorage.size());
	}

	@Override
	public void saveTransaction(Transaction transaction) {
		final String key = UUID.randomUUID().toString();
		transactionStorage.put(key, transaction);
	}
	
	@Override
	public void cleanUpRepositoryContent() {
		transactionStorage.clear();
	}

}
