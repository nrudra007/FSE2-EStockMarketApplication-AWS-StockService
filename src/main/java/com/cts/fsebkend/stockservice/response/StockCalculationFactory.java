package com.cts.fsebkend.stockservice.response;

public class StockCalculationFactory {

	public StockCalculation getStockCalculation(String stockCalculationType) {
		if(stockCalculationType == null){  
			return null;  
		}
		if(stockCalculationType.equalsIgnoreCase(StockCalculationType.MAXSTOCKCALCULATION.toString())) {  
			return new MaxStockCalculation();  
		}   
		else if(stockCalculationType.equalsIgnoreCase(StockCalculationType.MINSTOCKCALCULATION.toString())){  
			return new MinStockCalculation();  
		}   
		else if(stockCalculationType.equalsIgnoreCase(StockCalculationType.AVGSTOCKCALCULATION.toString())) {  
			return new AvgStockCalculation();  
		} 
		return null;
	}
}
