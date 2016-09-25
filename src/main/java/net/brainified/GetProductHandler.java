package net.brainified;

import java.util.Objects;

import javax.inject.Inject;

import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

final class GetProductHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetProductHandler.class);

  private final ProductService service;

  @Inject
  public GetProductHandler(final ProductService service) {
    this.service = service;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    final String id = routingContext.request().getParam("id");

    service.getProduct(id, result -> {
      if (result.succeeded()) {
        final JsonObject product = result.result();
        if (Objects.isNull(product)) {
          routingContext.response().setStatusCode(404).end("not found");
        } else {
          routingContext.response().putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(product));
        }
      } else {
        LOGGER.error(result.cause().getMessage());
        routingContext.response().setStatusCode(500).end();
      }
    });
  }

}
