package com.cts.fsebkend.stockservice.controllers;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cts.fsebkend.stockservice.models.Stock;
import com.cts.fsebkend.stockservice.response.StockResponse;
import com.cts.fsebkend.stockservice.services.StockService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/api/v1.0/market/stock")
@Api(value="stock-service", description="Stock Service APIs for E Stock Market")
public class StockController {
	
	Logger log = LoggerFactory.getLogger(StockController.class);

	@Autowired
	private StockService stockService;
	
	@Autowired
	private CompanyServiceProxy companyServiceProxy;
	
	@ApiOperation(value = "View the list of all available stocks")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
            @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
    }
    )
	@GetMapping("/getall")
	public ResponseEntity<?> getAllStocks() {
		ResponseEntity<?> resp = null;
		try {
			List<Stock> allStockList = stockService.getAllStocks();
			resp = new ResponseEntity<>(allStockList, HttpStatus.OK);
		}catch(Exception ex) {
			log.error("An exception occurred while fetching all available stocks >> "+ex.getMessage());
			resp = new ResponseEntity<>("An exception occurred while fetching all available stocks!!", HttpStatus.BAD_GATEWAY);
		}
		return resp;
	}
	
	@ApiOperation(value = "Add a new stock against a company to E Stock Market portal")
	@PostMapping("/add/{companycode}")
	public ResponseEntity<?> addStock(
			@RequestBody Stock stock , @PathVariable("companycode") String companyCode ) {
		log.debug("within addStock..");
		//input validation
		validateStockInputs(stock);
		stock.setCompanyCode(companyCode);
		try {
			Stock addedStock = stockService.addStock(stock);
			return new ResponseEntity<>(addedStock, HttpStatus.CREATED);
		}catch(Exception ex) {
			log.error("An error occurred while adding a stock against company code: "+companyCode+" due to: "+ex.getMessage());
			return new ResponseEntity<>("An error occurred while adding a stock against company code: "+companyCode
					+" in E-Stock Market Portal!!", HttpStatus.BAD_GATEWAY);
		}
	}
	
	@ApiOperation(value = "View all stock details of a comapany using company code for a specified time period")
	@GetMapping("/get/{companycode}/{startdate}/{enddate}")
	public ResponseEntity<?> getStocksByCompanyCodeAndStartDateEndDate(@PathVariable("companycode") String companyCode,
			@PathVariable("startdate") String startDate,
			@PathVariable("enddate") String endDate) {
		log.debug("within getStocksByCompanyCodeAndStartDateEndDate with inputs: companyCode="+companyCode
				+", startDate="+startDate+" and endDate="+endDate);
		StockResponse response = null;
		try {
			String companyName = companyServiceProxy.getCompanyNameByCompanyCode(companyCode);
			if(companyName == null) {
				log.debug("Company code is invalid !!");
				response = new StockResponse();
				response.setErrorMsg("Company having company code: "+companyCode+" does not exist !!");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			List<Stock> desiredStockList = stockService.getStocksByCompanyCodeAndStartDateEndDate(companyCode, startDate, endDate);
			if(desiredStockList.isEmpty()) {
				log.debug("No stock exist for company having company code: "+companyCode
						+" within the mentioned time interval!!");
				response = new StockResponse();
				response.setErrorMsg("No stock exist for company having company code: "+companyCode
						+" within the mentioned time frame !!");
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			
			//response by factory-method-design-pattern
			response = stockService.prepareStockResponse(desiredStockList);
			response.setCompanyName(companyName);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}catch(Exception ex) {
			log.error("An exception occurred while fetching stocks by company code: "+companyCode
					+" within the specific time interval >> "+ex.getMessage());
			response = new StockResponse();
			response.setErrorMsg("An exception occurred while fetching stocks by company code: "+companyCode
					+" within the specific time interval!!");
			return new ResponseEntity<>(response, HttpStatus.BAD_GATEWAY);
		}
	}
	
	@ApiOperation(value = "Delete all stocks for a company from E Stock Market portal")
	@GetMapping("/delete/{companycode}")
	public Boolean deleteStocksByCompanyCode(@PathVariable("companycode") String companyCode) {
		
		log.debug("within deleteStocksByCompanyCode..");
		Boolean isStocksDeleted = false;
		try {
			stockService.deleteStocksByCompanyCode(companyCode);
			isStocksDeleted = true;
		}catch(Exception ex) {
			log.error("An exception occurred while deleting the stocks by companyCode >> "+ex.getMessage());
			isStocksDeleted = false;
		}
		return isStocksDeleted;
	}
	
	@ApiOperation(value = "View latest stock price of a company")
	@GetMapping("/get/latest-stock-price/{companycode}")
	public Double getLatestStockPriceByCompanyCode(@PathVariable("companycode") String companyCode) {
		log.debug("within getLatestStockPriceByCompanyCode..");
		Double latestStockPrice = 0.0;
		try {
			latestStockPrice = stockService.getLatestStockPriceByCompanyCode(companyCode);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return latestStockPrice;
	}
	
	private void validateStockInputs(Stock stock) {
		
		if(stock == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock object cannot be null!!");
		}
		double stockPrice = stock.getPrice();
		if(Math.round(stockPrice) == stockPrice) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock price must be a fractional value!!");
		}
		if(!StringUtils.isBlank(stock.getDate())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
					"Date of stock must be fetched from server system only!!");
		}
		if(!StringUtils.isBlank(stock.getTime())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
					"Time of stock must be fetched from server system only!!");
		}
	}
}
