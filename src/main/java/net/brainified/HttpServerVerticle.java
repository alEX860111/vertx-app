package net.brainified;

import javax.inject.Inject;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;

final class HttpServerVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

  private final Vertx vertx;

  private final Router router;

  @Inject
  public HttpServerVerticle(final Vertx vertx, final Router router) {
    this.vertx = vertx;
    this.router = router;
  }

  @Override
  public void start(final Future<Void> fut) {
    vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("http.port", 8080), result -> {
      if (result.succeeded()) {
        LOGGER.info("Listening on port " + result.result().actualPort());
        fut.complete();
      } else {
        LOGGER.error(result.cause().getMessage());
        fut.fail(result.cause());
      }
    });
  }

}