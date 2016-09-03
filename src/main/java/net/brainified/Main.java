package net.brainified;

import com.google.inject.Guice;
import com.google.inject.Injector;

import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public final class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

  public static void main(String[] args) {
    LOGGER.info("Starting App...");
    final Injector injector = Guice.createInjector(new ApplicationModule());
    final Vertx vertx = injector.getInstance(Vertx.class);
    vertx.deployVerticle(injector.getInstance(HttpServerVerticle.class));
  }

}
