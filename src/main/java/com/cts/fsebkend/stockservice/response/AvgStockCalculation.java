package com.cts.fsebkend.stockservice.response;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cts.fsebkend.stockservice.models.Stock;

public class AvgStockCalculation extends StockCalculation {
	
	Logger log = LoggerFactory.getLogger(AvgStockCalculation.class);

	@Override
	public Double getPrice(List<Stock> stockList) {
		List<Double> stockPriceList = new ArrayList<>();
		for(Stock stock : stockList) {
			stockPriceList.add(stock.getPrice());
		}
		if(stockPriceList.isEmpty()) {
			log.error("stockPriceList is empty.. hence returning the avg stock price as 0.0!!");
			return 0.0;
		}
		Double averageStockPrice = stockPriceList.stream().mapToDouble(val -> val).average().orElse(0.0);
		return averageStockPrice;
	}
}
