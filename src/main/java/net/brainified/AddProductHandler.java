package net.brainified;

import javax.inject.Inject;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

final class AddProductHandler implements Handler<RoutingContext> {

  private static final String INVALID_JSON_IN_BODY = "Invalid JSON in body";

  private final ProductService service;

  @Inject
  public AddProductHandler(final ProductService service) {
    this.service = service;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    final ProductData data;
    try {
      data = Json.decodeValue(routingContext.getBodyAsString(), ProductData.class);
    } catch (final DecodeException e) {
      routingContext.response().setStatusCode(400).end(INVALID_JSON_IN_BODY);
      return;
    }

    final Future<Product> future = service.addProduct(data);
    future.setHandler(productResult -> {
      if (productResult.succeeded()) {
        final Product product = productResult.result();
        routingContext.response().setStatusCode(201)
          .putHeader("Content-Type", "application/json; charset=utf-8")
          .putHeader("Location", routingContext.request().absoluteURI() + "/" + product.getId())
          .end(Json.encodePrettily(product));
      } else {
        routingContext.response().setStatusCode(500).end();
      }
    });
  }

}
