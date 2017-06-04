package net.brainified.http;

final class FailureMessage {

  private final String message;

  private FailureMessage(final String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public static FailureMessage create(final String message) {
    return new FailureMessage(message);
  }

}
