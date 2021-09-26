package com.cts.fsebkend.stockservice.repositories;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.cts.fsebkend.stockservice.models.Stock;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Repository
public class StockRepository {

	Logger log = LoggerFactory.getLogger(StockRepository.class);

	private static final String REDIS_INDEX_KEY = "E_STOCK_MARKET_STOCK_KEY";

	@Autowired
	RedisTemplate<String, Stock> redisTemplate;

	public List<Stock> findAll() {
		log.debug("*******within findAll()..");
		List<Stock> allStocks = new ArrayList<Stock>();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Gson gson = new Gson();
			Map<Object, Object> entries = redisTemplate.opsForHash().entries(REDIS_INDEX_KEY);
			for(Entry<Object, Object> entry : entries.entrySet()){
				log.debug("*******stock entry: "+entry.getValue());
				String json = gson.toJson(entry.getValue(), LinkedHashMap.class);
				log.debug("*******stock JSON string: "+json);
				Stock stock = objectMapper.readValue(json, Stock.class);
				allStocks.add(stock);
			}
		}catch(Exception ex) {
			log.error("An exception occurred >> "+ex.getMessage());
		}

		return allStocks;
	}

	public Stock save(Stock newStock) {
		log.debug("*******within save().. "+newStock);
		Stock stock = null;
		try {
			redisTemplate.opsForHash().put(REDIS_INDEX_KEY, newStock.getId(), newStock);
			log.debug("*******stock added successfully: "+newStock);
			ObjectMapper objectMapper = new ObjectMapper();
			Gson gson = new Gson();
			String json = gson.toJson(redisTemplate.opsForHash().get(REDIS_INDEX_KEY, newStock.getId()), LinkedHashMap.class);
			stock = objectMapper.readValue(json, Stock.class);
		} catch (Exception ex) {
			log.error("An exception occurred >> "+ex.getMessage());
		}
		return stock;
	}

	public void delete(Stock deletedStock) {
		redisTemplate.opsForHash().delete(REDIS_INDEX_KEY, deletedStock.getId());
	}
}
