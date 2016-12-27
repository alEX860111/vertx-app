package net.brainified.http.products;

import java.util.List;

import net.brainified.db.Product;

final class ProductContainer {

  private List<Product> products;

  private Long numberOfProducts;

  public List<Product> getProducts() {
    return products;
  }

  public void setProducts(List<Product> products) {
    this.products = products;
  }

  public Long getNumberOfProducts() {
    return numberOfProducts;
  }

  public void setNumberOfProducts(Long numberOfProducts) {
    this.numberOfProducts = numberOfProducts;
  }

}
