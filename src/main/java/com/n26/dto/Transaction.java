package com.n26.dto;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transaction {
	private double amount;
	private Instant timestamp;
}
