package net.brainified.http;

import io.vertx.rxjava.ext.web.RoutingContext;

public interface RoutingContextHelper {

  public <T> T getBody(RoutingContext routingContext, Class<T> clazz);

}