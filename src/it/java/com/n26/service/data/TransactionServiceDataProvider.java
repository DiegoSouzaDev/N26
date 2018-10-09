package com.n26.service.data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

import com.n26.dto.StatisticsDTO;
import com.n26.dto.Transaction;

@Component
public class TransactionServiceDataProvider {

	public static final String VALID_STATISTICS_EXPECTED_STRING = "{\"sum\":\"300.00\",\"avg\":\"150.00\",\"max\":\"200.00\",\"min\":\"100.00\",\"count\":2}";
	
	public String getValidStatisticsExpectedString() {
		return VALID_STATISTICS_EXPECTED_STRING;
	}

	public List<Transaction> getValidEligibleTransactionList(Instant now) {
		final Transaction transaction1 = new Transaction();
		transaction1.setAmount(100.00);
		transaction1.setTimestamp(now);
		final Transaction transaction2 = new Transaction();
		transaction2.setAmount(200.00);
		transaction2.setTimestamp(now);

		return Arrays.asList(transaction1, transaction2);
	}
	
	public StatisticsDTO getStatisticsDTO() {
		final BigDecimal sum = new BigDecimal("99999.00");
		final BigDecimal avg = new BigDecimal("0.99");
		final BigDecimal max = new BigDecimal("999.99");
		final BigDecimal min = new BigDecimal("0.00");
		final long count = 9;
		
		return new StatisticsDTO(sum, avg, max, min, count);
	}
	
	public Transaction getValidRoundUpValueTransaction(final Instant now) {
		final Transaction transaction = new Transaction();
		transaction.setAmount(161.1677777);
		transaction.setTimestamp(now);
		
		return transaction;
	}
	
	public Transaction getOldTransaction(final Instant now) {
		final Instant timestamp = now.minus(80, ChronoUnit.SECONDS);
		
		final Transaction transaction = new Transaction();
		transaction.setAmount(161.1677777);
		transaction.setTimestamp(timestamp);
		
		return transaction;
	}
	
	public Transaction getFutureDatedTransaction(final Instant now) {
		final Instant timestamp = now.plus(60, ChronoUnit.SECONDS);

		final Transaction transaction = new Transaction();
		transaction.setAmount(161.1677777);
		transaction.setTimestamp(timestamp);

		return transaction;
	}
	
}
