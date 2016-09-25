package net.brainified;

import javax.inject.Inject;

import io.vertx.core.Handler;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClientUpdateResult;
import io.vertx.ext.web.RoutingContext;

final class UpdateProductHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateProductHandler.class);

  private static final String INVALID_JSON_IN_BODY = "Invalid JSON in body";

  private final ProductService service;

  @Inject
  public UpdateProductHandler(final ProductService service) {
    this.service = service;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    final String id = routingContext.request().getParam("id");

    JsonObject data = null;
    try {
      data = routingContext.getBodyAsJson();
    } catch (final DecodeException e) {
      routingContext.response().setStatusCode(400).end(INVALID_JSON_IN_BODY);
    }

    service.updateProduct(id, data, updateResult -> {
      if (updateResult.succeeded()) {
        final MongoClientUpdateResult result = updateResult.result();
        if (result.getDocModified() == 0) {
          routingContext.response().setStatusCode(404).end();
          return;
        }
        routingContext.response().setStatusCode(204).end();
      } else {
        LOGGER.error(updateResult.cause().getMessage());
        routingContext.response().setStatusCode(500).end();
      }
    });
  }

}
