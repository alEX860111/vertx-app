package net.brainified.http;

import javax.inject.Provider;

final class StatusCodeRegistryProvider implements Provider<StatusCodeRegistry> {

  @Override
  public StatusCodeRegistry get() {
    return new StatusCodeRegistryImpl()
        .registerThrowable(IllegalArgumentException.class, 400);
  }

}
