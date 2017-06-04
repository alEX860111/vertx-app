package net.brainified.http;

import java.util.Optional;

import javax.inject.Inject;

import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.ext.web.RoutingContext;

final class FailureHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(FailureHandler.class);

  private final StatusCodeRegistry registry;

  @Inject
  public FailureHandler(final StatusCodeRegistry registry) {
    this.registry = registry;
  }

  @Override
  public void handle(final RoutingContext routingContext) {
    final Throwable failure = routingContext.failure();

    LOGGER.error(failure.getMessage(), failure);

    final Optional<Integer> statusCodeOptional = registry.getStatusCode(failure);
    if (statusCodeOptional.isPresent()) {
      routingContext.response().putHeader("Content-Type", "application/json; charset=utf-8")
          .setStatusCode(statusCodeOptional.get())
          .end(Json.encodePrettily(FailureMessage.create(failure.getMessage())));
    } else {
      routingContext.next();
    }

  }

}
