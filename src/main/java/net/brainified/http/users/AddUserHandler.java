package net.brainified.http.users;

import javax.inject.Inject;

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
import net.brainified.http.login.HashService;

@HandlerConfiguration(path = "/users", method = HttpMethod.POST, requiresAuthentication = true)
final class AddUserHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(AddUserHandler.class);

  private final RoutingContextHelper routingContextHelper;

  private final HashService hashService;

  private final Dao<User> dao;

  @Inject
  public AddUserHandler(final RoutingContextHelper routingContextHelper, final HashService hashService, final Dao<User> dao) {
    this.routingContextHelper = routingContextHelper;
    this.hashService = hashService;
    this.dao = dao;
  }

  @Override
  public void handle(final RoutingContext routingContext) {
    final AddUserRequest addUserRequest = routingContextHelper.getBody(routingContext, AddUserRequest.class);

    final User user = new User();
    user.setUsername(addUserRequest.getUsername());
    user.setPasswordHash(hashService.hash(addUserRequest.getPassword()));
    user.setRole(addUserRequest.getRole());

    dao.add(user).subscribe(savedUser -> {
      routingContext
        .response()
        .setStatusCode(201)
        .putHeader("Content-Type", "application/json; charset=utf-8")
        .putHeader("Location", routingContext.request().absoluteURI() + "/" + savedUser.get_id())
        .end(Json.encodePrettily(savedUser));
    }, error -> {
      LOGGER.error(error.getMessage(), error);
      routingContext.response().setStatusCode(500).end();
    });

  }

}
