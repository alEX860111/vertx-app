package net.brainified.http;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.common.base.Preconditions;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import io.vertx.rxjava.ext.web.handler.CorsHandler;

final class RouterProvider implements Provider<Router> {

  private final Vertx vertx;

  private Set<Handler<RoutingContext>> handlers;

  @Inject
  public RouterProvider(final Vertx vertx, final Set<Handler<RoutingContext>> handlers) {
    this.vertx = vertx;
    this.handlers = handlers;
  }

  @Override
  public Router get() {
    final Router router = Router.router(vertx);

    final CorsHandler corsHandler = CorsHandler.create("*")
        .allowedHeader("Content-Type")
        .allowedMethod(HttpMethod.GET)
        .allowedMethod(HttpMethod.POST)
        .allowedMethod(HttpMethod.PUT)
        .allowedMethod(HttpMethod.DELETE);

    router.route().handler(corsHandler);
    router.route("/api*").handler(BodyHandler.create());

    handlers.forEach(handler -> registerHandler(router, handler));
    return router;
  }

  private void registerHandler(final Router router, final Handler<RoutingContext> handler) {
    final HandlerConfiguration config = handler.getClass().getAnnotation(HandlerConfiguration.class);
    Preconditions.checkNotNull(config, "Missing HandlerConfiguration");

    final HttpMethod method = config.method();

    switch (method) {
    case DELETE:
      router.delete(config.path()).handler(handler);
      break;
    case GET:
      router.get(config.path()).handler(handler);
      break;
    case POST:
      router.post(config.path()).handler(handler);
      break;
    case PUT:
      router.put(config.path()).handler(handler);
      break;
    default:
      throw new IllegalArgumentException("Unsupported HttpMethod: " + method);
    }
  }

}
