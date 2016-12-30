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
import io.vertx.rxjava.ext.web.handler.JWTAuthHandler;

final class RouterProvider implements Provider<Router> {

  private final Vertx vertx;

  private Set<Handler<RoutingContext>> handlers;

  private final JWTAuthHandler authenticationHandler;

  private final FailureHandler failureHandler;

  @Inject
  public RouterProvider(final Vertx vertx, final Set<Handler<RoutingContext>> handlers, final JWTAuthHandler authenticationHandler,
      final FailureHandler failureHandler) {
    this.vertx = vertx;
    this.handlers = handlers;
    this.authenticationHandler = authenticationHandler;
    this.failureHandler = failureHandler;
  }

  @Override
  public Router get() {
    final Router router = Router.router(vertx);

    final CorsHandler corsHandler = CorsHandler.create("*")
        .allowedHeader("Content-Type")
        .allowedHeader("Authorization")
        .allowedMethod(HttpMethod.GET)
        .allowedMethod(HttpMethod.POST)
        .allowedMethod(HttpMethod.PUT)
        .allowedMethod(HttpMethod.DELETE);

    router.route().handler(corsHandler);
    router.route("/*").handler(BodyHandler.create());

    router.route("/api/*").handler(authenticationHandler);

    handlers.forEach(handler -> registerHandler(router, handler));

    router.route().failureHandler(failureHandler);
    return router;
  }

  private void registerHandler(final Router router, final Handler<RoutingContext> handler) {
    final HandlerConfiguration config = handler.getClass().getAnnotation(HandlerConfiguration.class);
    Preconditions.checkNotNull(config, "Missing HandlerConfiguration");
    router.route(config.method(), config.path()).handler(handler);
  }

}
