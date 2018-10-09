package com.n26.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.n26.exception.FutureTransactionException;
import com.n26.exception.TransactionTooOldException;
import com.n26.service.TransactionService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TransactionRestControllerTest {
	
	private MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext wac;
	
	@MockBean
	private TransactionService service;
	
	@Before
	public void setup() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void saveValidTransactionTest() throws Exception {
		final String content = "{\"amount\":\"1000.90\", \"timestamp\":\"12312334\"}";
		
		mockMvc.perform(MockMvcRequestBuilders.post("/transactions").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(MockMvcResultMatchers.status().isCreated());
	}
	
	@Test
	public void saveOldTransactionTest() throws Exception {
		final String content = "{\"amount\":\"1000.90\", \"timestamp\":\"12312334\"}";
		
		doThrow(TransactionTooOldException.class).when(service).saveTransaction(any(), any());
		mockMvc.perform(MockMvcRequestBuilders.post("/transactions").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(MockMvcResultMatchers.status().isNoContent());
	}

	@Test
	public void saveTransactionWithTutureDataTest() throws Exception {
		final String content = "{\"amount\":\"1000.90\", \"timestamp\":\"12312334\"}";
		
		doThrow(FutureTransactionException.class).when(service).saveTransaction(any(), any());
		mockMvc.perform(MockMvcRequestBuilders.post("/transactions").contentType(MediaType.APPLICATION_JSON).content(content))
				.andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
	}
	
	@Test
	public void cleanUpRepositoryTest() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/transactions")).andExpect(MockMvcResultMatchers.status().isNoContent());
	}
	
}