package net.brainified;

import javax.inject.Inject;

import com.google.common.collect.Sets;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

final class HttpServerVerticle extends AbstractVerticle {

  private final Vertx vertx;

  private final ProductHandler handler;

  @Inject
  public HttpServerVerticle(final Vertx vertx, final ProductHandler handler) {
    this.vertx = vertx;
    this.handler = handler;
  }

  @Override
  public void start(final Future<Void> fut) {
    final Router router = createRouter();

    vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("http.port", 8080), result -> {
      if (result.succeeded()) {
        fut.complete();
      } else {
        fut.fail(result.cause());
      }
    });
  }

  private Router createRouter() {
    final Router router = Router.router(vertx);
    router.route().handler(CorsHandler.create("http://localhost:3000")
        .allowedMethods(Sets.newHashSet(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)));

    router.get("/api/products").handler(handler::getProducts);
    router.route("/api/products*").handler(BodyHandler.create());
    router.post("/api/products").handler(handler::addProduct);
    router.get("/api/products/:id").handler(handler::getProduct);
    router.put("/api/products/:id").handler(handler::updateProduct);
    router.delete("/api/products/:id").handler(handler::deleteProduct);
    return router;
  }

}