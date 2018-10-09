package com.n26.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.n26.exception.NoEligibleTransactionsException;
import com.n26.service.TransactionService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebMvc
public class StatisticRestControllerTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext wac;

	@InjectMocks
	private StatisticRestController controller;
	
	@MockBean
	private TransactionService service;
	
	@Before
	public void init() {
		mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

	}
	
	@Test()
	public void getStatisticsWithNoTransactionReturnsZeroTest() throws Exception {
		final String expected = "{\"sum\":\"0.00\",\"avg\":\"0.00\",\"max\":\"0.00\",\"min\":\"0.00\",\"count\":0}";
		
		when(service.getStatistics(any())).thenThrow(NoEligibleTransactionsException.class);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/statistics")).andExpect(status().isOk()).andExpect(content().string(CoreMatchers.equalTo(expected)));
		
	}

	@Test()
	public void getStatisticsWithTransactionValuesTest() throws Exception {
		final String expected = "{\"sum\":\"10.00\",\"avg\":\"1.00\",\"max\":\"1.00\",\"min\":\"1.00\",\"count\":10}";
		
		when(service.getStatistics(any())).thenReturn(expected);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/statistics")).andExpect(status().isOk()).andExpect(content().string(CoreMatchers.equalTo(expected)));
		
	}
}
