package net.brainified.http;

import java.util.Objects;

import javax.inject.Inject;

import com.google.common.base.MoreObjects;
import com.google.common.primitives.Ints;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.ext.web.RoutingContext;

@HandlerConfiguration(path = "/api/products", method = HttpMethod.GET)

final class GetProductListHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetProductListHandler.class);

  private static final String PAGE_DEFAULT = "1";
  private static final int PAGE_MIN = 1;

  private static final String PER_PAGE_DEFAULT = "10";
  private static final int PER_PAGE_MIN = 1;

  private final EventBus eventBus;

  @Inject
  public GetProductListHandler(final EventBus eventBus) {
    this.eventBus = eventBus;
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

    eventBus.<Long>sendObservable("getProductCount", new JsonObject()).subscribe(countMessage -> {
      final Long count = countMessage.body();

      final JsonObject params = new JsonObject();
      params.put("page", page);
      params.put("perpage", perpage);

      eventBus.<JsonArray>sendObservable("getProductList", params).subscribe(productsMessage -> {
        final JsonArray products = productsMessage.body();
        final JsonObject container = new JsonObject();
        container.put("products", products);
        container.put("numberOfProducts", count);
        routingContext.response()
          .putHeader("Content-Type", "application/json; charset=utf-8")
          .end(Json.encodePrettily(container));
      }, error -> {
        LOGGER.error(error.getMessage());
        routingContext.response().setStatusCode(500).end();
      });
    }, error -> {
      LOGGER.error(error.getMessage());
      routingContext.response().setStatusCode(500).end();
    });

  }

}
