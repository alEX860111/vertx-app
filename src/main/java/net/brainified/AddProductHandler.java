package net.brainified;

import java.time.Instant;

import javax.inject.Inject;

import io.vertx.core.Handler;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;

final class AddProductHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AddProductHandler.class);

  private static final String INVALID_JSON_IN_BODY = "Invalid JSON in body";

  private final ProductService service;

  @Inject
  public AddProductHandler(final ProductService service) {
    this.service = service;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    JsonObject data = null;
    try {
      data = routingContext.getBodyAsJson();
    } catch (final DecodeException e) {
      routingContext.response().setStatusCode(400).end(INVALID_JSON_IN_BODY);
    }

    final JsonObject product = new JsonObject();
    product.put("data", data);
    product.put("createdAt", Instant.now());

    service.addProduct(product, result -> {
      if (result.succeeded()) {
        final String id = result.result();
        routingContext.response().setStatusCode(201)
          .putHeader("Content-Type", "application/json; charset=utf-8")
          .putHeader("Location", routingContext.request().absoluteURI() + "/" + id)
          .end(Json.encodePrettily(product));
      } else {
        LOGGER.error(result.cause().getMessage());
        routingContext.response().setStatusCode(500).end();
      }
    });
  }

}
