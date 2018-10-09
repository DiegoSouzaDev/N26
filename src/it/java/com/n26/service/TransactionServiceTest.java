package com.n26.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.n26.dto.StatisticsDTO;
import com.n26.dto.Transaction;
import com.n26.exception.FutureTransactionException;
import com.n26.exception.NoEligibleTransactionsException;
import com.n26.exception.TransactionTooOldException;
import com.n26.repository.TransactionMemoryRepository;
import com.n26.service.data.TransactionServiceDataProvider;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {
	@Captor
	private ArgumentCaptor<Transaction> transactionCaptor;
	
	@InjectMocks
	private TransactionService service;

	@Mock
	private TransactionMemoryRepository repository;
	
	private TransactionServiceDataProvider provider;
	
	@Before
	public void init() {
		provider = new TransactionServiceDataProvider();
	}
	
	@Test(expected = NoEligibleTransactionsException.class)
	public void getStatisticsWithNoValidTransactionsThrowsExceptionTest() throws NoEligibleTransactionsException {
		final Instant now = Instant.now();
		
		when(repository.getEligibleTransactionList(any())).thenReturn(new ArrayList<>());
		
		service.getStatistics(now);
	}

	@Test()
	public void getValidStatisticsTest() throws NoEligibleTransactionsException {
		final String expectedString = provider.getValidStatisticsExpectedString();
		final Instant now = Instant.now();
		final List<Transaction> eligibleList = provider.getValidEligibleTransactionList(now);

		when(repository.getEligibleTransactionList(any())).thenReturn(eligibleList);
		
		final String actualString = service.getStatistics(now);

		assertThat(actualString, equalTo(expectedString));
	}

	@Test
	public void statisticsToStringTest() {
		final String expectedString = "{\"sum\":\"99999.00\",\"avg\":\"0.99\",\"max\":\"999.99\",\"min\":\"0.00\",\"count\":9}";
		final StatisticsDTO statistics = provider.getStatisticsDTO();

		final String actualString = service.statisticsToString(statistics);

		assertThat(actualString, equalTo(expectedString));
	}

	@Test
	public void saveValidTransactionTest() throws FutureTransactionException, TransactionTooOldException {
		final Instant now = Instant.now();
		final Transaction transaction = provider.getValidRoundUpValueTransaction(now);
		
		service.saveTransaction(transaction, now);
		verify(repository, times(1)).saveTransaction(transactionCaptor.capture());

		assertThat(transaction.getAmount(), equalTo(transactionCaptor.getValue().getAmount()));
	}

	@Test(expected = TransactionTooOldException.class)
	public void saveOldTransactionThrowsExceptionTest() throws FutureTransactionException, TransactionTooOldException {
		final Instant now = Instant.now();
		
		final Transaction transaction = provider.getOldTransaction(now);

		service.saveTransaction(transaction, now);
	}

	@Test(expected = FutureTransactionException.class)
	public void saveTransactionWithFutureTimestampThrowsExceptionTest() throws FutureTransactionException, TransactionTooOldException {
		final Instant now = Instant.now();
		
		final Transaction transaction = provider.getFutureDatedTransaction(now);

		service.saveTransaction(transaction, now);
	}
	
	@Test
	public void cleanUpOldTransactionsTest() {
		service.cleanUpOldTransactions();
		verify(repository, times(1)).removeOldTransactionfromStorage(any());
	}
	
	@Test
	public void getAverageAmountRoundUPResultTest() {
		final Double startAmount = 161.1977777;
		final BigDecimal expectedAmount = new BigDecimal("161.20");
		
		final BigDecimal actualAmount = service.getFormatedBigDecimal(startAmount);

		assertThat(actualAmount, equalTo(expectedAmount));
	}

	@Test
	public void getMinAmountRoundRoundUPResultTest() {
		final Double startAmount = 161.195001;
		final BigDecimal expectedAmount = new BigDecimal("161.20");
		
		final BigDecimal actualAmount = service.getFormatedBigDecimal(startAmount);

		assertThat(actualAmount, equalTo(expectedAmount));
	}
	
	@Test
	public void getMaxAmountRoundRoundUPResultTest() {
		final Double startAmount = 161.195000;
		final BigDecimal expectedAmount = new BigDecimal("161.19");
		
		final BigDecimal actualAmount = service.getFormatedBigDecimal(startAmount);
		
		assertThat(actualAmount, equalTo(expectedAmount));
	}
	
	@Test
	public void getSummarizedAmount() {
		final Double startAmount = 161.0000000000;
		final BigDecimal expectedAmount = new BigDecimal("161.00");
		
		final BigDecimal actualAmount = service.getFormatedBigDecimal(startAmount);
		
		assertThat(actualAmount, equalTo(expectedAmount));
	}
	
	@Test
	public void hasACleanUpRepositoryMethodTest() {
		service.cleanUpRepositoryContent();
		verify(repository, times(1)).cleanUpRepositoryContent();
	}
}
