package com.cts.fsebkend.stockservice.response;

import java.util.List;

import com.cts.fsebkend.stockservice.models.Stock;

public abstract class StockCalculation {

	protected Double price;

	public abstract Double getPrice(List<Stock> stockList);
}
