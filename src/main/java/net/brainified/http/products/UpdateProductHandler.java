package net.brainified.http.products;

import javax.inject.Inject;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.brainified.db.ProductDao;
import net.brainified.db.ProductData;
import net.brainified.http.HandlerConfiguration;

@HandlerConfiguration(path = "/api/products/:id", method = HttpMethod.PUT)
final class UpdateProductHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateProductHandler.class);

  private static final String INVALID_JSON_IN_BODY = "Invalid JSON in body";

  private final ProductDao dao;

  @Inject
  public UpdateProductHandler(final ProductDao dao) {
    this.dao = dao;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    final String body = routingContext.getBodyAsString();

    ProductData data = null;
    try {
      data = Json.decodeValue(body, ProductData.class);
    } catch (final DecodeException e) {
      routingContext.response().setStatusCode(400).end(INVALID_JSON_IN_BODY);
      return;
    }

    final String id = routingContext.request().getParam("id");

    dao.updateProduct(id, data).subscribe(numModified -> {
      if (numModified == 0) {
        routingContext.response().setStatusCode(404).end();
      } else {
        routingContext.response().setStatusCode(204).end();
      }
    }, error -> {
      LOGGER.error(error.getMessage());
      routingContext.response().setStatusCode(500).end();
    });
  }

}
