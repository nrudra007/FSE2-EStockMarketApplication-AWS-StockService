package com.cts.fsebkend.stockservice.response;

import java.util.List;

import com.cts.fsebkend.stockservice.models.Stock;

public class DoStockCalculation {

	private List<Stock> stockList;

	public DoStockCalculation(List<Stock> stockList) {
		super();
		this.stockList = stockList;
	}
	
	public double getStockPrice(StockCalculation stc) {
		return stc.getPrice(stockList);
	}
}
