package net.brainified.http;

import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.ext.web.RoutingContext;

final class FailureHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FailureHandler.class);

  @Override
  public void handle(final RoutingContext routingContext) {
    System.out.println("failure");
    final Throwable failure = routingContext.failure();

    LOGGER.error(failure.getMessage(), failure);

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
