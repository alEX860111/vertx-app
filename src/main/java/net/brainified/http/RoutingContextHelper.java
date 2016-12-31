package net.brainified.http;

import java.util.Optional;

import io.vertx.rxjava.ext.web.RoutingContext;

public interface RoutingContextHelper {

  public <T> T getBody(RoutingContext routingContext, Class<T> clazz);

  public <T extends Enum<T>> Optional<T> getParamAsEnum(RoutingContext routingContext, String paramName, Class<T> clazz);

}