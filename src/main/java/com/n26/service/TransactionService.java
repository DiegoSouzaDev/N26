package com.n26.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.n26.dto.StatisticsDTO;
import com.n26.dto.Transaction;
import com.n26.exception.FutureTransactionException;
import com.n26.exception.NoEligibleTransactionsException;
import com.n26.exception.TransactionTooOldException;
import com.n26.repository.TransactionMemoryRepository;
import com.n26.repository.TransactionRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransactionService {
	
	private static final long CLEANUP_TIMER = 120000L;
	private static final int VALID_INTERVAL = 60;
	private static final int FIELD_PRECISION = 2;
	private final TransactionRepository repository;

	@Autowired
	public TransactionService(final TransactionMemoryRepository memoryRepository) {
		repository = memoryRepository;
	}
	
	@Scheduled(fixedRate = CLEANUP_TIMER)
	public void cleanUpOldTransactions() {
		final Instant now = Instant.now();
		log.info("STARTING REPOSITORY CLEAN UP");
		repository.removeOldTransactionfromStorage(getBaseInstantPoint(now));
	}

	public void saveTransaction(final Transaction transaction, final Instant now) throws FutureTransactionException, TransactionTooOldException {
		validateTransactionDate(transaction, now);
		repository.saveTransaction(transaction);

	}

	public String getStatistics(final Instant now) throws NoEligibleTransactionsException {
		final Instant basePoint = getBaseInstantPoint(now);
		final List<Transaction> transactionList = repository.getEligibleTransactionList(basePoint);
		final long numberOfTransactions = transactionList.size();
		
		if (numberOfTransactions == 0) {
			throw new NoEligibleTransactionsException();
		}

		final DoubleSummaryStatistics amountSummaryStatistics = transactionList.stream().collect(Collectors.summarizingDouble(Transaction::getAmount));

		final BigDecimal sumAmount = getFormatedBigDecimal(amountSummaryStatistics.getSum());
		final BigDecimal maxAmount = getFormatedBigDecimal(amountSummaryStatistics.getMax());
		final BigDecimal minAmount = getFormatedBigDecimal(amountSummaryStatistics.getMin());
		final BigDecimal averageAmount = getFormatedBigDecimal(amountSummaryStatistics.getAverage());
		
		final StatisticsDTO statisticsDTO = new StatisticsDTO(sumAmount, averageAmount, maxAmount, minAmount, numberOfTransactions);

		return statisticsToString(statisticsDTO);
		
	}
	
	protected String statisticsToString(final StatisticsDTO statisticsDTO) {
		final StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{\"sum\":\"").append(statisticsDTO.getSum())
					 .append("\",\"avg\":\"").append(statisticsDTO.getAvg())
					 .append("\",\"max\":\"").append(statisticsDTO.getMax())
					 .append("\",\"min\":\"").append(statisticsDTO.getMin())
					 .append("\",\"count\":").append(statisticsDTO.getCount()).append("}");
		return stringBuilder.toString();
		
	}

	private Instant getBaseInstantPoint(final Instant now) {
		return now.minus(VALID_INTERVAL, ChronoUnit.SECONDS);
	}
	
	protected BigDecimal getFormatedBigDecimal(final Double amountStatistic) {
		return new BigDecimal(amountStatistic).setScale(FIELD_PRECISION, BigDecimal.ROUND_HALF_UP);
	}

	public void validateTransactionDate(final Transaction transaction, final Instant now) throws FutureTransactionException, TransactionTooOldException {
		final Instant baseInstantPoint = getBaseInstantPoint(now);
		if (transaction.getTimestamp().isAfter(now)) {
			throw new FutureTransactionException();
		}

		if (transaction.getTimestamp().isBefore(baseInstantPoint)) {
			throw new TransactionTooOldException();
		}
	}
	
	public void cleanUpRepositoryContent() {
		repository.cleanUpRepositoryContent();
	}
	
}
