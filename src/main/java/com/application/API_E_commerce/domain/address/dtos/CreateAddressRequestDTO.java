package com.application.API_E_commerce.domain.address.dtos;

public record CreateAddressRequestDTO( String street, String city, String state, String country, String zipCode ) {
}
