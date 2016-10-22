package net.brainified;

import javax.inject.Inject;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.ext.web.RoutingContext;

final class DeleteProductHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeleteProductHandler.class);

  private final EventBus eventBus;

  @Inject
  public DeleteProductHandler(final EventBus eventBus) {
    this.eventBus = eventBus;
  }

  @Override
  public void handle(RoutingContext routingContext) {
    final String id = routingContext.request().getParam("id");
    final JsonObject params = new JsonObject();
    params.put("id", id);

    eventBus.<Long>sendObservable("deleteProduct", params).subscribe(message -> {
      final Long numDeleted = message.body();
      if (numDeleted == 0) {
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
