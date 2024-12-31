package com.application.API_E_commerce.domain.cart.cartitem;

import com.application.API_E_commerce.domain.cart.Cart;
import com.application.API_E_commerce.domain.product.Product;

import java.util.UUID;

public class CartItem {
  private UUID id;
  private Cart cart;
  private Product product;
  private int quantity;

  public CartItem() {
  }

  public CartItem(UUID id, Cart cart, Product product, int quantity) {
    this.id = id;
    this.cart = cart;
    this.product = product;
    this.quantity = quantity;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Cart getCart() {
    return cart;
  }

  public void setCart(Cart cart) {
    this.cart = cart;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }
}
