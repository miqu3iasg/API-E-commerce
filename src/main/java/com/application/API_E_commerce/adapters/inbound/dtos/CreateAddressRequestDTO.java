package com.application.API_E_commerce.adapters.inbound.dtos;

public record CreateAddressRequestDTO(String street, String city, String state,
                                      String country, String zipCode) {
}
