package net.brainified;

import javax.inject.Inject;

import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClientDeleteResult;
import io.vertx.rxjava.ext.web.RoutingContext;

final class DeleteProductHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteProductHandler.class);

  private final ProductService service;

  @Inject
  public DeleteProductHandler(final ProductService service) {
    this.service = service;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    final String id = routingContext.request().getParam("id");

    service.deleteProduct(id, deleteResult -> {
      if (deleteResult.succeeded()) {
        final MongoClientDeleteResult result = deleteResult.result();
        if (result.getRemovedCount() == 0) {
          routingContext.response().setStatusCode(404).end();
          return;
        }
        routingContext.response().setStatusCode(204).end();
      } else {
        LOGGER.error(deleteResult.cause().getMessage());
        routingContext.response().setStatusCode(500).end();
      }
    });
  }

}
