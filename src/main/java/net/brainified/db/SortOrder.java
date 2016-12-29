package net.brainified.db;

public enum SortOrder {

  ASC(1), DESC(-1);

  private final int value;

  SortOrder(final int value) {
    this.value = value;
  }

  int getValue() {
    return value;
  }

  public static SortOrder fromString(final String string) {
    try {
      return SortOrder.valueOf(string.toUpperCase());
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

}
