package net.brainified;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import com.google.common.base.MoreObjects;
import com.google.common.primitives.Ints;

import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.ext.web.RoutingContext;

final class GetProductListHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetProductListHandler.class);

  private static final String PAGE_DEFAULT = "1";
  private static final int PAGE_MIN = 1;

  private static final String PER_PAGE_DEFAULT = "10";
  private static final int PER_PAGE_MIN = 1;

  private final ProductService service;

  @Inject
  public GetProductListHandler(final ProductService service) {
    this.service = service;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    final Integer page = Ints.tryParse(MoreObjects.firstNonNull(routingContext.request().getParam("page"), PAGE_DEFAULT));
    if (Objects.isNull(page) || page < PAGE_MIN) {
      routingContext.response().setStatusCode(400).end("page must be greater than or equal to " + PAGE_MIN);
      return;
    }

    final Integer perpage = Ints.tryParse(MoreObjects.firstNonNull(routingContext.request().getParam("perpage"), PER_PAGE_DEFAULT));
    if (Objects.isNull(perpage) || perpage < PER_PAGE_MIN) {
      routingContext.response().setStatusCode(400).end("perpage must be greater than or equal to " + PER_PAGE_MIN);
      return;
    }

    service.getProductCount(countResult -> {
      if (countResult.succeeded()) {
        service.getProductList(page, perpage, productsResult -> {
          if (productsResult.succeeded()) {
            final JsonObject container = new JsonObject();
            final List<JsonObject> products = productsResult.result();
            container.put("products", products);
            container.put("numberOfProducts", countResult.result());
            routingContext.response().putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(container));
          } else {
            LOGGER.error(productsResult.cause().getMessage());
            routingContext.response().setStatusCode(500).end();
          }
        });
      } else {
        LOGGER.error(countResult.cause().getMessage());
        routingContext.response().setStatusCode(500).end();
      }
    });

  }

}
