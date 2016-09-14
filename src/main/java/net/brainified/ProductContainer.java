package net.brainified;

import java.util.List;

public final class ProductContainer {

  private List<Product> products;

  private Integer numberOfProducts;

  public List<Product> getProducts() {
    return products;
  }

  public void setProducts(List<Product> products) {
    this.products = products;
  }

  public Integer getNumberOfProducts() {
    return numberOfProducts;
  }

  public void setNumberOfProducts(Integer numberOfProducts) {
    this.numberOfProducts = numberOfProducts;
  }

}
