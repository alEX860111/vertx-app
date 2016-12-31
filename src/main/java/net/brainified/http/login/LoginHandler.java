package net.brainified.http.login;

import javax.inject.Inject;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.brainified.http.HandlerConfiguration;
import net.brainified.http.RoutingContextHelper;

@HandlerConfiguration(path = "/login", method = HttpMethod.POST, requiresAuthentication = false)
final class LoginHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoginHandler.class);

  private final RoutingContextHelper routingContextHelper;

  private LoginService loginService;

  @Inject
  public LoginHandler(final RoutingContextHelper routingContextHelper, final LoginService loginService) {
    this.routingContextHelper = routingContextHelper;
    this.loginService = loginService;
  }

  @Override
  public void handle(final RoutingContext routingContext) {
    final LoginRequest loginRequest = routingContextHelper.getBody(routingContext, LoginRequest.class);

    loginService.login(loginRequest).subscribe(loginResponseOptional -> {
      if (loginResponseOptional.isPresent()) {
        routingContext.response()
        .putHeader("Content-Type", "application/json; charset=utf-8")
        .end(Json.encodePrettily(loginResponseOptional.get()));
      } else {
        routingContext.response().setStatusCode(403).end();
      }
    }, error -> {
      LOGGER.error(error.getMessage(), error);
      routingContext.response().setStatusCode(500).end();
      });

  }

}
