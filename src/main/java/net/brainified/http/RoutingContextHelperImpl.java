package net.brainified.http;

import java.util.Objects;
import java.util.Optional;

import com.google.common.collect.Range;
import com.google.common.primitives.Ints;

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
      throw new HandlerException("Invalid JSON in body.", 400, e);
    }
  }

  @Override
  public <T extends Enum<T>> Optional<T> getParamAsEnum(final RoutingContext routingContext, final String paramName, final Class<T> clazz) {
    final String parameter = routingContext.request().getParam(paramName);
    if (Objects.isNull(parameter)) {
      return Optional.empty();
    }
    try {
      return Optional.of(Enum.valueOf(clazz, parameter.toUpperCase()));
    } catch (IllegalArgumentException e) {
      throw new HandlerException(String.format("Invalid value '%s' for parameter '%s'.", parameter, paramName), 400, e);
    }
  }

  @Override
  public Optional<Integer> getParamAsInteger(final RoutingContext routingContext, final String paramName, final Range<Integer> allowedValues) {
    final String parameter = routingContext.request().getParam(paramName);
    if (Objects.isNull(parameter)) {
      return Optional.empty();
    }
    final Integer paramAsInteger = Ints.tryParse(parameter);
    if (Objects.isNull(paramAsInteger)) {
      throw new HandlerException(String.format("Invalid value '%s' for parameter '%s'. Must be an integer.", parameter, paramName), 400);
    }
    if (!allowedValues.contains(paramAsInteger)) {
      throw new HandlerException(String.format("Invalid value '%s' for parameter '%s'. Must be in range %s.", parameter, paramName, allowedValues), 400);
    }
    return Optional.of(paramAsInteger);
  }

}
