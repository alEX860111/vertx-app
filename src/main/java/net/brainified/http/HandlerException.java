package net.brainified.http;

public final class HandlerException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final int statusCode;
  
  public HandlerException(final String msg, final int statusCode) {
    super(msg);
    this.statusCode = statusCode;
  }

  public HandlerException(final String msg, final int statusCode, final Throwable cause) {
    super(msg, cause);
    this.statusCode = statusCode;
  }

  public int getStatusCode() {
    return statusCode;
  }

}
