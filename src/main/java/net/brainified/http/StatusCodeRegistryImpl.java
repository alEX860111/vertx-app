package net.brainified.http;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Maps;

final class StatusCodeRegistryImpl implements StatusCodeRegistry {

  private Map<Class<? extends Throwable>, Integer> exceptionsToStatusCodes;

  public StatusCodeRegistryImpl() {
    exceptionsToStatusCodes = Maps.newHashMap();
  }

  public StatusCodeRegistry registerThrowable(final Class<? extends Throwable> clazz, final Integer statusCode) {
    exceptionsToStatusCodes.put(clazz, statusCode);
    return this;
  }

  @Override
  public Optional<Integer> getStatusCode(final Throwable throwable) {
    return Optional.ofNullable(exceptionsToStatusCodes.get(throwable.getClass()));
  }

}
