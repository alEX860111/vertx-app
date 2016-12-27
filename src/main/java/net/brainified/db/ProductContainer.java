package net.brainified.db;

import java.util.List;

public final class ProductContainer {

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
