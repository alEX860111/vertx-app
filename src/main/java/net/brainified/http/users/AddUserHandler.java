package net.brainified.http.users;

import javax.inject.Inject;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.ext.web.RoutingContext;
import net.brainified.db.Dao;
import net.brainified.db.User;
import net.brainified.http.HandlerConfiguration;
import net.brainified.http.RoutingContextHelper;

@HandlerConfiguration(path = "/users", method = HttpMethod.POST, requiresAuthentication = true)
final class AddUserHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AddUserHandler.class);

  private RoutingContextHelper routingContextHelper;

  private final Dao<User> dao;

  @Inject
  public AddUserHandler(final RoutingContextHelper routingContextHelper, final Dao<User> dao) {
    this.routingContextHelper = routingContextHelper;
    this.dao = dao;
  }

  @Override
  public void handle(final RoutingContext routingContext) {
    final AddUserRequest addUserRequest = routingContextHelper.getBody(routingContext, AddUserRequest.class);

    final User user = new User();
    user.setUsername(addUserRequest.getUsername());
    user.setPasswordHash(Hashing.sha1().hashString(addUserRequest.getPassword(), Charsets.UTF_8).toString());
    user.setRole(addUserRequest.getRole());

    dao.add(user).subscribe(savedUser -> {
      routingContext
        .response()
        .setStatusCode(201)
        .putHeader("Content-Type", "application/json; charset=utf-8")
        .putHeader("Location", routingContext.request().absoluteURI() + "/" + savedUser.get_id())
        .end(Json.encodePrettily(savedUser));
    }, error -> {
      LOGGER.error(error.getMessage());
      routingContext.response().setStatusCode(500).end();
    });

  }

}
