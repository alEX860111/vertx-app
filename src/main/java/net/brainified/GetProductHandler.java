package net.brainified;

import javax.inject.Inject;

import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.ext.web.RoutingContext;

final class GetProductHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetProductHandler.class);

  private final ProductService service;

  @Inject
  public GetProductHandler(final ProductService service) {
    this.service = service;
  }

  @Override
  public void handle(final RoutingContext routingContext) {
    final String id = routingContext.request().getParam("id");

    service.getProduct(id).subscribe(product -> {
      if (product.isPresent()) {
        routingContext.response().putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(product.get()));
      } else {
        routingContext.response().setStatusCode(404).end("not found");
      }
    }, error -> {
      LOGGER.error(error.getMessage());
      routingContext.response().setStatusCode(500).end();
    });

  }

}
