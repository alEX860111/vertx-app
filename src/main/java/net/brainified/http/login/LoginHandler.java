package net.brainified.http.login;

import javax.inject.Inject;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.rxjava.ext.auth.jwt.JWTAuth;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.brainified.db.Dao;
import net.brainified.db.User;
import net.brainified.http.HandlerConfiguration;
import net.brainified.http.RoutingContextHelper;

@HandlerConfiguration(path = "/login", method = HttpMethod.POST, requiresAuthentication = false)
final class LoginHandler implements Handler<RoutingContext> {

  private RoutingContextHelper routingContextHelper;

  private final JWTAuth jwtAuth;

  private final Dao<User> userDao;

  @Inject
  public LoginHandler(final RoutingContextHelper routingContextHelper, final JWTAuth jwtAuth, final Dao<User> userDao) {
    this.routingContextHelper = routingContextHelper;
    this.jwtAuth = jwtAuth;
    this.userDao = userDao;
  }

  @Override
  public void handle(final RoutingContext routingContext) {
    final LoginRequest loginRequest = routingContextHelper.getBody(routingContext, LoginRequest.class);

    userDao.getByKey("username", loginRequest.getUsername()).subscribe(userOptional -> {
      if (!userOptional.isPresent()) {
        routingContext.response().setStatusCode(403).end();
        return;
      }

      final User user = userOptional.get();
      final String passwordHash = Hashing.sha1().hashString(loginRequest.getPassword(), Charsets.UTF_8).toString();

      if (user.getPasswordHash().equals(passwordHash)) {
        final String token = createToken(user);
        final JsonObject response = new JsonObject().put("token", token);
        routingContext.response()
          .putHeader("Content-Type", "application/json; charset=utf-8")
          .end(Json.encodePrettily(response));
      } else {
        routingContext.response().setStatusCode(403).end();
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
