package net.brainified.http;

import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.rxjava.ext.web.RoutingContext;

final class RoutingContextHelperImpl implements RoutingContextHelper {

  @Override
  public <T> T getBody(final RoutingContext routingContext, final Class<T> clazz) {
    final String body = routingContext.getBodyAsString();

    try {
      return Json.decodeValue(body, clazz);
    } catch (final DecodeException e) {
      throw new HandlerException("Invalid JSON in body", e, 400);
    }

  }

}
