package com.cts.fsebkend.stockservice.response;

public enum StockCalculationType {

	MAXSTOCKCALCULATION("MAX_STOCK_CALCULATION"), 
	MINSTOCKCALCULATION("MIN_STOCK_CALCULATION"), 
	AVGSTOCKCALCULATION("AVG_STOCK_CALCULATION");

	// declaring private variable for getting values
	private String calculationType;

	// getter method
	public String getCalculationType(){
		return this.calculationType;
	}

	private StockCalculationType(String calculationType){
		this.calculationType = calculationType;
	}
	
	@Override
    public String toString() {
        return this.calculationType;
    }
}
