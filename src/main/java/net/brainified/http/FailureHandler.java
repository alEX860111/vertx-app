package net.brainified.http;

import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.RoutingContext;

final class FailureHandler implements Handler<RoutingContext> {

  @Override
  public void handle(final RoutingContext routingContext) {
    final Throwable failure = routingContext.failure();
    if (failure instanceof HandlerException) {
      final HandlerException handlerException = (HandlerException) failure;
      routingContext.response().setStatusCode(handlerException.getStatusCode()).end(handlerException.getMessage());
    } else {
      routingContext.next();
    }
  }

}
