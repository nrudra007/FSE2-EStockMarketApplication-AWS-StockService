package com.cts.fsebkend.stockservice.services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cts.fsebkend.stockservice.models.Stock;
import com.cts.fsebkend.stockservice.repositories.StockRepository;
import com.cts.fsebkend.stockservice.response.DoStockCalculation;
import com.cts.fsebkend.stockservice.response.StockCalculation;
import com.cts.fsebkend.stockservice.response.StockCalculationFactory;
import com.cts.fsebkend.stockservice.response.StockCalculationType;
import com.cts.fsebkend.stockservice.response.StockResponse;

@Service
public class StockService {
	
	Logger log = LoggerFactory.getLogger(StockService.class);

	@Autowired
	private StockRepository stockRepository;

	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static final String DATE_PARSING_EXP_MSG = "Date parsing exception occurred!!";

	public Stock addStock(Stock stock) {
		log.debug("within addStock of StockService..");
		String stockCreationDateTime = dateFormat.format(new Date());
		String[] stockCreationDateTimeArr = stockCreationDateTime.split(" ");
		String stockCretionDate = (stockCreationDateTimeArr.length >= 1) ? stockCreationDateTimeArr[0] : null;
		String stockCretionTime = (stockCreationDateTimeArr.length >= 2) ? stockCreationDateTimeArr[1] : null;
		stock.setDate(stockCretionDate);
		stock.setTime(stockCretionTime);
		return stockRepository.save(stock);
	}
	
	public List<Stock> getAllStocks() {
		return stockRepository.findAll();
	}

	public List<Stock> getStocksByCompanyCodeAndStartDateEndDate(String companyCode, String startDateStr, String endDateStr) throws Exception {
		List<Stock> desiredStockList = new ArrayList<>();
		List<Stock> allStockList = getAllStocks();
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = dateFormat.parse(startDateStr);
		}catch (ParseException e) {
			log.error("startDate is not of format: yyyy-MM-dd hh:mm:ss.. "
					+ "Hence going to make the startDate of that format only");
			startDate = dateFormat.parse(startDateStr+" 23:59:59");
		}
		try {
			endDate = dateFormat.parse(endDateStr);
		}catch (ParseException e) {
			log.error("endDate is not of format: yyyy-MM-dd hh:mm:ss.. "
					+ "Hence going to make the endDate of that format only");
			endDate = dateFormat.parse(endDateStr+" 23:59:59");
		}
		try {
			for(Stock stock : allStockList) {
				Date stockCreationDate = dateFormat.parse(stock.getDate()+" "+stock.getTime());
				if(stock.getCompanyCode().equalsIgnoreCase(companyCode) && 
						stockCreationDate.after(startDate) && stockCreationDate.before(endDate)) {
					desiredStockList.add(stock);
				}
			}
		} catch (ParseException e) {
			throw new Exception(DATE_PARSING_EXP_MSG);
		}
		return desiredStockList;
	}

	public void deleteStocksByCompanyCode(String companyCode) {
		List<Stock> deletedStockList = new ArrayList<>();
		List<Stock> allStockList = getAllStocks();
		for(Stock stock : allStockList) {
			if(stock.getCompanyCode().equalsIgnoreCase(companyCode)) {
				deletedStockList.add(stock);
			}
		}
		
		if(!deletedStockList.isEmpty()) {
			for(Stock deletedStock : deletedStockList) {
				stockRepository.delete(deletedStock);
			}
		}
	}

	public Double getLatestStockPriceByCompanyCode(String companyCode) throws Exception {
		log.debug("getLatestStockPriceByCompanyCode for company code: "+companyCode);
		Double latestStockPrice = 0.0;
		List<Stock> desiredStockList = new ArrayList<>();
		List<Stock> allStockList = getAllStocks();
		List<Date> dates = new ArrayList<>();
		//prepare stock list & stock creation date list by companyCode
		try {
			for(Stock stock : allStockList) {
				if(stock.getCompanyCode().equalsIgnoreCase(companyCode)) {
					desiredStockList.add(stock);
					Date stockCreationDate = dateFormat.parse(stock.getDate()+" "+stock.getTime());
					dates.add(stockCreationDate);
				}
			}
		} catch (ParseException e) {
			throw new Exception(DATE_PARSING_EXP_MSG);
		}
		//calculate the latest stock creation date
		if(dates.isEmpty()) {
			log.error("No stock present for company code: "+companyCode);
			return latestStockPrice;
		}
		Date latestDate = Collections.max(dates);
		
		//calculate the latest stock price of the company
		try {
			for(Stock stock : desiredStockList) {
				Date stockCreationDate = dateFormat.parse(stock.getDate()+" "+stock.getTime());
				if(stockCreationDate.compareTo(latestDate) == 0) {
					latestStockPrice = stock.getPrice();
					break;
				}
			}
		} catch (ParseException e) {
			throw new Exception(DATE_PARSING_EXP_MSG);
		}
		return latestStockPrice;
	}

	public StockResponse prepareStockResponse(List<Stock> desiredStockList) {

		StockResponse response = new StockResponse();
		response.setStockList(desiredStockList);
		
		StockCalculationFactory stcFactory = new StockCalculationFactory();
		StockCalculation maxStc = stcFactory.getStockCalculation(StockCalculationType.MAXSTOCKCALCULATION.toString());
		StockCalculation minStc = stcFactory.getStockCalculation(StockCalculationType.MINSTOCKCALCULATION.toString());
		StockCalculation avgStc = stcFactory.getStockCalculation(StockCalculationType.AVGSTOCKCALCULATION.toString());
		DoStockCalculation doStc = new DoStockCalculation(desiredStockList);
		
		response.setMaxStockPrice((double) Math.round(doStc.getStockPrice(maxStc) * 100) / 100);
		response.setMinStockPrice((double) Math.round(doStc.getStockPrice(minStc) * 100) / 100);
		response.setAvgStockPrice((double) Math.round(doStc.getStockPrice(avgStc) * 100) / 100);
		return response;
	}
}
