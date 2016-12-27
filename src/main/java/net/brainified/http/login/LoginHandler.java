package net.brainified.http.login;

import javax.inject.Inject;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.rxjava.ext.auth.jwt.JWTAuth;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.brainified.db.User;
import net.brainified.db.UserDao;
import net.brainified.http.HandlerConfiguration;

@HandlerConfiguration(path = "/login", method = HttpMethod.POST)
final class LoginHandler implements Handler<RoutingContext> {

  private static final String INVALID_JSON_IN_BODY = "Invalid JSON in body";

  private final JWTAuth jwtAuth;

  private final UserDao userDao;

  @Inject
  public LoginHandler(final JWTAuth jwtAuth, final UserDao userDao) {
    this.jwtAuth = jwtAuth;
    this.userDao = userDao;
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

    final String password = loginRequest.getPassword();

    userDao.searchUser(loginRequest.getUsername()).subscribe(userOptional -> {
      if (!userOptional.isPresent()) {
        routingContext.response().setStatusCode(403).end("login failed");
        return;
      }

      final User user = userOptional.get();
      final String passwordHash = Hashing.sha1().hashString(password, Charsets.UTF_8).toString();

      if (user.getPasswordHash().equals(passwordHash)) {
        final String token = createToken(user);
        final JsonObject response = new JsonObject().put("token", token);
        routingContext.response()
          .putHeader("Content-Type", "application/json; charset=utf-8")
          .end(Json.encodePrettily(response));
      } else {
        routingContext.response().setStatusCode(403).end("login failed");
      }

    });

  }

  private String createToken(final User user) {
    final JsonObject claims = new JsonObject()
        .put("username", user.getUsername())
        .put("role", user.getRole());
    final String token = jwtAuth.generateToken(claims, new JWTOptions());
    return token;
  }

}
