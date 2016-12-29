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

@HandlerConfiguration(path = "/api/users/:id", method = HttpMethod.GET)
final class GetUserHandler implements Handler<RoutingContext> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetUserHandler.class);

  private final Dao<User> dao;

  @Inject
  public GetUserHandler(final Dao<User> dao) {
    this.dao = dao;
  }

  @Override
  public void handle(final RoutingContext routingContext) {
    final String id = routingContext.request().getParam("id");

    dao.getById(id).subscribe(user -> {
      if (user.isPresent()) {
        routingContext.response()
          .putHeader("Content-Type", "application/json; charset=utf-8")
          .end(Json.encodePrettily(user.get()));
      } else {
        routingContext.response().setStatusCode(404).end("not found");
      }
    }, error -> {
      LOGGER.error(error.getMessage());
      routingContext.response().setStatusCode(500).end();
    });

  }

}
