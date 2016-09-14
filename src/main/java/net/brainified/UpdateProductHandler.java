package net.brainified;

import java.util.Objects;

import javax.inject.Inject;

import com.google.common.base.MoreObjects;
import com.google.common.primitives.Ints;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

final class UpdateProductHandler implements Handler<RoutingContext> {

  private static final String INVALID_JSON_IN_BODY = "Invalid JSON in body";

  private final ProductService service;

  @Inject
  public UpdateProductHandler(final ProductService service) {
    this.service = service;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    final Integer id = Ints.tryParse(MoreObjects.firstNonNull(routingContext.request().getParam("id"), ""));
    if (Objects.isNull(id)) {
      routingContext.response().setStatusCode(400).end("Invalid id");
      return;
    }

    final ProductData data;
    try {
      data = Json.decodeValue(routingContext.getBodyAsString(), ProductData.class);
    } catch (final DecodeException e) {
      routingContext.response().setStatusCode(400).end(INVALID_JSON_IN_BODY);
      return;
    }

    final Future<Product> future = service.updateProduct(id, data);
    future.setHandler(productResult -> {
      if (productResult.succeeded()) {
        final Product product = productResult.result();
        routingContext.response().putHeader("content-type", "application/json; charset=utf-8").end(Json.encodePrettily(product));
      } else {
        routingContext.response().setStatusCode(404).end();
      }
    });
  }

}
