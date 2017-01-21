package net.brainified.db;

public final class DaoDuplicateKeyException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public DaoDuplicateKeyException(final String msg, final Throwable cause) {
    super(msg, cause);
  }

}
