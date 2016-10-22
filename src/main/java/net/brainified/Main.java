package net.brainified;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;

import io.vertx.core.Verticle;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.RxHelper;
import io.vertx.rxjava.core.Vertx;
import net.brainified.db.DBVerticle;
import net.brainified.http.HttpVerticle;

public final class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    LOGGER.info("Starting App...");

    final Injector injector = Guice.createInjector(new ApplicationModule());

    final Vertx vertx = injector.getInstance(Vertx.class);
    RxHelper.deployVerticle(vertx, injector.getInstance(Key.get(Verticle.class, HttpVerticle.class)));
    RxHelper.deployVerticle(vertx, injector.getInstance(Key.get(Verticle.class, DBVerticle.class)));
    RxHelper.deployVerticle(vertx, injector.getInstance(Key.get(Verticle.class, DBVerticle.class)));
  }

}
