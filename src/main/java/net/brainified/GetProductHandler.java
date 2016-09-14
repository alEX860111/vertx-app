package net.brainified;

import java.util.Objects;

import javax.inject.Inject;

import com.google.common.base.MoreObjects;
import com.google.common.primitives.Ints;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

final class GetProductHandler implements Handler<RoutingContext> {

  private final ProductService service;

  @Inject
  public GetProductHandler(final ProductService service) {
    this.service = service;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    final Integer id = Ints.tryParse(MoreObjects.firstNonNull(routingContext.request().getParam("id"), ""));
    if (Objects.isNull(id)) {
      routingContext.response().setStatusCode(400).end("Invalid id");
      return;
    }

    final Future<Product> future = service.getProduct(id);
    future.setHandler(productResult -> {
      if (productResult.succeeded()) {
        final Product product = productResult.result();
        routingContext.response().putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(product));
      } else {
        routingContext.response().setStatusCode(404).end();
      }
    });
  }

}
