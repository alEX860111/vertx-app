package net.brainified;

import javax.inject.Inject;

import com.google.common.collect.Sets;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

final class HttpServerVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

  private final Vertx vertx;

  private final Handler<RoutingContext> getProductListHandler;

  private final Handler<RoutingContext> getProductHandler;

  private final Handler<RoutingContext> addProductHandler;

  private final Handler<RoutingContext> updateProductHandler;

  private final Handler<RoutingContext> deleteProductHandler;

  @Inject
  public HttpServerVerticle(
      final Vertx vertx,
      @GetProductList final Handler<RoutingContext> getProductListHandler,
      @GetProduct final Handler<RoutingContext> getProductHandler,
      @AddProduct final Handler<RoutingContext> addProductHandler,
      @UpdateProduct final Handler<RoutingContext> updateProductHandler,
      @DeleteProduct final Handler<RoutingContext> deleteProductHandler) {
    this.vertx = vertx;
    this.getProductListHandler = getProductListHandler;
    this.getProductHandler = getProductHandler;
    this.addProductHandler = addProductHandler;
    this.updateProductHandler = updateProductHandler;
    this.deleteProductHandler = deleteProductHandler;  }

  @Override
  public void start(final Future<Void> fut) {
    final Router router = createRouter();

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

  private Router createRouter() {
    final Router router = Router.router(vertx);
    router.route().handler(CorsHandler.create("*")
        .allowedHeader("Content-Type")
        .allowedMethods(Sets.newHashSet(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)));

    router.route("/api/products*").handler(BodyHandler.create());

    router.get("/api/products").handler(getProductListHandler);
    router.post("/api/products").handler(addProductHandler);
    router.get("/api/products/:id").handler(getProductHandler);
    router.put("/api/products/:id").handler(updateProductHandler);
    router.delete("/api/products/:id").handler(deleteProductHandler);
    return router;
  }

}