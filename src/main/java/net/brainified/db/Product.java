package net.brainified.db;

public final class Product extends MongoObject {

  public enum SortKey {

    NAME("name"), PRICE("price"), CREATEDAT("createdAt");

    private final String sortKey;

    SortKey(final String sortKey) {
      this.sortKey = sortKey;
    }

    public String getSortKey() {
      return sortKey;
    }

  }

  private String name;

  private Double price;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

}
