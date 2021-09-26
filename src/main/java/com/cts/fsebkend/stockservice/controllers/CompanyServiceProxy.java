package com.cts.fsebkend.stockservice.controllers;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Configuration
@FeignClient(name="company-service", url = "${LOAD_BALANCER_URL}")
public interface CompanyServiceProxy {

	@GetMapping("/api/v1.0/market/company/name/{companycode}")
	public String getCompanyNameByCompanyCode(@PathVariable("companycode") String companyCode);
}
