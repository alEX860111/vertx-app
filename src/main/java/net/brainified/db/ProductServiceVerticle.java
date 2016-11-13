package net.brainified.db;

import java.util.Set;

import javax.inject.Inject;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.eventbus.EventBus;
import io.vertx.rxjava.core.eventbus.Message;

public final class ProductServiceVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceVerticle.class);

  private final EventBus eventBus;

  private Set<Handler<Message<JsonObject>>> handlers;

  @Inject
  public ProductServiceVerticle(final EventBus eventBus, final Set<Handler<Message<JsonObject>>> handlers) {
    this.eventBus = eventBus;
    this.handlers = handlers;
  }

  @Override
  public void start() throws Exception {
    handlers.forEach(handler -> {
      final String address = handler.getClass().getAnnotation(EventBusHandlerConfiguration.class).address();
      eventBus.<JsonObject>consumer(address).handler(handler);
    });

    LOGGER.info(this.getClass() + " initialized.");
  }

}
