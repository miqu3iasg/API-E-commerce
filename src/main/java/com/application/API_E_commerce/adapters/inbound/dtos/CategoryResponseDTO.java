package com.application.API_E_commerce.adapters.inbound.dtos;

import java.util.List;
import java.util.UUID;

public class CategoryResponseDTO {

	private UUID id;
	private String name;
	private String description;
	private List<ProductResponseDTO> products;

	public CategoryResponseDTO (UUID id, String name, String description, List<ProductResponseDTO> products) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.products = products;
	}

	public List<ProductResponseDTO> getProducts () {
		return products;
	}

	public void setProducts (List<ProductResponseDTO> products) {
		this.products = products;
	}

	public String getDescription () {
		return description;
	}

	public void setDescription (String description) {
		this.description = description;
	}

	public String getName () {
		return name;
	}

	public void setName (String name) {
		this.name = name;
	}

	public UUID getId () {
		return id;
	}

	public void setId (UUID id) {
		this.id = id;
	}

}
