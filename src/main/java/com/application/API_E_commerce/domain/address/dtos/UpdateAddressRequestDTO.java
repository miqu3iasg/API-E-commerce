package com.application.API_E_commerce.domain.address.dtos;

public record UpdateAddressRequestDTO (String street, String city, String state, String zipCode, String country) {
}
