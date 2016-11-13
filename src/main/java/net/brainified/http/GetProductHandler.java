package net.brainified.http;

import java.util.Objects;

import javax.inject.Inject;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.ext.web.RoutingContext;

@HandlerConfiguration(path = "/api/products/:id", method = HttpMethod.GET)
final class GetProductHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetProductHandler.class);

  private final EventBus eventBus;

  @Inject
  public GetProductHandler(final EventBus eventBus) {
    this.eventBus = eventBus;
  }

  @Override
  public void handle(final RoutingContext routingContext) {
    final String id = routingContext.request().getParam("id");
    final JsonObject params = new JsonObject();
    params.put("id", id);

    eventBus.<JsonObject>sendObservable("getProduct", params).subscribe(message -> {
      final JsonObject product = message.body();
      if (Objects.nonNull(product)) {
        routingContext.response().putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(product));
      } else {
        routingContext.response().setStatusCode(404).end("not found");
      }
    }, error -> {
      LOGGER.error(error.getMessage());
      routingContext.response().setStatusCode(500).end();
    });

  }

}
