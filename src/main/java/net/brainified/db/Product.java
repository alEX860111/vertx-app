package net.brainified.db;

public final class Product {

  private String _id;

  private String createdAt;

  private ProductData data;

  public String get_id() {
    return _id;
  }

  public void set_id(String _id) {
    this._id = _id;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public ProductData getData() {
    return data;
  }

  public void setData(ProductData data) {
    this.data = data;
  }

}
