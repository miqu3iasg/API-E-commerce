package com.application.API_E_commerce.adapters.inbound.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFiltersCriteria {

	private String name;
	private String description;
	private BigDecimal minPrice;
	private BigDecimal maxPrice;
	private UUID categoryId;

	public ProductFiltersCriteria () {
	}

	public String getName () {
		return name;
	}

	public void setName (String name) {
		this.name = name;
	}

	public String getDescription () {
		return description;
	}

	public void setDescription (String description) {
		this.description = description;
	}

	public BigDecimal getMinPrice () {
		return minPrice;
	}

	public void setMinPrice (BigDecimal minPrice) {
		this.minPrice = minPrice;
	}

	public BigDecimal getMaxPrice () {
		return maxPrice;
	}

	public void setMaxPrice (BigDecimal maxPrice) {
		this.maxPrice = maxPrice;
	}

	public UUID getCategoryId () {
		return categoryId;
	}

	public void setCategoryId (UUID categoryId) {
		this.categoryId = categoryId;
	}

}
