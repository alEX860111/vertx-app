package net.brainified.http.login;

import javax.inject.Inject;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.rxjava.ext.auth.jwt.JWTAuth;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.brainified.http.HandlerConfiguration;

@HandlerConfiguration(path = "/login", method = HttpMethod.POST)
final class LoginHandler implements Handler<RoutingContext> {

  private static final String USER = "admin";

  private static final String PW = "qwertz";

  private static final String INVALID_JSON_IN_BODY = "Invalid JSON in body";

  private final JWTAuth jwtAuth;

  @Inject
  public LoginHandler(JWTAuth jwtAuth) {
    this.jwtAuth = jwtAuth;
  }

  @Override
  public void handle(final RoutingContext routingContext) {
    final String body = routingContext.getBodyAsString();

    LoginRequest loginRequest = null;
    try {
      loginRequest = Json.decodeValue(body, LoginRequest.class);
    } catch (final DecodeException e) {
      routingContext.response().setStatusCode(400).end(INVALID_JSON_IN_BODY);
      return;
    }

    if (USER.equals(loginRequest.getUsername()) && PW.equals(loginRequest.getPassword())) {
      final JsonObject claims = new JsonObject().put("username", loginRequest.getUsername());
      final String token = jwtAuth.generateToken(claims, new JWTOptions());
      final JsonObject response = new JsonObject().put("token", token);
      routingContext.response().putHeader("Content-Type", "application/json; charset=utf-8").end(Json.encodePrettily(response));
    } else {
      routingContext.response().setStatusCode(403).end("login failed");
    }

  }

}
