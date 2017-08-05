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

  private final AuthHandler authenticationHandler;

  private final FailureHandler failureHandler;

  private final AuthorisationHandlerFactory authorisationHandlerFactory;

  @Inject
  public RouterProvider(final Vertx vertx, final Set<Handler<RoutingContext>> handlers, final AuthHandler authHandler,
      final FailureHandler failureHandler, final AuthorisationHandlerFactory authorisationHandlerFactory) {
    this.vertx = vertx;
    this.handlers = handlers;
    this.authenticationHandler = authHandler;
    this.failureHandler = failureHandler;
    this.authorisationHandlerFactory = authorisationHandlerFactory;
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

    handlers.forEach(handler -> registerHandler(router, handler));

    return router;
  }

  private void registerHandler(final Router router, final Handler<RoutingContext> handler) {
    final HandlerConfiguration config = handler.getClass().getAnnotation(HandlerConfiguration.class);
    Preconditions.checkNotNull(config, "Missing HandlerConfiguration");

    if (config.allowedRoles().length != 0 && !"test".equals(System.getProperty("environment"))) {
      router.route(config.method(), config.path()).handler(authenticationHandler);

      final Handler<RoutingContext> authorisationHandler = authorisationHandlerFactory.createAuthorisationHandler(config.allowedRoles());
      router.route(config.method(), config.path()).handler(authorisationHandler);
    }
    router.route(config.method(), config.path()).handler(handler);
  }

}
