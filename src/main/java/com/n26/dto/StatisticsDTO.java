package com.n26.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatisticsDTO {
	
	private final BigDecimal sum;
	private final BigDecimal avg;
	private final BigDecimal max;
	private final BigDecimal min;
	private final long count;
	
}
