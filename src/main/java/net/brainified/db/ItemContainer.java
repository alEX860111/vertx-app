package net.brainified.db;

import java.util.List;

public final class ItemContainer<T> {

  private final Long count;

  private final List<T> items;

  public ItemContainer(final Long count, final List<T> items) {
    this.count = count;
    this.items = items;
  }

  public Long getCount() {
    return count;
  }

  public List<T> getItems() {
    return items;
  }

}
