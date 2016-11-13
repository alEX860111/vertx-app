package net.brainified.http;

import javax.inject.Inject;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.ext.web.RoutingContext;

@HandlerConfiguration(path = "/api/products", method = HttpMethod.POST)
final class AddProductHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AddProductHandler.class);

  private static final String INVALID_JSON_IN_BODY = "Invalid JSON in body";

  private final EventBus eventBus;

  @Inject
  public AddProductHandler(final EventBus eventBus) {
    this.eventBus = eventBus;
  }

  @Override
  public void handle(final RoutingContext routingContext) {
    JsonObject data = null;
    try {
      data = routingContext.getBodyAsJson();
    } catch (final DecodeException e) {
      routingContext.response().setStatusCode(400).end(INVALID_JSON_IN_BODY);
      return;
    }

    final JsonObject params = new JsonObject();
    params.put("data", data);

    eventBus.<JsonObject>sendObservable("addProduct", params).subscribe(message -> {
      final JsonObject savedProduct = message.body();
      routingContext.response().setStatusCode(201).putHeader("Content-Type", "application/json; charset=utf-8")
          .putHeader("Location", routingContext.request().absoluteURI() + "/" + savedProduct.getString(("_id")))
          .end(Json.encodePrettily(savedProduct));
    }, error -> {
      LOGGER.error(error.getMessage());
      routingContext.response().setStatusCode(500).end();
    });
  }

}
