package net.brainified.http;

public final class InvalidParametersException extends IllegalArgumentException {

  private static final long serialVersionUID = 1L;

  public InvalidParametersException(final String msg) {
    super(msg);
  }

  public InvalidParametersException(final String msg, final Throwable cause) {
    super(msg, cause);
  }

}
