package net.brainified.http;

import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.rxjava.ext.web.RoutingContext;

final class FailureHandler implements Handler<RoutingContext> {

  @Override
  public void handle(final RoutingContext routingContext) {
    final Throwable failure = routingContext.failure();
    if (failure instanceof HandlerException) {
      final HandlerException handlerException = (HandlerException) failure;
      routingContext.response()
        .putHeader("Content-Type", "application/json; charset=utf-8")
        .setStatusCode(handlerException.getStatusCode())
        .end(Json.encodePrettily(FailureMessage.create(handlerException.getMessage())));
    } else {
      routingContext.next();
    }
  }

}
