package com.cts.fsebkend.stockservice.response;

import java.util.List;

import com.cts.fsebkend.stockservice.models.Stock;

public class StockResponse {

	private String companyName;
	private List<Stock> stockList;
	private double maxStockPrice;
	private double minStockPrice;
	private double avgStockPrice;
	private String errorMsg;
	
	/**
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}
	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	/**
	 * @return the stockList
	 */
	public List<Stock> getStockList() {
		return stockList;
	}
	/**
	 * @param stockList the stockList to set
	 */
	public void setStockList(List<Stock> stockList) {
		this.stockList = stockList;
	}
	/**
	 * @return the maxStockPrice
	 */
	public double getMaxStockPrice() {
		return maxStockPrice;
	}
	/**
	 * @param maxStockPrice the maxStockPrice to set
	 */
	public void setMaxStockPrice(double maxStockPrice) {
		this.maxStockPrice = maxStockPrice;
	}
	/**
	 * @return the minStockPrice
	 */
	public double getMinStockPrice() {
		return minStockPrice;
	}
	/**
	 * @param minStockPrice the minStockPrice to set
	 */
	public void setMinStockPrice(double minStockPrice) {
		this.minStockPrice = minStockPrice;
	}
	/**
	 * @return the avgStockPrice
	 */
	public double getAvgStockPrice() {
		return avgStockPrice;
	}
	/**
	 * @param avgStockPrice the avgStockPrice to set
	 */
	public void setAvgStockPrice(double avgStockPrice) {
		this.avgStockPrice = avgStockPrice;
	}
	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}
	/**
	 * @param errorMsg the errorMsg to set
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}