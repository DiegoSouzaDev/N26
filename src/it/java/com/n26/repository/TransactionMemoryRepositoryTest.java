package com.n26.repository;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.n26.dto.Transaction;
import com.n26.repository.data.TransactionRepositoryDataProvider;

@RunWith(MockitoJUnitRunner.class)
public class TransactionMemoryRepositoryTest {

	@InjectMocks
	private TransactionMemoryRepository repository;

	private TransactionRepositoryDataProvider provider;
	
	@Before
	public void init() {
		provider = new TransactionRepositoryDataProvider();
	}

	@After
	public void tearDown() {
		repository.getTransactionStorage().clear();
	}
	
	@Test
	public void cleanUpRepositoryTest() {
		repository.getTransactionStorage().putAll(provider.getTransactionMap(Instant.now()));
		final Integer repositoryStartSize = repository.getTransactionStorage().size();
		
		repository.cleanUpRepositoryContent();
		
		assertThat(repositoryStartSize, is(4));
		assertThat(repository.getTransactionStorage().size(), is(0));
	}

	@Test
	public void removeOldTransactonsTest() {
		final Instant now = Instant.now();
		final Instant baseInstantPoint = now.minus(60, ChronoUnit.SECONDS);
		repository.getTransactionStorage().putAll(provider.getTransactionMap(now));
		final Integer repositoryStartSize = repository.getTransactionStorage().size();
		
		repository.removeOldTransactionfromStorage(baseInstantPoint);
		
		assertThat(repositoryStartSize, is(4));
		assertThat(repository.getTransactionStorage().size(), is(3));
	}
	
	@Test
	public void getEligibleTest() {
		final Instant now = Instant.now();
		final Instant baseInstantPoint = now.minus(60, ChronoUnit.SECONDS);
		repository.getTransactionStorage().putAll(provider.getTransactionMap(now));
		final Integer repositoryStartSize = repository.getTransactionStorage().size();
		
		final List<Transaction> transactionList = repository.getEligibleTransactionList(baseInstantPoint);
		
		assertThat(repositoryStartSize, is(4));
		assertThat(transactionList.size(), is(3));
	}
	
	@Test
	public void saveValidTransactionTest() {
		final Transaction transaction = provider.buildTransaction(300.00, Instant.now());
		final Integer repositoryStartSize = repository.getTransactionStorage().size();
		
		repository.saveTransaction(transaction);
		final Integer repositoryEndtSize = repository.getTransactionStorage().size();
		
		assertThat(repositoryStartSize, is(0));
		assertThat(repositoryEndtSize, is(1));
	}
}
