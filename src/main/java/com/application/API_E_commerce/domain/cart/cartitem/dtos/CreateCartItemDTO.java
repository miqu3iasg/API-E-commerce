package com.application.API_E_commerce.domain.cart.cartitem.dtos;

import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.product.Product;

public record CreateCartItemDTO(Cart cart, Product product, int quantity) {
}
