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
import io.vertx.rxjava.ext.web.handler.AuthHandler;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import io.vertx.rxjava.ext.web.handler.CorsHandler;

final class RouterProvider implements Provider<Router> {

  private final Vertx vertx;

  private final Set<Handler<RoutingContext>> handlers;

  private final AuthHandler authHandler;

  private final FailureHandler failureHandler;

  @Inject
  public RouterProvider(final Vertx vertx, final Set<Handler<RoutingContext>> handlers, final AuthHandler authHandler,
      final FailureHandler failureHandler) {
    this.vertx = vertx;
    this.handlers = handlers;
    this.authHandler = authHandler;
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
    router.route().handler(BodyHandler.create());
    router.route().failureHandler(failureHandler);

    if (!"test".equals(System.getProperty("environment"))) {
      router.route("/api/*").handler(authHandler);
    }

    handlers.forEach(handler -> registerHandler(router, handler));

    return router;
  }

  private void registerHandler(final Router router, final Handler<RoutingContext> handler) {
    final HandlerConfiguration config = handler.getClass().getAnnotation(HandlerConfiguration.class);
    Preconditions.checkNotNull(config, "Missing HandlerConfiguration");
    final String path = config.requiresAuthentication() ? "/api" + config.path() : config.path();
    router.route(config.method(), path).handler(handler);
  }

}
